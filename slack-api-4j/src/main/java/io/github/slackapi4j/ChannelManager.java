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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConnection;
import io.github.slackapi4j.internal.SlackConstants;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.NormalChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class should not be used it exists only for compatibility back to the Slack APi v 1.
 *
 * @deprecated use {@link ConversationsManager}
 */
@Deprecated
public class ChannelManager {
  private final Gson gson;
  private final SlackConnection connection;

  ChannelManager(final SlackApi main) {
    gson = main.getGson();
    connection = main.getSlack();
  }

  public NormalChannel create(final String name) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * Join a Channel.
   *
   * @param channel the channel
   * @return true on success
   * @throws SlackException If the session generates an error
   * @throws IOException    if there is an error connecting to the rest endpoint
   */
  public boolean joinChannel(final NormalChannel channel) throws SlackException, IOException {
    final Map<String, Object> params = ImmutableMap.<String, Object>builder()
        .put("channel", channel.getName())
        .build();

    final JsonObject result = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
    return !(result.has("already_in_channel") && result.get("already_in_channel").getAsBoolean());
  }

  /**
   * Leave  a Channel.
   *
   * @param channel the channel
   * @return true on success
   * @throws SlackException If the session generates an error
   * @throws IOException    if the {@link Gson} object returned cannot be decoded
   */

  public boolean leaveChannel(final NormalChannel channel) throws SlackException, IOException {
    final Map<String, Object> params = ImmutableMap.<String, Object>builder()
        .put("channel", channel.getId())
        .build();

    final JsonObject result = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
    return !(result.has("not_in_channel") && result.get("not_in_channel").getAsBoolean());
  }

  /**
   * Get a Channel.
   *
   * @param id the id as a string to retrieve
   * @return The Channel
   * @throws SlackException If the session generates an error
   * @throws IOException    if the gson object returned cannot be decoded
   */

  public NormalChannel getChannel(final String id) throws SlackException, IOException {
    final Map<String, Object> params = ImmutableMap.<String, Object>builder()
        .put("channel", id)
        .build();

    final JsonObject raw = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
    return gson.fromJson(raw.getAsJsonObject("channel"), NormalChannel.class);
  }

  public List<NormalChannel> getChannels() throws SlackException, IOException {
    return getChannels(true);
  }

  /**
   * Join a Channel.
   *
   * @param includeArchived if you want archived channels as well
   * @return A list of channels
   * @throws SlackException If the session generates an error
   * @throws IOException    if the gson object returned cannot be decoded
   */

  public List<NormalChannel> getChannels(final boolean includeArchived)
      throws SlackException, IOException {
    final JsonObject raw;
    if (includeArchived) {
      raw = connection.callMethodHandled(SlackConstants.CHANNEL_LIST);
    } else {
      final Map<String, Object> params = ImmutableMap.<String, Object>builder()
          .put("exclude_archived", 1)
          .build();
      raw = connection.callMethodHandled(SlackConstants.CHANNEL_LIST, params);
    }

    final JsonArray rawList = raw.getAsJsonArray("channels");
    final ImmutableList.Builder<NormalChannel> channels = ImmutableList.builder();

    for (final JsonElement rawChannel : rawList) {
      channels.add(gson.fromJson(rawChannel, NormalChannel.class));
    }

    return channels.build();
  }

  /**
   * Purge a Channel.
   *
   * @param id the channel id
   * @return true on success
   * @throws IOException if the gson object returned cannot be decoded
   */

  public boolean purgeChannel(final String id) throws IOException {
    final Map<String, Object> params = ImmutableMap.<String, Object>builder()
        .put("channel", id)
        .build();
    try {
      final JsonObject raw = connection.callMethodHandled(SlackConstants.CHANNEL_HISTORY, params);
      final JsonArray rawList = raw.getAsJsonArray("messages");
      final List<Message> messages = new ArrayList<>();
      for (final JsonElement message : rawList) {
        messages.add(gson.fromJson(message, Message.class));
      }
      for (final Message message : messages) {
        final Map<String, Object> p = ImmutableMap.<String, Object>builder()
            .put("channel", id)
            .put("ts", message.getTimestamp())
            .build();
        connection.callMethodHandled(SlackConstants.CHAT_DELETE, p);
      }
    } catch (final SlackException e) {
      return false;
    }
    return true;
  }
}
