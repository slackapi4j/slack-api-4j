package au.com.addstar.slackapi;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.internal.SlackConstants;

@SuppressWarnings("WeakerAccess")
public class SlackAPI
{
    private final SlackConnection connection;
    private final Gson gson;
    
    private final ChannelManager channels;
    private final GroupManager groups;
    
    public SlackAPI(final String token)
    {
        this.connection = new SlackConnection(token);
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(NormalChannel.class, NormalChannel.getGsonAdapter());
        builder.registerTypeAdapter(GroupChannel.class, NormalChannel.getGsonAdapter());
        builder.registerTypeAdapter(DirectChannel.class, NormalChannel.getGsonAdapter());
        builder.registerTypeAdapter(User.class, User.getGsonAdapter());
        builder.registerTypeAdapter(Message.class, Message.getGsonAdapter());
        Attachment.addGsonAdapters(builder);
        
        this.gson = builder.create();
        
        this.channels = new ChannelManager(this);
        this.groups = new GroupManager(this);
    }
    
    public ChannelManager getChannelManager()
    {
        return this.channels;
    }
    
    public GroupManager getGroupManager()
    {
        return this.groups;
    }
    
    public RealTimeSession startRTSession() throws SlackException, IOException
    {
        final JsonObject root = this.connection.callMethodHandled(SlackConstants.RTM_START);
        return new RealTimeSession(root, this);
    }
    
    public void sendMessage(final String message, final BaseChannel channel) throws SlackException, IOException, NullPointerException
    {
        this.sendMessage(message, channel, MessageOptions.DEFAULT);
    }
    
    public Message sendMessage(final String message, final BaseChannel channel, final MessageOptions options) throws SlackException, IOException, NullPointerException
    {
        final Map<String, Object> params = Maps.newHashMap();
        params.put("channel", channel.getId().toString());
        params.put("text", message);
        if (options.getUsername() != null) {
            params.put("username", options.getUsername());
        }
        params.put("as_user", options.isAsUser());
        params.put("link_names", options.isLinkNames() ? 1 : 0);
        params.put("unfurl_links", options.isUnfurlLinks());
        params.put("unfurl_media", options.isUnfurlMedia());
        if (options.getIconEmoji() != null) {
            params.put("icon_emoji", options.getIconEmoji());
        } else if (options.getIconUrl() != null) {
            params.put("icon_url", options.getIconUrl().toExternalForm());
        }
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
            final JsonArray attachments = new JsonArray();
            for (final Attachment attachment : options.getAttachments()) {
                attachments.add(this.gson.toJsonTree(attachment));
            }
            
            params.put("attachments", attachments);
        }
        
        params.put("mrkdwn", options.isFormat());
        
        final JsonObject root = this.connection.callMethodHandled(SlackConstants.CHAT_POST, params);
        return this.gson.fromJson(root.get("message"), Message.class);
    }
    
    SlackConnection getSlack()
    {
        return this.connection;
    }
    
    Gson getGson()
    {
        return this.gson;
    }
}
