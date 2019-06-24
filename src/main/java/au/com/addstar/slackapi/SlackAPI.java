package au.com.addstar.slackapi;

import java.io.IOException;
import java.util.Map;

import au.com.addstar.slackapi.objects.*;
import au.com.addstar.slackapi.objects.blocks.Block;
import au.com.addstar.slackapi.objects.blocks.composition.CompositionObject;
import au.com.addstar.slackapi.objects.blocks.elements.Element;
import com.google.common.collect.Maps;
import com.google.gson.*;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.internal.SlackConstants;
import lombok.Data;

@SuppressWarnings("WeakerAccess")
public class SlackAPI
{
    private SlackConnection connection;
    private Gson gson;

    private final ChannelManager channels;
    private final GroupManager groups;
    private final ConversationsManager conversations;

    public ConversationsManager getConversations() {
        return conversations;
    }

    public SlackAPI(String token)
    {
        connection = new SlackConnection(token);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(NormalChannel.class, NormalChannel.getGsonAdapter());
        builder.registerTypeAdapter(GroupChannel.class, GroupChannel.getGsonAdapter());
        builder.registerTypeAdapter(DirectChannel.class, DirectChannel.getGsonAdapter());
        builder.registerTypeAdapter(Conversation.class,Conversation.getGsonAdapter());
        builder.registerTypeAdapter(User.class, User.getGsonAdapter());
        builder.registerTypeAdapter(Message.class, Message.getGsonAdapter());
        Attachment.addGsonAdapters(builder);
        Block.addGsonAdapters(builder);
        CompositionObject.addGsonAdapters(builder);
        Element.addGsonAdapters(builder);
        gson = builder.create();

        channels = new ChannelManager(this);
        groups = new GroupManager(this);
        conversations = new ConversationsManager(this);
    }

    public ChannelManager getChannelManager()
    {
        return channels;
    }

    public GroupManager getGroupManager()
    {
        return groups;
    }

    public RealTimeSession startRTSession() throws SlackException, IOException
    {
        JsonObject root = connection.callMethodHandled(SlackConstants.RTM_START);
        return new RealTimeSession(root, this);
    }

    public Message sendMessage(String message, IdBaseObject channel) throws SlackException, IOException
    {
       return sendMessage(message, channel, MessageOptions.DEFAULT);
    }

    /**
     * Sends a message
     * @param message
     * @return
     * @throws IOException
     * @throws SlackException
     */
    public Message sendMessage(Message message) throws IOException, SlackException {
        JsonElement obj = gson.toJsonTree(message);
        JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POST, obj.getAsJsonObject());
        return gson.fromJson(root.get("message"), Message.class);
    }

    /**
     * Sends an ephemeral message.
     * @param message
     * @return
     * @throws IOException
     * @throws SlackException
     */
    public Message sendEphemeral(Message message) throws IOException, SlackException {
        JsonElement obj = gson.toJsonTree(message);
        JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POSTEMPHEMERAL, obj.getAsJsonObject());
        return gson.fromJson(root.get("message"), Message.class);
    }
    /**
     * @deprecated use {@link #sendMessage(Message)}
     * @param message The string message
     * @param channel the channel to send it too
     * @param options a set of options to apply
     * @return a Message
     * @throws SlackException
     * @throws IOException
     */
    @Deprecated
    public Message sendMessage(String message, IdBaseObject channel, MessageOptions options) throws SlackException, IOException
    {
        Map<String, Object> params = Maps.newHashMap();
        params.put("channel", channel.getId().toString());
        params.put("text", message);
        if (options.getUsername() != null)
            params.put("username", options.getUsername());
        params.put("as_user", options.isAsUser());
        params.put("link_names", options.isLinkNames() ? 1 : 0);
        params.put("unfurl_links", options.isUnfurlLinks());
        params.put("unfurl_media", options.isUnfurlMedia());
        if (options.getIconEmoji() != null)
            params.put("icon_emoji", options.getIconEmoji());
        else if (options.getIconUrl() != null)
            params.put("icon_url", options.getIconUrl().toExternalForm());
        if (options.getMode() != null)
        {
            switch (options.getMode())
            {
            case Full:
                params.put("parse", "full");
                break;
            case None:
                params.put("parse", "none");
                break;
            default:
                break;
            }
        }

        if (options.getAttachments() != null)
        {
            JsonArray attachments = new JsonArray();
            for (Attachment attachment : options.getAttachments())
                attachments.add(gson.toJsonTree(attachment));

            params.put("attachments", attachments);
        }

        params.put("mrkdwn", options.isFormat());

        JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POST, params);
        Message out = gson.fromJson(root.get("message"), Message.class);
        out.setSubtype(Message.MessageType.Sent);
        return out;
    }

    SlackConnection getSlack()
    {
        return connection;
    }

    Gson getGson()
    {
        return gson;
    }
}
