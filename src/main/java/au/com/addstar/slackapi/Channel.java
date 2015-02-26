package au.com.addstar.slackapi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Channel
{
	private String name;
	private String id;
	private long creationDate;
	private String creationUserId;
	private boolean isArchived;
	private boolean isGeneral;
	
	private List<String> memberIds;
	private boolean isClientMember;
	
	private String topic;
	private String topicUpdateUserId;
	private long topicUpdateDate;
	
	private String purpose;
	private String purposeUpdateUserId;
	private long purposeUpdateDate;
	
	// Member only values
	private long lastRead;
	private int unreadCount;
	private int unreadCountDisplay;
	
	private Message latest;
	
	static Object getGsonAdapter()
	{
		return new ChannelJsonAdapter();
	}
	
	private static class ChannelJsonAdapter implements JsonDeserializer<Channel>
	{
		@Override
		public Channel deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException
		{
			if (!(element instanceof JsonObject))
				throw new JsonParseException("Expected JSONObject as channel root");
			
			JsonObject root = (JsonObject)element;
			
			Channel channel = new Channel();
			channel.name = root.get("name").getAsString();
			channel.id = root.get("id").getAsString();
			channel.creationDate = root.get("created").getAsLong() * 1000;
			channel.creationUserId = root.get("creator").getAsString();
			channel.isArchived = root.get("is_archived").getAsBoolean();
			channel.isGeneral = root.get("is_general").getAsBoolean();
			
			JsonArray members = root.get("members").getAsJsonArray();
			List<String> memberList = new ArrayList<String>(members.size());
			for (JsonElement member : members)
				memberList.add(member.getAsString());
			channel.memberIds = memberList;
			
			JsonObject topic = root.get("topic").getAsJsonObject();
			channel.topic = topic.get("value").getAsString();
			channel.topicUpdateDate = Utilities.getAsTimestamp(topic.get("last_set"));
			channel.topicUpdateUserId = topic.get("creator").getAsString();
			
			JsonObject purpose = root.get("purpose").getAsJsonObject();
			channel.purpose = purpose.get("value").getAsString();
			channel.purposeUpdateDate = Utilities.getAsTimestamp(purpose.get("last_set"));
			channel.purposeUpdateUserId = purpose.get("creator").getAsString();
			
			channel.isClientMember = root.get("is_member").getAsBoolean();
			if (channel.isClientMember)
			{
				channel.lastRead = Utilities.getAsTimestamp(root.get("last_read"));
				channel.latest = context.deserialize(root.get("latest"), Message.class);
				channel.unreadCount = root.get("unread_count").getAsInt();
				channel.unreadCountDisplay = root.get("unread_count_display").getAsInt();
			}
			
			return channel;
		}
	}
}
