package io.github.slackapi4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConnection;
import io.github.slackapi4j.internal.SlackConstants;

import io.github.slackapi4j.objects.GroupChannel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This is used to manager group channels
 * @deprecated Use {@link ConversationsManager)
 */

@Deprecated
public class GroupManager
{
    private final Gson gson;
    private final SlackConnection connection;

    GroupManager(SlackAPI main)
    {
        this.gson = main.getGson();
        this.connection = main.getSlack();
    }

    public List<GroupChannel> getGroups() throws SlackException, IOException
    {
        return this.getGroups(true);
    }

    public List<GroupChannel> getGroups(boolean includeArchived) throws SlackException, IOException
    {
        JsonObject raw;
        if (includeArchived) {
            raw = connection.callMethodHandled(SlackConstants.GROUP_LIST);
        } else
        {
            Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("exclude_archived", 1)
                .build();
            raw = this.connection.callMethodHandled(SlackConstants.GROUP_LIST, params);
        }

        JsonArray rawList = raw.getAsJsonArray("groups");
        ImmutableList.Builder<GroupChannel> groups = ImmutableList.builder();

        for (JsonElement rawGroup : rawList) {
            groups.add(gson.fromJson(rawGroup, GroupChannel.class));
        }

        return groups.build();
    }
}
