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

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

import io.github.slackapi4j.eventListeners.RealTimeListener;
import io.github.slackapi4j.events.ConversationEvent;
import io.github.slackapi4j.events.MessageEvent;
import io.github.slackapi4j.events.RealTimeEvent;
import io.github.slackapi4j.events.UserConversationEvent;
import io.github.slackapi4j.exceptions.SlackRTException;
import io.github.slackapi4j.internal.Utilities;
import io.github.slackapi4j.objects.Conversation;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.ObjectID;
import io.github.slackapi4j.objects.User;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import lombok.Getter;
import io.github.slackapi4j.objects.Message.MessageType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public class RealTimeSession implements Closeable
{
    private final Gson gson;
    @Getter
    private final SlackAPI api;
    private final ExecutorService service;

    @Getter
    private User self;
    private final Set<User> users;
    private final Set<Conversation> channels;
    private final Map<Future<Conversation>, ObjectID> futureConversations;
    private final Map<String, User> userMap;
    private final Map<String, Conversation> channelMap;

    private final Map<ObjectID, User> userIdMap;
    private final Map<ObjectID, Conversation> channelIdMap;
    private final Set<Conversation> joined;

    private WebSocketClient client;
    private Session session;
    private int nextMessageId = 1;
    private boolean needJoinConfirm;

    private final List<RealTimeListener> listeners;

    private final Map<Integer, Message> pendingMessages;

    RealTimeSession(final JsonObject object, final SlackAPI main) throws IOException
    {
        this.api = main;
        this.gson = this.api.getGson();
        this.service = Executors.newCachedThreadPool();
        this.listeners = Lists.newArrayList();
        this.pendingMessages = Maps.newHashMap();
        this.futureConversations = Maps.newHashMap();

        final JsonArray channels = object.getAsJsonArray("channels");
        final JsonArray users = object.getAsJsonArray("users");
        final JsonObject self = object.getAsJsonObject("self");

        this.channels = Sets.newHashSetWithExpectedSize(channels.size());
        this.channelMap = Maps.newHashMapWithExpectedSize(channels.size());
        this.channelIdMap = Maps.newHashMapWithExpectedSize(channels.size());
        this.joined = Sets.newHashSetWithExpectedSize(channels.size());
        this.users = Sets.newHashSetWithExpectedSize(users.size());
        this.userMap = Maps.newHashMapWithExpectedSize(users.size());
        this.userIdMap = Maps.newHashMapWithExpectedSize(users.size());

        this.load(channels, users, self);
        this.initWebSocket(object.get("url").getAsString());
    }

    public void addListener(final RealTimeListener listener)
    {
        synchronized (this.listeners)
        {
            this.listeners.add(listener);
        }
    }

    public void removeListener(final RealTimeListener listener)
    {
        synchronized (this.listeners)
        {
            this.listeners.remove(listener);
        }
    }

    private void postLogin()
    {
        synchronized (this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onLoginComplete();
            }
        }
    }

    private void postClose()
    {
        synchronized (this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onClose();
            }
        }
    }

    private void postError(final SlackRTException ex)
    {
        synchronized (this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onError(ex);
            }
        }
    }

    private void postEvent(final RealTimeEvent event)
    {
        synchronized (this.listeners)
        {
            for (final RealTimeListener listener : this.listeners)
            {
                listener.onEvent(event);
            }
        }
    }

    private void load(final JsonArray channels, final JsonArray users, final JsonObject self)
    {
        final ObjectID selfId = new ObjectID(self.get("id").getAsString());
        // Load users
        for (final JsonElement user : users)
        {
            try
            {
                final User loaded = this.gson.fromJson(user, User.class);
                if (loaded.getId().equals(selfId)) {
                    this.self = loaded;
                }
                this.addUser(loaded);
            } catch (final Throwable e)
            {
                System.err.println("Unable to load user " + user);
                e.printStackTrace();
            }
        }
        // Load Conversations
        // Load channels


        for (final JsonElement channel : channels)
        {
            final Conversation loaded = this.gson.fromJson(channel, Conversation.class);
            this.updateChannel(loaded);
        }
    }

    private void initWebSocket(final String url) throws IOException
    {
        try
        {
            final URI uri = new URI(url);
            this.needJoinConfirm = true;
            this.client = new WebSocketClient(new SslContextFactory.Client());
            this.client.start();
            final Future<Session> future = this.client.connect(new SocketClient(), uri);

            this.session = future.get(this.client.getConnectTimeout() + 1000, TimeUnit.MILLISECONDS);

            this.nextMessageId = 1;
        } catch (final URISyntaxException e)
        {
            // Should never happen
        } catch (final IOException e)
        {
            throw e;
        } catch (final InterruptedException ignored)
        {

        } catch (final ExecutionException e)
        {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            } else {
                throw new IOException(e.getCause());
            }
        } catch (final TimeoutException e)
        {
            // Probably wont
            throw new SocketTimeoutException();
        }
        // Sigh, couldnt they pick a more specific one? :/
        catch (final Exception e)
        {
            throw new IOException(e);
        }
    }

    private void addUser(final User user)
    {
        this.users.add(user);
        this.userMap.put(user.getName().toLowerCase(), user);
        this.userIdMap.put(user.getId(), user);
    }

    public Set<User> getUsers()
    {
        return Collections.unmodifiableSet(this.users);
    }

    public User getUser(final String name)
    {
        return this.userMap.get(name.toLowerCase());
    }

    public User getUserById(final ObjectID id)
    {
        return this.userIdMap.get(id);
    }

    private void addChannel(@Nonnull final Conversation channel) {
        this.channels.add(channel);
        this.channelMap.put(channel.getName().toLowerCase(), channel);
        this.channelIdMap.put(channel.getId(), channel);
        if (channel.isMember()) {
            this.joined.add(channel);
        } else {
            this.joined.remove(channel);
        }
    }

    /**
     * This should be the only method used to remove a channel
     *
     * @param chanID the channel Id to remove
     */
    private void removeChannel(@Nonnull final ObjectID chanID) {
        final Conversation out = this.channelIdMap.remove(chanID);
        if (out != null) {
            this.channels.remove(out);
            this.channelMap.remove(out.getName());
            this.joined.remove(out);
        } else {
            synchronized (this.channelMap) {
                for (final Conversation channel : this.channelMap.values()) {
                    if (channel.getId().equals(chanID)) {
                        this.channelMap.remove(channel.getName());
                    }
                }
            }
        }
    }

    private void removeChannel(@Nonnull final Conversation channel) {
        this.removeChannel(channel.getId());
    }

    /**
     * This method may add a relatively incomplete object to the channel list
     * but the deign is such that it is update by a call to then api to get a full object
     *
     * @param channel the channel to update
     */
    private void updateChannel(final Conversation channel) {
        if (channel != null) {
            this.addChannel(channel);
            final Future<Conversation> future =
                    this.service.submit(() ->
                            this.api.getConversations().getConversation(channel.getId().toString()));
            this.futureConversations.put(future, channel.getId());
        }
        this.checkFutureConversations();

    }

    private void updateChannel(final ObjectID channelID) {
        final Future<Conversation> future =
                this.service.submit(() ->
                        this.api.getConversations().getConversation(channelID.toString()));
        this.futureConversations.put(future, channelID);
        this.checkFutureConversations();
    }

    private void checkFutureConversations() {
        for (final Map.Entry<Future<Conversation>, ObjectID> f : this.futureConversations.entrySet()) {
            if (f.getKey().isDone()) {
                try {
                    final Conversation c = f.getKey().get();
                    if (c != null) {
                        this.addChannel(c);
                    } else {
                        this.removeChannel(f.getValue());
                    }
                } catch (final InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public Set<Conversation> getAllChannels() {
        return Collections.unmodifiableSet(this.channels);
    }

    public Conversation getChannel(final String name) {
        return this.channelMap.get(name.toLowerCase());
    }

    public Conversation getChannelById(final ObjectID id) {
        return this.channelIdMap.get(id);
    }

    private int appendId(final JsonObject object) {
        final int id = this.nextMessageId++;
        object.addProperty("id", id);
        return id;
    }

    public void sendMessage(final String text, final Conversation channel) {
        this.sendMessage(new Message(text, channel));
    }

    public void sendMessage(final Message message) {
        final JsonObject object = this.gson.toJsonTree(message).getAsJsonObject();
        final int id = this.appendId(object);
        this.pendingMessages.put(id, message);
        this.send(object);
    }

    private void send(final JsonObject object) {
        this.session.getRemote().sendStringByFuture(this.gson.toJson(object));
    }

    public boolean isOpen() {
        return this.client != null && this.client.isRunning();
    }

    @Override
    public void close()
    {
        try {
            this.client.stop();
            this.client = null;
            this.service.shutdown();
            this.futureConversations.forEach((conversationFuture, objectID) ->
                    conversationFuture.cancel(true));
            this.futureConversations.clear();
        } catch (final Exception e )
        {
            // Its shutting down, I dont care
        }
    }

    private SlackRTException makeException(final JsonObject object)
    {
        if (object.has("error")) {
            final JsonObject error = object.getAsJsonObject("error");
            return new SlackRTException(error.get("code").getAsInt(), error.get("msg").getAsString());
        }
        return null;
    }

    private void onReply(final JsonObject reply) {
        final int replyId = reply.get("reply_to").getAsInt();
        // TODO: Handle other types of replies
        final Message message = this.pendingMessages.remove(replyId);

        if (reply.get("ok").getAsBoolean()) {
            this.postEvent(new MessageEvent(this.self, message, message.getSubtype()));
        }
        else {
            final SlackRTException exception = this.makeException(reply);
            if (exception != null) {
                this.postError(exception);
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
        if (this.needJoinConfirm) {
            if ("hello".equals(type)) {
                this.needJoinConfirm = false;
                this.postLogin();
            }
            else {
                this.postError(this.makeException(event));
                this.close();
                return;
            }

            return;
        }

        final RealTimeEvent newEvent;
        final Conversation conversation;
        final ObjectID conID;
        final ObjectID userID;
        switch (type)
        {
        case "message":
        {
            // A message from a previous session
            if (event.has("reply_to")) {
                return;
            }
            final Message message = this.gson.fromJson(event, Message.class);
            final User user;
            if (message.getSubtype() == MessageType.Edit) {
                user = this.getUserById(message.getEditUserId());
            } else {
                user = this.getUserById(message.getUserId());
            }
            newEvent = new MessageEvent(user, message, message.getSubtype());
            break;
        }
        case "channel_created":
            case "im_created":
                conversation = this.gson.fromJson(event.get("channel"), Conversation.class);
                newEvent = new ConversationEvent(conversation.getId(), ConversationEvent.EventType.Create);
                this.updateChannel(conversation);
            break;
        case "channel_joined":
            case "group_joined":
                conversation = this.gson.fromJson(event.get("channel"), Conversation.class);
                conID = conversation.getId();
                newEvent = new ConversationEvent(conID, ConversationEvent.EventType.Join);
                this.updateChannel(conversation);
                break;
            case "group_deleted":
            case "channel_deleted":
                conID = new ObjectID(event.get("channel").toString());
                newEvent = new ConversationEvent(conID, ConversationEvent.EventType.Delete);
                this.updateChannel(conID);
                break;
        case "channel_left":
            case "group_left":
                conID = new ObjectID(event.get("channel").toString());
                newEvent = new ConversationEvent(conID, ConversationEvent.EventType.Join);
                this.updateChannel(conID);
            break;
        case "channel_rename":
            case "group_rename":
                conversation = this.gson.fromJson(event.get("channel"), Conversation.class);
                newEvent = new ConversationEvent(conversation.getId(), ConversationEvent.EventType.Rename);
                final Conversation oldObject = this.channelIdMap.get(conversation.getId());
                this.channelMap.remove(oldObject.getName());
                this.channelIdMap.remove(oldObject.getId());
                this.updateChannel(conversation);
            break;
        case "channel_archive":
            case "group_archive":
                conID = new ObjectID(event.get("channel").toString());
                userID = new ObjectID(event.get("user").toString());
                newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Archive);
                this.updateChannel(conID);
            break;
        case "channel_unarchive":
            case "group_unarchive":
                conID = new ObjectID(event.get("channel").toString());
                userID = new ObjectID(event.get("user").toString());
                newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Unarchive);
                this.updateChannel(conID);
            break;
        case "group_open":
            case "im_open":
                conID = new ObjectID(event.get("channel").toString());
                userID = new ObjectID(event.get("user").toString());
                newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Open);
                this.updateChannel(conID);
            break;
        case "group_close":
            case "im_close":
                conID = new ObjectID(event.get("channel").toString());
                userID = new ObjectID(event.get("user").toString());
                newEvent = new UserConversationEvent(conID, userID, ConversationEvent.EventType.Close);
                this.updateChannel(conID);
            break;
        case "user_change":
            final User user = this.gson.fromJson(event.get("user"), User.class);
            this.addUser(user);
            newEvent = null;
            break;
        case "error":
            this.postError(this.makeException(event));
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
            this.postEvent(newEvent);
        }
    }

    private class SocketClient implements WebSocketListener
    {
        @Override
        public void onWebSocketBinary(final byte[] payload, final int offset, final int len )
        {
        }

        @Override
        public void onWebSocketClose(final int statusCode, final String reason ) {
            RealTimeSession.this.postClose();
        }

        @Override
        public void onWebSocketConnect(final Session session )
        {
            RealTimeSession.this.session = session;
        }

        @Override
        public void onWebSocketError(final Throwable cause )
        {
            cause.printStackTrace();
        }

        @Override
        public void onWebSocketText(final String message ) {
            final JsonObject event = RealTimeSession.this.gson.fromJson(message, JsonElement.class).getAsJsonObject();
            if (event.has("ok")) {
                RealTimeSession.this.onReply(event);
            } else {
                RealTimeSession.this.onEvent(event);
            }
        }
    }
}
