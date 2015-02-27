package au.com.addstar.slackapi;

import java.lang.reflect.Type;

import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Message
{
	private String userId;
	private String text;
	private String sourceId;
	private long timestamp;
	private MessageType subtype;
	
	private String editUserId;
	private long editTimestamp;
	
	public Message(String text, Channel channel)
	{
		this.sourceId = channel.getId();
		this.text = text;
	}
	
	public Message(String text, Group group)
	{
		this.sourceId = group.getId();
		this.text = text;
	}
	
	static Object getGsonAdapter()
	{
		return new MessageJsonAdapter();
	}
	
	@Override
	public String toString()
	{
		return String.format("%s: '%s' from %s", subtype, text, userId);
	}
	
	private static class MessageJsonAdapter implements JsonDeserializer<Message>, JsonSerializer<Message>
	{
		@Override
		public Message deserialize( JsonElement element, Type typeOfT, JsonDeserializationContext context ) throws JsonParseException
		{
			if (!(element instanceof JsonObject))
				throw new JsonParseException("Expected JSONObject as message root");
			
			JsonObject root = (JsonObject)element;
			
			Message message = new Message();
			if (root.has("user"))
				message.userId = root.get("user").getAsString();
			
			message.text = root.get("text").getAsString();
			message.timestamp = Utilities.getAsTimestamp(root.get("ts"));
			if (root.has("channel"))
				message.sourceId = root.get("channel").getAsString();
			
			if (root.has("edited"))
			{
				JsonObject edited = root.getAsJsonObject("edited");
				message.editUserId = edited.get("user").getAsString();
				message.editTimestamp = Utilities.getAsTimestamp(edited.get("ts"));
			}
			
			message.subtype = MessageType.fromId(Utilities.getAsString(root.get("subtype")));
			
			return message;
		}

		@Override
		public JsonElement serialize( Message src, Type typeOfSrc, JsonSerializationContext context )
		{
			JsonObject object = new JsonObject();
			object.addProperty("type", "message");
			object.addProperty("channel", src.sourceId);
			object.addProperty("text", src.text);
			
			return object;
		}
	}
	
	public enum MessageType
	{
		Normal(""),
		FromBot("bot_message"),
		FromMeCommand("me_message"),
		
		Edit("message_changed"),
		Delete("message_deleted"),
		
		ChannelJoin("channel_join"),
		ChannelLeave("channel_leave"),
		ChannelTopic("channel_topic"),
		ChannelPurpose("channel_purpose"),
		ChannelName("channel_name"),
		ChannelArchive("channel_archive"),
		ChannelUnarchive("channel_unarchive"),
		
		GroupJoin("group_join"),
		GroupLeave("group_leave"),
		GroupTopic("group_topic"),
		GroupPurpose("group_purpose"),
		GroupName("group_name"),
		GroupArchive("group_archive"),
		GroupUnarchive("group_unarchive"),
		
		FileShare("file_share"),
		FileComment("file_comment"),
		FileMention("file_mention");
		
		private final String id;
		
		private MessageType(String id)
		{
			this.id = id;
		}
		
		static MessageType fromId(String id)
		{
			if (id == null)
				return Normal;
			
			for (MessageType type : values())
			{
				if (type.id.equals(id))
					return type;
			}
			
			return Normal;
		}
	}
}
