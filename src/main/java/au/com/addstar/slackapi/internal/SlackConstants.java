package au.com.addstar.slackapi.internal;

import java.io.Serializable;

public final class SlackConstants
{
	public static final String HOST = "api.slack.com";
	
	public static final String API_TEST = "api.test";
	public static final String AUTH_TEST = "auth.test";
	
	public static final String CHANNEL_ARCHIVE = "channels.archive";
	public static final String CHANNEL_CREATE = "channels.create";
	public static final String CHANNEL_HISTORY = "channels.history";
	public static final String CHANNEL_INFO = "channels.info";
	public static final String CHANNEL_INVITE = "channels.invite";
	public static final String CHANNEL_JOIN = "channels.join";
	public static final String CHANNEL_KICK = "channels.kick";
	public static final String CHANNEL_LEAVE = "channels.leave";
	@Deprecated
	public static final String CHANNEL_LIST = "channels.list";
	public static final String CHANNEL_MARK = "channels.mark";
	public static final String CHANNEL_RENAME = "channels.rename";
	public static final String CHANNEL_REPLIES = "channel.replies";
	public static final String CHANNEL_SET_PURPOSE = "channels.setPurpose";
	public static final String CHANNEL_SET_TOPIC = "channels.setTopic";
	public static final String CHANNEL_UNARCHIVE = "channels.unarchive";

	public static final String CONVERSATION_ARCHIVE = "conversations.archive";
	public static final String CONVERSATION_CREATE = "conversations.create";
	public static final String CONVERSATION_CLOSE = "conversations.create";
	public static final String CONVERSATION_HISTORY = "conversations.history";
	public static final String CONVERSATION_INFO = "conversations.info";
	public static final String CONVERSATION_INVITE = "conversations.invite";
	public static final String CONVERSATION_KICK = "conversations.kick";
	public static final String CONVERSATION_LEAVE = "conversations.leave";
	public static final String CONVERSATION_LIST = "conversations.list";
	public static final String CONVERSATION_MARK = "channels.mark";
	public static final String CONVERSATION_RENAME = "conversations.rename";
	public static final String CONVERSATION_REPLIES = "conversations.replies";
	public static final String CONVERSATION_SET_PURPOSE = "conversations.setPurpose";
	public static final String CONVERSATION_SET_TOPIC = "conversations.setTopic";
	public static final String CONVERSATION_UNARCHIVE = "conversations.unarchive";

	public static final String CHAT_DELETE = "chat.delete";
	public static final String CHAT_GETPERMALINK = "chat.getPermalink";
	public static final String CHAT_MEMESSAGE = "chat.meMessage";
	public static final String CHAT_POSTEMPHEMERAL = "chat.postEphemeral";
	public static final String CHAT_POST = "chat.postMessage";
	public static final String CHAT_UNFURL = "chat.unfurl";
	public static final String CHAT_UPDATE = "chat.update";
	
	public static final String EMOJI_LIST = "emoji.list";
	
	public static final String FILE_DELETE = "files.delete";
	public static final String FILE_INFO = "files.info";
	public static final String FILE_LIST = "files.list";
	public static final String FILE_UPLOAD = "files.upload";
	
	public static final String GROUP_ARCHIVE = "groups.archive";
	public static final String GROUP_CLOSE = "groups.close";
	public static final String GROUP_CREATE = "groups.create";
	public static final String GROUP_CREATE_CHILD = "groups.createChild";
	public static final String GROUP_HISTORY = "groups.history";
	public static final String GROUP_INVITE = "groups.invite";
	public static final String GROUP_KICK = "groups.kick";
	public static final String GROUP_LEAVE = "groups.leave";
	@Deprecated
	public static final String GROUP_LIST = "groups.list";
	public static final String GROUP_MARK = "groups.mark";
	public static final String GROUP_OPEN = "groups.open";
	public static final String GROUP_RENAME = "groups.rename";
	public static final String GROUP_SET_PURPOSE = "groups.setPurpose";
	public static final String GROUP_SET_TOPIC = "groups.setTopic";
	public static final String GROUP_UNARCHIVE = "groups.unarchive";
	
	public static final String IM_CLOSE = "im.close";
	public static final String IM_HISTORY = "im.history";
	@Deprecated
	public static final String IM_LIST = "im.list";
	public static final String IM_MARK = "im.mark";
	public static final String IM_OPEN = "im.open";
	
	public static final String OAUTH_ACCESS = "oauth.access";
	
	public static final String RTM_START = "rtm.start";
	
	public static final String SEARCH_ALL = "search.all";
	public static final String SEARCH_FILES = "search.files";
	public static final String SEARCH_MESSAGES = "search.messages";
	
	public static final String STARS_LIST = "stars.list";
	
	public static final String USER_GET_PRESENCE = "users.getPresence";
	public static final String USER_INFO = "users.info";
	public static final String USER_LIST = "users.list";
	public static final String USER_SET_ACTIVE = "users.setActive";
	public static final String USER_SET_PRESENCE = "users.setPresence";
}
