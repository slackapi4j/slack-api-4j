package au.com.addstar.slackapi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.internal.SlackConstants;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GroupManager
{
	private Gson gson;
	private SlackConnection connection;
	
	GroupManager(SlackAPI main)
	{
		gson = main.getGson();
		connection = main.getSlack();
	}
	
	public List<Group> getGroups() throws SlackException, IOException
	{
		return getGroups(true);
	}
	
	public List<Group> getGroups(boolean includeArchived) throws SlackException, IOException
	{
		JsonObject raw;
		if (includeArchived)
			raw = connection.callMethodHandled(SlackConstants.GROUP_LIST);
		else
		{
			Map<String, Object> params = ImmutableMap.<String, Object>builder()
				.put("exclude_archived", 1)
				.build();
			raw = connection.callMethodHandled(SlackConstants.GROUP_LIST, params);
		}
		
		JsonArray rawList = raw.getAsJsonArray("groups");
		ImmutableList.Builder<Group> groups = ImmutableList.builder();
		
		for (JsonElement rawGroup : rawList)
			groups.add(gson.fromJson(rawGroup, Group.class));
		
		return groups.build();
	}
}
