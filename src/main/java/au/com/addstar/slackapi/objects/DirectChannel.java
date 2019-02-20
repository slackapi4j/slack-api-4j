package au.com.addstar.slackapi.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
@Deprecated
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper=true)
public class DirectChannel extends TimeStampedBaseObject
{
	private ObjectID userId;
	private boolean isUserDeleted;
	
	// Optional params
	private boolean isOpen;
	private long lastRead;
	private int unreadCount;
	private Message latest;
	
	@Override
	protected void load( JsonObject object, JsonDeserializationContext context )
	{
		super.load(object, context);
		
		userId = new ObjectID(object.get("user").getAsString());
		isUserDeleted = Utilities.getAsBoolean(object.get("is_user_deleted"), false);
		
		isOpen = Utilities.getAsBoolean(object.get("is_open"), true);
		if (object.has("last_read"))
		{
			lastRead = Utilities.getAsTimestamp(object.get("last_read"));
			unreadCount = Utilities.getAsInt(object.get("unread_count"));
			latest = context.deserialize(object.get("latest"), Message.class);
		}
	}
}
