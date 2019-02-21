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
    private final Gson gson;
    private final SlackConnection connection;
    
    GroupManager(final SlackAPI main)
    {
        this.gson = main.getGson();
        this.connection = main.getSlack();
    }
    
    public List<GroupChannel> getGroups() throws SlackException, IOException
    {
        return this.getGroups(true);
    }
    
    public List<GroupChannel> getGroups(final boolean includeArchived) throws SlackException, IOException
    {
        final JsonObject raw;
        if (includeArchived) {
            raw = this.connection.callMethodHandled(SlackConstants.GROUP_LIST);
        } else
        {
            final Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("exclude_archived", 1)
                .build();
            raw = this.connection.callMethodHandled(SlackConstants.GROUP_LIST, params);
        }
        
        final JsonArray rawList = raw.getAsJsonArray("groups");
        final ImmutableList.Builder<GroupChannel> groups = ImmutableList.builder();
        
        for (final JsonElement rawGroup : rawList) {
            groups.add(this.gson.fromJson(rawGroup, GroupChannel.class));
        }
        
        return groups.build();
    }
}
