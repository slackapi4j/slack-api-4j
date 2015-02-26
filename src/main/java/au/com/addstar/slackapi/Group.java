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
public class Group
{
	private String name;
	private String id;
	private long creationDate;
	private String creationUserId;
	private boolean isArchived;
	
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
		return new GroupJsonAdapter();
	}
	
	private static class GroupJsonAdapter implements JsonDeserializer<Group>
	{
		@Override
		public Group deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException
		{
			if (!(element instanceof JsonObject))
				throw new JsonParseException("Expected JSONObject as group root");
			
			JsonObject root = (JsonObject)element;
			
			Group group = new Group();
			group.name = root.get("name").getAsString();
			group.id = root.get("id").getAsString();
			group.creationDate = root.get("created").getAsLong() * 1000;
			group.creationUserId = root.get("creator").getAsString();
			group.isArchived = root.get("is_archived").getAsBoolean();
			
			JsonArray members = root.get("members").getAsJsonArray();
			List<String> memberList = new ArrayList<String>(members.size());
			for (JsonElement member : members)
				memberList.add(member.getAsString());
			group.memberIds = memberList;
			
			JsonObject topic = root.get("topic").getAsJsonObject();
			group.topic = topic.get("value").getAsString();
			group.topicUpdateDate = Utilities.getAsTimestamp(topic.get("last_set"));
			group.topicUpdateUserId = topic.get("creator").getAsString();
			
			JsonObject purpose = root.get("purpose").getAsJsonObject();
			group.purpose = purpose.get("value").getAsString();
			group.purposeUpdateDate = Utilities.getAsTimestamp(purpose.get("last_set"));
			group.purposeUpdateUserId = purpose.get("creator").getAsString();
			
			group.isClientMember = root.has("last_read");
			if (group.isClientMember)
			{
				group.lastRead = Utilities.getAsTimestamp(root.get("last_read"));
				group.latest = context.deserialize(root.get("latest"), Message.class);
				group.unreadCount = root.get("unread_count").getAsInt();
				group.unreadCountDisplay = root.get("unread_count_display").getAsInt();
			}
			
			return group;
		}
	}
}
