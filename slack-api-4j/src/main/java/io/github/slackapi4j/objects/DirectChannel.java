package io.github.slackapi4j.objects;

import io.github.slackapi4j.internal.Utilities;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
