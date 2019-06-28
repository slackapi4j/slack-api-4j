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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.NormalChannel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConnection;
import io.github.slackapi4j.internal.SlackConstants;

/**
 * @deprecated use {@link ConversationsManager}
 */
@Deprecated
public class ChannelManager
{
    private final Gson gson;
    private final SlackConnection connection;

    ChannelManager(SlackAPI main)
    {
        this.gson = main.getGson();
        this.connection = main.getSlack();
    }

    public NormalChannel create(String name) throws SlackException, IOException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean joinChannel(NormalChannel channel) throws SlackException, IOException
    {
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", channel.getName())
            .build();

        JsonObject result = this.connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return !(result.has("already_in_channel") && result.get("already_in_channel").getAsBoolean());
    }

    public boolean leaveChannel(NormalChannel channel) throws SlackException, IOException
    {
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", channel.getId())
            .build();

        JsonObject result = this.connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return !(result.has("not_in_channel") && result.get("not_in_channel").getAsBoolean());
    }

    public NormalChannel getChannel(String id) throws SlackException, IOException
    {
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", id)
            .build();

        JsonObject raw = this.connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return this.gson.fromJson(raw.getAsJsonObject("channel"), NormalChannel.class);
    }

    public List<NormalChannel> getChannels() throws SlackException, IOException
    {
        return this.getChannels(true);
    }

    public List<NormalChannel> getChannels(boolean includeArchived) throws SlackException, IOException
    {
        JsonObject raw;
        if (includeArchived) {
            raw = connection.callMethodHandled(SlackConstants.CHANNEL_LIST);
        } else
        {
            Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("exclude_archived", 1)
                .build();
            raw = this.connection.callMethodHandled(SlackConstants.CHANNEL_LIST, params);
        }

        JsonArray rawList = raw.getAsJsonArray("channels");
        ImmutableList.Builder<NormalChannel> channels = ImmutableList.builder();

        for (JsonElement rawChannel : rawList) {
            channels.add(gson.fromJson(rawChannel, NormalChannel.class));
        }

        return channels.build();
    }

    public boolean purgeChannel (String id)  throws SlackException, IOException{
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("channel",id)
                .build();
        JsonObject raw = this.connection.callMethodHandled(SlackConstants.CHANNEL_HISTORY, params);
        JsonArray rawList = raw.getAsJsonArray("messages");
        List<Message> messages = new ArrayList<>();
        for (JsonElement message : rawList){
            messages.add(this.gson.fromJson(message, Message.class));
        }
        for (Message message: messages) {
            Map<String, Object> p = ImmutableMap.<String, Object>builder()
                    .put("channel",id)
                    .put("ts",message.getTimestamp())
                    .build();
            this.connection.callMethodHandled(SlackConstants.CHAT_DELETE, p);
        }
        return true;
    }
}
