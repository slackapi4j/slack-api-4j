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
public class NormalChannel extends Conversation
{
	// Member only values
	private long lastRead;
	private int unreadCount;
	private int unreadCountDisplay;
	
	private Message latest;
	
	@Override
	protected void load( JsonObject root, JsonDeserializationContext context )
	{
		super.load(root, context);

		if (super.isMember() && root.has("last_read"))
		{
			lastRead = Utilities.getAsTimestamp(root.get("last_read"));
			latest = context.deserialize(root.get("latest"), Message.class);
			unreadCount = root.get("unread_count").getAsInt();
			unreadCountDisplay = root.get("unread_count_display").getAsInt();
		}
	}
}
