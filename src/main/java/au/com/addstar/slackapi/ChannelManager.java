package au.com.addstar.slackapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.addstar.slackapi.objects.Message;
import au.com.addstar.slackapi.objects.NormalChannel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.internal.SlackConstants;

/**
 * @deprecated use {@link ConversationsManager}
 */
@Deprecated
public class ChannelManager
{
    private Gson gson;
    private SlackConnection connection;
    
    ChannelManager(SlackAPI main)
    {
        gson = main.getGson();
        connection = main.getSlack();
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
        
        JsonObject result = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return !(result.has("already_in_channel") && result.get("already_in_channel").getAsBoolean());
    }
    
    public boolean leaveChannel(NormalChannel channel) throws SlackException, IOException
    {
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", channel.getId())
            .build();
        
        JsonObject result = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return !(result.has("not_in_channel") && result.get("not_in_channel").getAsBoolean());
    }
    
    public NormalChannel getChannel(String id) throws SlackException, IOException
    {
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
            .put("channel", id)
            .build();
        
        JsonObject raw = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
        return gson.fromJson(raw.getAsJsonObject("channel"), NormalChannel.class);
    }
    
    public List<NormalChannel> getChannels() throws SlackException, IOException
    {
        return getChannels(true);
    }
    
    public List<NormalChannel> getChannels(boolean includeArchived) throws SlackException, IOException
    {
        JsonObject raw;
        if (includeArchived)
            raw = connection.callMethodHandled(SlackConstants.CHANNEL_LIST);
        else
        {
            Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("exclude_archived", 1)
                .build();
            raw = connection.callMethodHandled(SlackConstants.CHANNEL_LIST, params);
        }
        
        JsonArray rawList = raw.getAsJsonArray("channels");
        ImmutableList.Builder<NormalChannel> channels = ImmutableList.builder();
        
        for (JsonElement rawChannel : rawList)
            channels.add(gson.fromJson(rawChannel, NormalChannel.class));
        
        return channels.build();
    }

    public boolean purgeChannel (String id)  throws SlackException, IOException{
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("channel",id)
                .build();
        JsonObject raw = connection.callMethodHandled(SlackConstants.CHANNEL_HISTORY,params);
        JsonArray rawList = raw.getAsJsonArray("messages");
        List<Message> messages = new ArrayList<>();
        for (JsonElement message : rawList){
            messages.add(gson.fromJson(message,Message.class));
        }
        for (Message message: messages) {
            Map<String, Object> p = ImmutableMap.<String, Object>builder()
                    .put("channel",id)
                    .put("ts",message.getTimestamp())
                    .build();
            connection.callMethodHandled(SlackConstants.CHAT_DELETE,p );
        }
        return true;
    }
}
