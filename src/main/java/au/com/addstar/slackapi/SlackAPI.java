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

public class SlackAPI
{
	private SlackConnection connection;
	private Gson gson;
	
	private ChannelManager channels;
	private GroupManager groups;
	
	public SlackAPI(String token)
	{
		connection = new SlackConnection(token);
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Channel.class, Channel.getGsonAdapter());
		builder.registerTypeAdapter(User.class, User.getGsonAdapter());
		builder.registerTypeAdapter(Group.class, Group.getGsonAdapter());
		builder.registerTypeAdapter(Message.class, Message.getGsonAdapter());
		Attachment.addGsonAdapters(builder);
				
		gson = builder.create();
		
		channels = new ChannelManager(this);
		groups = new GroupManager(this);
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
	
	public void sendMessage(String message, Channel channel) throws SlackException, IOException
	{
		sendMessage(message, channel, MessageOptions.DEFAULT);
	}
	
	public Message sendMessage(String message, Channel channel, MessageOptions options) throws SlackException, IOException
	{
		return sendMessage(message, channel.getId(), options);
	}
	
	public void sendMessage(String message, Group group) throws SlackException, IOException
	{
		sendMessage(message, group, MessageOptions.DEFAULT);
	}
	
	public Message sendMessage(String message, Group group, MessageOptions options) throws SlackException, IOException
	{
		return sendMessage(message, group.getId(), options);
	}
	
	private Message sendMessage(String message, String id, MessageOptions options) throws SlackException, IOException
	{
		Map<String, Object> params = Maps.newHashMap();
		params.put("channel", id);
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
		
		if (options.getAttachments() != null)
		{
			JsonArray attachments = new JsonArray();
			for (Attachment attachment : options.getAttachments())
				attachments.add(gson.toJsonTree(attachment));
			
			params.put("attachments", attachments);
		}
		
		params.put("mrkdwn", options.isFormat());
		
		JsonObject root = connection.callMethodHandled(SlackConstants.CHAT_POST, params);
		return gson.fromJson(root.get("message"), Message.class);
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
