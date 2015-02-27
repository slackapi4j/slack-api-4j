package au.com.addstar.slackapi;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	
	SlackConnection getSlack()
	{
		return connection;
	}
	
	Gson getGson()
	{
		return gson;
	}
}
