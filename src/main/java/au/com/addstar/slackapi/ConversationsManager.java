package au.com.addstar.slackapi;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.internal.SlackConstants;
import au.com.addstar.slackapi.internal.SlackConversationType;
import au.com.addstar.slackapi.objects.*;
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


    ConversationsManager(SlackAPI main) {
        gson = main.getGson();
        connection = main.getSlack();
    }
    
    /**
     * List Conversations / Channels in workspace
     * @param types the Types to list
     * @return List of Conversations
     * @throws SlackException
     * @throws IOException
     */
    public List<Conversation> listConversations(final List<SlackConversationType> types) throws SlackException, IOException {
        return this.listConversations(types,true);
    }
    
    /**
     * Gets a conversation based on an ID.
     * @param conversationID
     * @return Conversation
     * @throws IOException
     * @throws SlackException
     */
    public Conversation getConversation(final String conversationID) throws IOException, SlackException {
        final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.<String, Object>builder()
                .put("channel", conversationID);
        final Map<String, Object> params = mapBuilder.build();
        final JsonObject result = this.connection.callMethodHandled(SlackConstants.CONVERSATION_INFO, params);
        return this.gson.fromJson(result.get("channel").getAsJsonObject(),Conversation.class);
    }
    
    /**
     * A list of conversations by Type
     * @param types A list of types to return
     * @param excludeArchived if true will not return archived conversations
     * @return List
     * @throws SlackException
     * @throws IOException
     */
    public List<Conversation> listConversations(final List<SlackConversationType> types, final boolean excludeArchived) throws SlackException, IOException
    {
        final StringBuilder builder = new StringBuilder();
        for(final SlackConversationType t:types){
            builder.append(t).append(',');
        }
        final String typeString = builder.toString().substring(0,builder.length()-1);
        final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.<String, Object>builder()
                .put("type", typeString);
        if(!excludeArchived) {
            mapBuilder.put("exclude_archived", "false");
        }
        final Map<String, Object> params = mapBuilder.build();
        final List<Conversation> conversations = new ArrayList<>();
        final JsonObject result = this.connection.callMethodHandled(SlackConstants.CONVERSATION_LIST, params);
        final JsonArray array = result.getAsJsonArray("channels");
        for(final JsonElement object:array){
            conversations.add(this.gson.fromJson(object,Conversation.class));
        }
        return conversations;
    }
    
    /**
     * returns true if the Bot is a member of the channel
     * @param c The conversation
     * @return
     */
    public boolean isMember(final Conversation c){
        return c.isMember();
    }
    
    /**
     * Deletes all messages from a conversation
     * @param c the conversation / channel
     * @return true if deleted
     * @throws SlackException
     * @throws IOException
     */
    public boolean purgeChannel (final Conversation c)  throws SlackException, IOException{
        final Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("channel",c.getId().getId())
                .build();
        final JsonObject raw = this.connection.callMethodHandled(SlackConstants.CONVERSATION_HISTORY,params);
        final JsonArray rawList = raw.getAsJsonArray("messages");
        final List<Message> messages = new ArrayList<>();
        for (final JsonElement message : rawList){
            messages.add(this.gson.fromJson(message,Message.class));
        }
        for (final Message message: messages) {
            final Map<String, Object> p = ImmutableMap.<String, Object>builder()
                    .put("channel",c.getId().getId())
                    .put("ts",message.getTimestamp())
                    .build();
            this.connection.callMethodHandled(SlackConstants.CHAT_DELETE,p );
        }
        return true;
    }
    
    /**
     * Returns a conversation thats is a MultiParty DM
     * @param users the users to add.
     * @return
     * @throws IOException
     * @throws SlackException
     */
    public Conversation createMultiPartyMessage(List<User> users) throws IOException, SlackException {
        StringBuilder userlist = new StringBuilder();
        for(User u:users){
            userlist.append(u.getId()).append(',');
        }
        
        final String userString = userlist.toString().substring(0,userlist.length()-1);
        final ImmutableMap.Builder<String,Object> builder = ImmutableMap.builder();
        builder.put("users",userString);
        builder.put("return_im",true);
        final Map<String, Object> params = builder.build();
        final JsonObject raw = this.connection.callMethodHandled(SlackConstants.CONVERSATION_OPEN,params);
        final Conversation con = this.gson.fromJson(raw.get("channel"),Conversation.class);
        return con;
    }
    
    /**
     * Closes a Group or DM Channel. It wont close a public or private Channel.
     * @param conversation
     * @return true if closed.
     * @throws IOException
     * @throws SlackException
     */
    public boolean closeMultiPartyMessage(Conversation conversation) throws IOException, SlackException {
            final Map<String, Object> params = ImmutableMap.<String, Object>builder().
                    put("channel", conversation.getId())
                    .build();
            final JsonObject raw = this.connection.callMethodHandled(SlackConstants.CONVERSATION_CLOSE, params);
            return true;
    }
}
