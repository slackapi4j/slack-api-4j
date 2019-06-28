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

import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConnection;
import io.github.slackapi4j.internal.SlackConstants;
import io.github.slackapi4j.internal.SlackConversationType;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.slackapi4j.objects.Conversation;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.ObjectID;
import io.github.slackapi4j.objects.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/02/2019.
 */
public class ConversationsManager {
    private final Gson gson;
    private final SlackConnection connection;


    ConversationsManager(SlackAPI main) {
        this.gson = main.getGson();
        this.connection = main.getSlack();
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

    public List<ObjectID> getMembers(Conversation conversation) throws SlackException, IOException {
        final Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("channel", conversation.getId())
                .build();
        final JsonObject raw = this.connection.callMethodHandled(SlackConstants.CONVERSATION_MEMBERS, params);
        final JsonArray rawList = raw.getAsJsonArray("members");
        final List<ObjectID> users = new ArrayList<>();
        for (final JsonElement user : rawList) {
            users.add(new ObjectID(user.getAsString()));
        }
        return users;
    }
    /**
     * Returns a conversation thats is a MultiParty DM
     * @param users the users to add.
     * @return
     * @throws IOException
     * @throws SlackException
     */
    public Conversation createDMConversation(final List<User> users) throws IOException, SlackException {
        if (users.size() == 0) {
            throw new IOException("No users to recieve");
        }
        StringBuilder userlist = new StringBuilder();
        for (final User u : users) {
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
