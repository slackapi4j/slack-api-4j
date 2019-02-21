package au.com.addstar.slackapi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.internal.SlackConstants;

public class ChannelManager
{
    private final Gson gson;
    private final SlackConnection connection;
    
    ChannelManager(final SlackAPI main)
    {
        this.gson = main.getGson();
        this.connection = main.getSlack();
    }
    
    public NormalChannel create(final String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public boolean joinChannel(final NormalChannel channel) throws SlackException, IOException
    {
        final Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", channel.getName())
            .build();
        
        final JsonObject result = this.connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return !(result.has("already_in_channel") && result.get("already_in_channel").getAsBoolean());
    }
    
    public boolean leaveChannel(final NormalChannel channel) throws SlackException, IOException
    {
        final Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", channel.getId())
            .build();
        
        final JsonObject result = this.connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return !(result.has("not_in_channel") && result.get("not_in_channel").getAsBoolean());
    }
    
    public NormalChannel getChannel(final String id) throws SlackException, IOException
    {
        final Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", id)
            .build();
        
        final JsonObject raw = this.connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return this.gson.fromJson(raw.getAsJsonObject("channel"), NormalChannel.class);
    }
    
    public List<NormalChannel> getChannels() throws SlackException, IOException
    {
        return this.getChannels(true);
    }
    
    public List<NormalChannel> getChannels(final boolean includeArchived) throws SlackException, IOException
    {
        final JsonObject raw;
        if (includeArchived) {
            raw = this.connection.callMethodHandled(SlackConstants.CHANNEL_LIST);
        } else
        {
            final Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("exclude_archived", 1)
                .build();
            raw = this.connection.callMethodHandled(SlackConstants.CHANNEL_LIST, params);
        }
        
        final JsonArray rawList = raw.getAsJsonArray("channels");
        final ImmutableList.Builder<NormalChannel> channels = ImmutableList.builder();
        
        for (final JsonElement rawChannel : rawList) {
            channels.add(this.gson.fromJson(rawChannel, NormalChannel.class));
        }
        
        return channels.build();
    }
}
