package au.com.addstar.slackapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.eclipse.jetty.util.IO;

@SuppressWarnings("WeakerAccess")
public class SlackAPI
{
    private SlackConnection connection;
    private Gson gson;

    private static boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(final boolean debug) {
        SlackAPI.debug = debug;
    }
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

    /**
     * Ideally encode the target conversation into the message.
     *
     * @param message
     * @param channel
     * @return
     * @throws SlackException
     * @throws IOException
     * @deprecated use {@link #sendMessage(Message)}
     */
    @Deprecated
    public Message sendMessage(String message, IdBaseObject channel) throws SlackException, IOException {
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
        JsonElement elem = gson.toJsonTree(message);
        JsonObject obj = elem.getAsJsonObject();
        this.addDefaultOptions(obj);
        JsonObject root = this.connection.callMethodHandled(SlackConstants.CHAT_POST, obj);
        return this.gson.fromJson(root.get("message"), Message.class);
    }

    private void addDefaultOptions(JsonObject object) {
        MessageOptions options = MessageOptions.DEFAULT;
        object.addProperty("as_user", options.isAsUser());
        object.addProperty("link_names", options.isLinkNames() ? 1 : 0);
        object.addProperty("unfurl_links", options.isUnfurlLinks());
        object.addProperty("unfurl_media", options.isUnfurlMedia());
        if (options.getIconEmoji() != null)
            object.addProperty("icon_emoji", options.getIconEmoji());
        else if (options.getIconUrl() != null)
            object.addProperty("icon_url", options.getIconUrl().toExternalForm());
        if (options.getMode() != null) {
            switch (options.getMode()) {
                case Full:
                    object.addProperty("parse", "full");
                    break;
                case None:
                    object.addProperty("parse", "none");
                    break;
                default:
                    break;
            }
        }
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
        JsonObject out = obj.getAsJsonObject();
        addDefaultOptions(out);
        JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POSTEMPHEMERAL, out);
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

    List<User> getUsers() throws SlackException, IOException {
        JsonObject root = connection.callMethodHandled(SlackConstants.USER_LIST);
        TypeToken<List<User>> token = new TypeToken<List<User>>(){};
        List<User> user = gson.fromJson(root.get("members"), token.getType());
        return user;
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
