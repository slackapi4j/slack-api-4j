package au.com.addstar.slackapi;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

@SuppressWarnings("ALL")
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper=true)
public class DirectChannel extends BaseChannel
{
    private ObjectID userId;
    private boolean isUserDeleted;
    
    // Optional params
    private boolean isOpen;
    private long lastRead;
    private int unreadCount;
    private Message latest;
    
    @Override
    protected void load(final JsonObject object, final JsonDeserializationContext context )
    {
        super.load(object, context);
        
        this.userId = new ObjectID(object.get("user").getAsString());
        this.isUserDeleted = Utilities.getAsBoolean(object.get("is_user_deleted"), false);
        
        this.isOpen = Utilities.getAsBoolean(object.get("is_open"), true);
        if (object.has("last_read"))
        {
            this.lastRead = Utilities.getAsTimestamp(object.get("last_read"));
            this.unreadCount = Utilities.getAsInt(object.get("unread_count"));
            this.latest = context.deserialize(object.get("latest"), Message.class);
        }
    }
}
