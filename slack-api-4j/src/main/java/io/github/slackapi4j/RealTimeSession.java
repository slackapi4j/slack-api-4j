package io.github.slackapi4j;

/*-
 * #%L
 * slack-api-4j
 * %%
 * Copyright (C) 2018 - 2019 SlackApi4J
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.slackapi4j.eventlisteners.RealTimeListener;
import io.github.slackapi4j.events.ConversationEvent;
import io.github.slackapi4j.events.MessageEvent;
import io.github.slackapi4j.events.RealTimeEvent;
import io.github.slackapi4j.events.UserConversationEvent;
import io.github.slackapi4j.exceptions.SlackRtException;
import io.github.slackapi4j.internal.Utilities;
import io.github.slackapi4j.objects.Conversation;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.Message.MessageType;
import io.github.slackapi4j.objects.ObjectID;
import io.github.slackapi4j.objects.User;
import lombok.Getter;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public class RealTimeSession implements Closeable {
  private final Gson gson;
  @Getter
  private final SlackApi api;
  private final ExecutorService service;
  private final Set<User> users;
  private final Set<Conversation> channels;
  private final Map<Future<Conversation>, ObjectID> futureConversations;
  private final Map<String, User> userMap;
  private final Map<String, Conversation> channelMap;
  private final Map<ObjectID, User> userIdMap;
  private final Map<ObjectID, Conversation> channelIdMap;
  private final Set<Conversation> joined;
  private final List<RealTimeListener> listeners;
  private final Map<Integer, Message> pendingMessages;
  @Getter
  private User self;
  private WebSocketClient client;
  private Session session;
  private int nextMessageId = 1;
  private boolean needJoinConfirm;

  RealTimeSession(final JsonObject object, final SlackApi main) throws IOException {
    api = main;
    gson = api.getGson();
    service = Executors.newCachedThreadPool();
    listeners = Lists.newArrayList();
    pendingMessages = Maps.newHashMap();
    futureConversations = Maps.newHashMap();

    final JsonArray channels = object.getAsJsonArray("channels");
    final JsonArray users = object.getAsJsonArray("users");
    final JsonObject self = object.getAsJsonObject("self");

    this.channels = Sets.newHashSetWithExpectedSize(channels.size());
    channelMap = Maps.newHashMapWithExpectedSize(channels.size());
    channelIdMap = Maps.newHashMapWithExpectedSize(channels.size());
    joined = Sets.newHashSetWithExpectedSize(channels.size());
    this.users = Sets.newHashSetWithExpectedSize(users.size());
    userMap = Maps.newHashMapWithExpectedSize(users.size());
    userIdMap = Maps.newHashMapWithExpectedSize(users.size());

    load(channels, users, self);
    initWebSocket(object.get("url").getAsString());
  }

  /**
   * Add a listener to the session.
   *
   * @param listener the Listener to add
   */
  public void addListener(final RealTimeListener listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }
  }

  /**
   * Remove a listener from this session.
   *
   * @param listener the listener to remove
   */
  public void removeListener(final RealTimeListener listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
  }

  private void postLogin() {
    synchronized (listeners) {
      for (final RealTimeListener listener : listeners) {
        listener.onLoginComplete();
      }
    }
  }

  private void postClose() {
    synchronized (listeners) {
      for (final RealTimeListener listener : listeners) {
        listener.onClose();
      }
    }
  }

  private void postError(final SlackRtException ex) {
    synchronized (listeners) {
      for (final RealTimeListener listener : listeners) {
        listener.onError(ex);
      }
    }
  }

  private void postEvent(final RealTimeEvent event) {
    synchronized (listeners) {
      for (final RealTimeListener listener : listeners) {
        listener.onEvent(event);
      }
    }
  }

  private void load(final JsonArray channels, final JsonArray users, final JsonObject self) {
    final ObjectID selfId = new ObjectID(self.get("id").getAsString());
    // Load users
    for (final JsonElement user : users) {
      try {
        final User loaded = gson.fromJson(user, User.class);
        if (loaded.getId().equals(selfId)) {
          this.self = loaded;
        }
        addUser(loaded);
      } catch (final Throwable e) {
        System.err.println("Unable to load user " + user);
        e.printStackTrace();
      }
    }
    // Load Conversations
    // Load channels


    for (final JsonElement channel : channels) {
      final Conversation loaded = gson.fromJson(channel, Conversation.class);
      updateChannel(loaded);
    }
  }

  private void initWebSocket(final String url) throws IOException {
    try {
      final URI uri = new URI(url);
      needJoinConfirm = true;
      client = new WebSocketClient(new SslContextFactory.Client());
      client.start();
      final Future<Session> future = client.connect(new SocketClient(), uri);

      session = future.get(client.getConnectTimeout() + 1000, TimeUnit.MILLISECONDS);

      nextMessageId = 1;
    } catch (final URISyntaxException e) {
      // Should never happen
    } catch (final IOException e) {
      throw e;
    } catch (final InterruptedException e) {
      throw new IOException(e.getCause());
    } catch (final ExecutionException e) {
      if (e.getCause() instanceof IOException) {
        throw (IOException) e.getCause();
      } else {
        throw new IOException(e.getCause());
      }
    } catch (final TimeoutException e) {
      throw new SocketTimeoutException(); // Probably wont
    } catch (final Exception e) {     // Sigh, couldn't they pick a more specific one? :/
      throw new IOException(e);
    }
  }

  private void addUser(final User user) {
    users.add(user);
    userMap.put(user.getName().toLowerCase(), user);
    userIdMap.put(user.getId(), user);
  }

  public Set<User> getUsers() {
    return Collections.unmodifiableSet(users);
  }

  public User getUser(final String name) {
    return userMap.get(name.toLowerCase());
  }

  public User getUserById(final ObjectID id) {
    return userIdMap.get(id);
  }

  private void addChannel(@Nonnull final Conversation channel) {
    channels.add(channel);
    channelMap.put(channel.getName().toLowerCase(), channel);
    channelIdMap.put(channel.getId(), channel);
    if (channel.isMember()) {
      joined.add(channel);
    } else {
      joined.remove(channel);
    }
  }

  /**
   * This should be the only method used to remove a channel.
   *
   * @param chanID the channel Id to remove
   */
  private void removeChannel(@Nonnull final ObjectID chanID) {
    final Conversation out = channelIdMap.remove(chanID);
    if (out != null) {
      channels.remove(out);
      channelMap.remove(out.getName());
      joined.remove(out);
    } else {
      synchronized (channelMap) {
        for (final Conversation channel : channelMap.values()) {
          if (channel.getId().equals(chanID)) {
            channelMap.remove(channel.getName());
          }
        }
      }
    }
  }

  private void removeChannel(@Nonnull final Conversation channel) {
    removeChannel(channel.getId());
  }

  /**
   * This method may add a relatively incomplete object to the channel list
   * but the deign is such that it is update by a call to then api to get a full object.
   *
   * @param channel the channel to update
   */
  private void updateChannel(final Conversation channel) {
    if (channel != null) {
      addChannel(channel);
      final Future<Conversation> future =
          service.submit(() ->
              api.getConversations().getConversation(channel.getId().toString()));
      futureConversations.put(future, channel.getId());
    }
    checkFutureConversations();

  }

  private void updateChannel(final ObjectID channelID) {
    final Future<Conversation> future =
        service.submit(() ->
            api.getConversations().getConversation(channelID.toString()));
    futureConversations.put(future, channelID);
    checkFutureConversations();
  }

  private void checkFutureConversations() {
    for (final Map.Entry<Future<Conversation>, ObjectID> f : futureConversations.entrySet()) {
      if (f.getKey().isDone()) {
        try {
          final Conversation c = f.getKey().get();
          if (c != null) {
            addChannel(c);
          } else {
            removeChannel(f.getValue());
          }
        } catch (final InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }

      }
    }
  }

  public Set<Conversation> getAllChannels() {
    return Collections.unmodifiableSet(channels);
  }

  public Conversation getChannel(final String name) {
    return channelMap.get(name.toLowerCase());
  }

  public Conversation getChannelById(final ObjectID id) {
    return channelIdMap.get(id);
  }

  private int appendId(final JsonObject object) {
    final int id = nextMessageId++;
    object.addProperty("id", id);
    return id;
  }

  public void sendMessage(final String text, final Conversation channel) {
    sendMessage(new Message(text, channel));
  }

  /**
   * Send a message.
   *
   * @param message the message to send
   */
  public void sendMessage(final Message message) {
    final JsonObject object = gson.toJsonTree(message).getAsJsonObject();
    final int id = appendId(object);
    pendingMessages.put(id, message);
    send(object);
  }

  private void send(final JsonObject object) {
    session.getRemote().sendStringByFuture(gson.toJson(object));
  }

  public boolean isOpen() {
    return client != null && client.isRunning();
  }

  @Override
  public void close() {
    try {
      client.stop();
      client = null;
      service.shutdown();
      futureConversations.forEach((conversationFuture, objectID) ->
          conversationFuture.cancel(true));
      futureConversations.clear();
    } catch (final Exception e) {
      // Its shutting down, I dont care
    }
  }

  private SlackRtException makeException(final JsonObject object) {
    if (object.has("error")) {
      final JsonObject error = object.getAsJsonObject("error");
      return new SlackRtException(error.get("code").getAsInt(), error.get("msg").getAsString());
    }
    return null;
  }

  private void onReply(final JsonObject reply) {
    final int replyId = reply.get("reply_to").getAsInt();
    // TODO: Handle other types of replies
    final Message message = pendingMessages.remove(replyId);

    if (reply.get("ok").getAsBoolean()) {
      postEvent(new MessageEvent(self, message, message.getSubtype()));
    } else {
      final SlackRtException exception = makeException(reply);
      if (exception != null) {
        postError(exception);
      }
      // Not sure what to do it no error
    }
  }

  private void onEvent(final JsonObject event) {
    final String type = Utilities.getAsString(event.get("type"));
    if (type == null) {
      return;
    }
    // Handle login first
    if (needJoinConfirm) {
      if ("hello".equals(type)) {
        needJoinConfirm = false;
        postLogin();
      } else {
        postError(makeException(event));
        close();
        return;
      }

      return;
    }

    final RealTimeEvent newEvent;
    final Conversation conversation;
    final ObjectID conID;
    final ObjectID userID;
    switch (type) {
      case "message": {
        // A message from a previous session
        if (event.has("reply_to")) {
          return;
        }
        final Message message = gson.fromJson(event, Message.class);
        final User user;
        if (message.getSubtype() == MessageType.Edit) {
          user = getUserById(message.getEditUserId());
        } else {
          user = getUserById(message.getUserId());
        }
        newEvent = new MessageEvent(user, message, message.getSubtype());
        break;
      }
      case "channel_created":
      case "im_created":
        conversation = gson.fromJson(event.get("channel"), Conversation.class);
        newEvent = new ConversationEvent(conversation.getId(), ConversationEvent.EventType.Create);
        updateChannel(conversation);
        break;
      case "channel_joined":
      case "group_joined":
        conversation = gson.fromJson(event.get("channel"), Conversation.class);
        conID = conversation.getId();
        newEvent = new ConversationEvent(conID, ConversationEvent.EventType.Join);
        updateChannel(conversation);
        break;
      case "group_deleted":
      case "channel_deleted":
        conID = new ObjectID(event.get("channel").toString());
        newEvent = new ConversationEvent(conID, ConversationEvent.EventType.Delete);
        updateChannel(conID);
        break;
      case "channel_left":
      case "group_left":
        conID = new ObjectID(event.get("channel").toString());
        newEvent = new ConversationEvent(conID, ConversationEvent.EventType.Join);
        updateChannel(conID);
        break;
      case "channel_rename":
      case "group_rename":
        conversation = gson.fromJson(event.get("channel"), Conversation.class);
        newEvent = new ConversationEvent(conversation.getId(), ConversationEvent.EventType.Rename);
        final Conversation oldObject = channelIdMap.get(conversation.getId());
        channelMap.remove(oldObject.getName());
        channelIdMap.remove(oldObject.getId());
        updateChannel(conversation);
        break;
      case "channel_archive":
      case "group_archive":
        conID = new ObjectID(event.get("channel").toString());
        userID = new ObjectID(event.get("user").toString());
        newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Archive);
        updateChannel(conID);
        break;
      case "channel_unarchive":
      case "group_unarchive":
        conID = new ObjectID(event.get("channel").toString());
        userID = new ObjectID(event.get("user").toString());
        newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Unarchive);
        updateChannel(conID);
        break;
      case "group_open":
      case "im_open":
        conID = new ObjectID(event.get("channel").toString());
        userID = new ObjectID(event.get("user").toString());
        newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Open);
        updateChannel(conID);
        break;
      case "group_close":
      case "im_close":
        conID = new ObjectID(event.get("channel").toString());
        userID = new ObjectID(event.get("user").toString());
        newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Close);
        updateChannel(conID);
        break;
      case "user_change":
        final User user = gson.fromJson(event.get("user"), User.class);
        addUser(user);
        newEvent = null;
        break;
      case "error":
        postError(makeException(event));
        return;
      case "team_join":
      case "group_history_changed":
      case "im_history_changed":
      case "channel_history_changed":
      default:
        newEvent = null;
        break;
    }
    if (newEvent != null) {
      postEvent(newEvent);
    }
  }

  private class SocketClient implements WebSocketListener {
    /**
     * A WebSocket binary frame has been received.
     *
     * @param payload the raw payload array received
     * @param offset  the offset in the payload array where the data starts
     * @param len     the length of bytes in the payload
     */
    @Override
    public void onWebSocketBinary(final byte[] payload, final int offset, final int len) {
    }

    /**
     * A Close Event was received.
     * The underlying Connection will be considered closed at this point.
     * @param statusCode the close status code.
     *                   (See {@link org.eclipse.jetty.websocket.api.StatusCode})
     * @param reason the optional reason for the close.
     */

    @Override
    public void onWebSocketClose(final int statusCode, final String reason) {
      postClose();
    }

    @Override
    public void onWebSocketConnect(final Session session) {
      RealTimeSession.this.session = session;
    }

    /**
     * A WebSocket exception has occurred.
     * This is a way for the internal implementation to notify of exceptions occurred during the
     * processing of websocket. Usually this occurs from bad / malformed incoming packets.
     * (example: bad UTF8 data, frames that are too big, violations of the spec)
     * This will result in the {@link Session} being closed by the implementing side.
     * @param cause the error that occurred.
     */

    @Override
    public void onWebSocketError(final Throwable cause) {
      cause.printStackTrace();
    }

    /**
     * A WebSocket Text frame was received.
     * @param message the message
     */

    @Override
    public void onWebSocketText(final String message) {
      final JsonObject event = gson.fromJson(message, JsonElement.class).getAsJsonObject();
      if (event.has("ok")) {
        onReply(event);
      } else {
        onEvent(event);
      }
    }
  }
}
