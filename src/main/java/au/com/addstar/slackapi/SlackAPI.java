package au.com.addstar.slackapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import au.com.addstar.slackapi.internal.SlackConnection;

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
	
	SlackConnection getSlack()
	{
		return connection;
	}
	
	Gson getGson()
	{
		return gson;
	}
}
