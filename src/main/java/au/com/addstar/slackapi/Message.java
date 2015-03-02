package au.com.addstar.slackapi;

import java.lang.reflect.Type;
import java.util.List;

import au.com.addstar.slackapi.internal.Utilities;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
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
import lombok.Setter;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Message
{
	private ObjectID userId;
	@Setter
	private String text;
	private ObjectID sourceId;
	private long timestamp;
	private MessageType subtype;
	
	private ObjectID editUserId;
	private long editTimestamp;
	
	@Setter
	private List<Attachment> attachments;
	
	public Message(String text, BaseChannel channel)
	{
		this.sourceId = channel.getId();
		this.text = text;
		this.subtype = MessageType.Sent;
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
				message.userId = new ObjectID(root.get("user").getAsString());
			
			message.text = root.get("text").getAsString();
			message.timestamp = Utilities.getAsTimestamp(root.get("ts"));
			if (root.has("channel"))
				message.sourceId = new ObjectID(root.get("channel").getAsString());
			
			if (root.has("edited"))
			{
				JsonObject edited = root.getAsJsonObject("edited");
				message.editUserId = new ObjectID(edited.get("user").getAsString());
				message.editTimestamp = Utilities.getAsTimestamp(edited.get("ts"));
			}
			
			message.subtype = MessageType.fromId(Utilities.getAsString(root.get("subtype")));
			
			if (root.has("attachments"))
			{
				message.attachments = Lists.newArrayList();
				JsonArray attachments = root.getAsJsonArray("attachments");
				for (JsonElement rawAttachment : attachments)
					message.attachments.add(context.<Attachment>deserialize(rawAttachment, Attachment.class));
			}
			
			return message;
		}

		@Override
		public JsonElement serialize( Message src, Type typeOfSrc, JsonSerializationContext context )
		{
			JsonObject object = new JsonObject();
			object.addProperty("type", "message");
			object.addProperty("channel", src.sourceId.toString());
			object.addProperty("text", src.text);
			
			if (src.attachments != null)
			{
				JsonArray attachments = new JsonArray();
				for (Attachment attachment : src.attachments)
					attachments.add(context.serialize(attachment));
				object.add("attachments", attachments);
			}
			
			return object;
		}
	}
	
	public enum MessageType
	{
		Normal(""),
		Sent(""),
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
