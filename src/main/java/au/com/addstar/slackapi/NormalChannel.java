package au.com.addstar.slackapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper=true)
public class NormalChannel extends BaseChannel
{
	private String name;
	private ObjectID creationUserId;
	private boolean isArchived;
	private boolean isGeneral;
	
	private List<ObjectID> memberIds;
	private boolean isClientMember;
	
	private String topic;
	private ObjectID topicUpdateUserId;
	private long topicUpdateDate;
	
	private String purpose;
	private ObjectID purposeUpdateUserId;
	private long purposeUpdateDate;
	
	// Member only values
	private long lastRead;
	private int unreadCount;
	private int unreadCountDisplay;
	
	private Message latest;
	
	@Override
	protected void load( JsonObject root, JsonDeserializationContext context )
	{
		super.load(root, context);
		
		name = root.get("name").getAsString();
		creationUserId = new ObjectID(root.get("creator").getAsString());
		isArchived = Utilities.getAsBoolean(root.get("is_archived"), false);
		isGeneral = Utilities.getAsBoolean(root.get("is_general"), false);
		
		if (root.has("members"))
		{
			JsonArray members = root.get("members").getAsJsonArray();
			List<ObjectID> memberList = new ArrayList<ObjectID>(members.size());
			for (JsonElement member : members)
				memberList.add(new ObjectID(member.getAsString()));
			memberIds = memberList;
		}
		else
			memberIds = Collections.emptyList();
		
		if (root.has("topic"))
		{
			JsonObject topic = root.get("topic").getAsJsonObject();
			this.topic = topic.get("value").getAsString();
			topicUpdateDate = Utilities.getAsTimestamp(topic.get("last_set"));
			topicUpdateUserId = new ObjectID(topic.get("creator").getAsString());
		}
		
		if (root.has("purpose"))
		{
			JsonObject purpose = root.get("purpose").getAsJsonObject();
			this.purpose = purpose.get("value").getAsString();
			purposeUpdateDate = Utilities.getAsTimestamp(purpose.get("last_set"));
			purposeUpdateUserId = new ObjectID(purpose.get("creator").getAsString());
		}
		
		if (root.has("is_member"))
			isClientMember = root.get("is_member").getAsBoolean();
		else
			isClientMember = root.has("last_read");
		
		if (isClientMember && root.has("last_read"))
		{
			lastRead = Utilities.getAsTimestamp(root.get("last_read"));
			latest = context.deserialize(root.get("latest"), Message.class);
			unreadCount = root.get("unread_count").getAsInt();
			unreadCountDisplay = root.get("unread_count_display").getAsInt();
		}
	}
}
