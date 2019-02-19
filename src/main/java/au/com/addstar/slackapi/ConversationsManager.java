package au.com.addstar.slackapi;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.internal.SlackConstants;
import au.com.addstar.slackapi.internal.SlackConversationType;
import au.com.addstar.slackapi.objects.Conversation;
import au.com.addstar.slackapi.objects.NormalChannel;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/02/2019.
 */
public class ConversationsManager {
    private Gson gson;
    private SlackConnection connection;


    public ConversationsManager(SlackAPI main) {
        gson = main.getGson();
        connection = main.getSlack();
    }

    public boolean joinConversation(Conversation channel) throws SlackException, IOException
    {
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("channel", channel.getName())
                .build();

        JsonObject result = connection.callMethodHandled(SlackConstants.CONVERSATION_JOIN, params);
        Conversation conv = gson.fromJson(result,Conversation.class);
        return conv.isMember();
    }

    public List<Conversation> listConversations(List<SlackConversationType> types) throws SlackException, IOException
    {
        StringBuilder builder = new StringBuilder();
        for(SlackConversationType t:types){
            builder.append(t.toString()).append(",");
        }
        String typeString = builder.toString().substring(0,builder.length()-1);
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("type", typeString)
                .build();
        List<Conversation> conversations = new ArrayList<>();
        JsonObject result = connection.callMethodHandled(SlackConstants.CONVERSATION_LIST, params);
        JsonArray array = result.getAsJsonArray("channels");
        for(JsonElement object:array){
            conversations.add(gson.fromJson(object,Conversation.class));
        }

        return conversations;
    }
}
