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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConnection;
import io.github.slackapi4j.internal.SlackConstants;
import io.github.slackapi4j.objects.Attachment;
import io.github.slackapi4j.objects.Conversation;
import io.github.slackapi4j.objects.DirectChannel;
import io.github.slackapi4j.objects.GroupChannel;
import io.github.slackapi4j.objects.IdBaseObject;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.NormalChannel;
import io.github.slackapi4j.objects.User;
import io.github.slackapi4j.objects.blocks.Block;
import io.github.slackapi4j.objects.blocks.composition.CompositionObject;
import io.github.slackapi4j.objects.blocks.elements.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class SlackApi {
  private static boolean s_debug;
  private final SlackConnection connection;
  private final Gson gson;
  @SuppressWarnings("deprecation")
  private final ChannelManager channels;
  @SuppressWarnings("deprecation")
  private final GroupManager groups;
  private final ConversationsManager conversations;

  /**
   * The base Api class from here you can get almost everything..
   *
   * @param token the token from the interface
   */
  @SuppressWarnings("deprecation")
  public SlackApi(final String token) {
    connection = new SlackConnection(token);
    final GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(NormalChannel.class, NormalChannel.getGsonAdapter());
    builder.registerTypeAdapter(GroupChannel.class, GroupChannel.getGsonAdapter());
    builder.registerTypeAdapter(DirectChannel.class, DirectChannel.getGsonAdapter());
    builder.registerTypeAdapter(Conversation.class, Conversation.getGsonAdapter());
    builder.registerTypeAdapter(User.class, User.getGsonAdapter());
    builder.registerTypeAdapter(Message.class, Message.getGsonAdapter());
    Block.addGsonAdapters(builder);
    CompositionObject.addGsonAdapters(builder);
    Element.addGsonAdapters(builder);
    gson = builder.create();

    channels = new ChannelManager(this);
    groups = new GroupManager(this);
    conversations = new ConversationsManager(this);
  }

  public static boolean isDebug() {
    return s_debug;
  }

  public static void setDebug(final boolean debug) {
    SlackApi.s_debug = debug;
  }

  /**
   * Manages conversations.
   *
   * @return the manager
   */
  public ConversationsManager getConversations() {
    return conversations;
  }

  /**
   * use {@link SlackApi#getConversations()}.
   *
   * @return ChannelManager
   * @deprecated use {@link SlackApi#getConversations()}
   */
  @Deprecated
  public ChannelManager getChannelManager() {
    return channels;
  }

  /**
   * use {@link SlackApi#getConversations()}.
   *
   * @return GroupManager
   * @deprecated use {@link SlackApi#getConversations()}
   */
  @Deprecated
  public GroupManager getGroupManager() {
    return groups;
  }

  public RealTimeSession startRtSession() throws SlackException, IOException {
    final JsonObject root = connection.callMethodHandled(SlackConstants.RTM_START);
    return new RealTimeSession(root, this);
  }

  /**
   * Ideally encode the target conversation into the message.
   *
   * @param message a Message
   * @param channel the target channel
   * @return the message as it arrived - it will now have a timestamp
   * @throws SlackException if there was a error with the api
   * @throws IOException    encoding errors
   * @deprecated use {@link SlackApi#sendMessage(Message)}
   */
  @Deprecated
  public Message sendMessage(final String message, final IdBaseObject channel)
      throws SlackException, IOException {
    return sendMessage(message, channel, MessageOptions.DEFAULT);
  }

  /**
   * Sends a message.
   *
   * @param message a Message
   * @return the message as it arrived - it will now have a timestamp
   * @throws IOException    encoding errors
   * @throws SlackException sending errors
   */
  public Message sendMessage(final Message message) throws IOException, SlackException {
    return sendMessage(message, MessageOptions.DEFAULT);
  }

  /**
   * Sends a message to with options.
   *
   * @param message The string message
   * @param options a set of options to apply
   * @return a Message
   * @throws IOException    encoding errors
   * @throws SlackException sending errors
   */
  public Message sendMessage(final Message message, final MessageOptions options)
      throws IOException, SlackException {
    final JsonElement elem = gson.toJsonTree(message);
    final JsonObject obj = elem.getAsJsonObject();
    addDefaultOptions(obj, options);
    final JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POST, obj);
    return gson.fromJson(root.get("message"), Message.class);
  }

  /**
   * Sends a message to a specific channel with options.
   *
   * @param message The string message
   * @param channel the channel to send it too
   * @param options a set of options to apply
   * @return a Message
   * @throws IOException    encoding errors
   * @throws SlackException sending errors
   * @since 0.0.2
   * @deprecated use {@link SlackApi#sendMessage(Message)}
   */
  @Deprecated
  public Message sendMessage(final String message, final IdBaseObject channel,
                             final MessageOptions options) throws SlackException, IOException {
    final Map<String, Object> params = createParams(options, channel, message);
    final JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POST, params);
    final Message out = gson.fromJson(root.get("message"), Message.class);
    out.setSubtype(Message.MessageType.Sent);
    return out;
  }

  @Deprecated
  private Map<String, Object> createParams(final MessageOptions options, final IdBaseObject channel, final String message) {
    final Map<String, Object> params = Maps.newHashMap();
    params.put("channel", channel.getId().toString());
    params.put("text", message);
    if (options.getUsername() != null) {
      params.put("username", options.getUsername());
    }
    params.put("as_user", options.isAsUser());
    params.put("link_names", options.isLinkNames() ? 1 : 0);
    params.put("unfurl_links", options.isUnfurlLinks());
    params.put("unfurl_media", options.isUnfurlMedia());
    if (options.getIconEmoji() != null) {
      params.put("icon_emoji", options.getIconEmoji());
    } else if (options.getIconUrl() != null) {
      params.put("icon_url", options.getIconUrl().toExternalForm());
    }
    if (options.getMode() != null) {
      switch (options.getMode()) {
        case Full:
          params.put("parse", "full");
          break;
        case None:
          params.put("parse", "none");
          break;
        default:
          break;
      }
    }
    if (options.getAttachments() != null) {
      final JsonArray attachments = new JsonArray();
      for (final Attachment attachment : options.getAttachments()) {
        attachments.add(gson.toJsonTree(attachment));
      }
      params.put("attachments", attachments);
    }
    params.put("mrkdwn", options.isFormat());

    return params;
  }

  private void addDefaultOptions(final JsonObject object, final MessageOptions options) {
    object.addProperty("as_user", options.isAsUser());
    object.addProperty("link_names", options.isLinkNames() ? 1 : 0);
    object.addProperty("unfurl_links", options.isUnfurlLinks());
    object.addProperty("unfurl_media", options.isUnfurlMedia());
    if (options.getIconEmoji() != null) {
      object.addProperty("icon_emoji", options.getIconEmoji());
    } else if (options.getIconUrl() != null) {
      object.addProperty("icon_url", options.getIconUrl().toExternalForm());
    }
    if (options.getMode() != null) {
      switch (options.getMode()) {
        case Full:
          object.addProperty("parse", "full");
          break;
        case None:
          object.addProperty("parse", "none");
          break;
        default:
          break;
      }
    }
  }

  /**
   * Sends an ephemeral message.
   *
   * @param message a Message
   * @return The message as send -  you can check the timestamp to see when it arrived
   * @throws IOException    encoding errors
   * @throws SlackException sending errors
   * @since 0.0.2
   */
  public Message sendEphemeral(final Message message) throws IOException, SlackException {
    return sendEphemeral(message, MessageOptions.DEFAULT);
  }

  /**
   * Sends a message that is only visible in the client that receives it for the user it is sent
   * to.
   *
   * @param message the Message
   * @param options some Options
   * @return the Sent message
   * @throws IOException    if the gson wont decode
   * @throws SlackException if there is a exception from the api
   * @since 0.0.2
   */
  public Message sendEphemeral(final Message message, final MessageOptions options)
      throws IOException, SlackException {
    final JsonElement obj = gson.toJsonTree(message);
    final JsonObject out = obj.getAsJsonObject();
    addDefaultOptions(out, options);
    final JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POSTEMPHEMERAL, out);
    return gson.fromJson(root.get("message"), Message.class);
  }

  /**
   * A list of Users.
   *
   * @return User List
   * @throws SlackException method exception
   * @throws IOException    encoding exception
   */
  @SuppressWarnings("unused")
  public List<User> getUsers() throws SlackException, IOException {
    final JsonObject root = connection.callMethodHandled(SlackConstants.USER_LIST);
    final TypeToken<List<User>> token = new TypeToken<List<User>>() {
    };
    return gson.fromJson(root.get("members"), token.getType());
  }

  SlackConnection getSlack() {
    return connection;
  }

  Gson getGson() {
    return gson;
  }
}
