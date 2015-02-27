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
	private Gson gson;
	private SlackConnection connection;
	
	ChannelManager(SlackAPI main)
	{
		gson = main.getGson();
		connection = main.getSlack();
	}
	
	public Channel create(String name) throws SlackException, IOException
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public boolean joinChannel(Channel channel) throws SlackException, IOException
	{
		Map<String, Object> params = ImmutableMap.<String, Object>builder()
			.put("channel", channel.getName())
			.build();
		
		JsonObject result = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
		return !(result.has("already_in_channel") && result.get("already_in_channel").getAsBoolean());
	}
	
	public boolean leaveChannel(Channel channel) throws SlackException, IOException
	{
		Map<String, Object> params = ImmutableMap.<String, Object>builder()
			.put("channel", channel.getId())
			.build();
		
		JsonObject result = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
		return !(result.has("not_in_channel") && result.get("not_in_channel").getAsBoolean());
	}
	
	public Channel getChannel(String id) throws SlackException, IOException
	{
		Map<String, Object> params = ImmutableMap.<String, Object>builder()
			.put("channel", id)
			.build();
		
		JsonObject raw = connection.callMethodHandled(SlackConstants.CHANNEL_INFO, params);
		return gson.fromJson(raw.getAsJsonObject("channel"), Channel.class);
	}
	
	public List<Channel> getChannels() throws SlackException, IOException
	{
		return getChannels(true);
	}
	
	public List<Channel> getChannels(boolean includeArchived) throws SlackException, IOException
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
		ImmutableList.Builder<Channel> channels = ImmutableList.builder();
		
		for (JsonElement rawChannel : rawList)
			channels.add(gson.fromJson(rawChannel, Channel.class));
		
		return channels.build();
	}
}
