package au.com.addstar.slackapi.objects;

import au.com.addstar.slackapi.internal.Utilities;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 26/08/2018.
 * This object is created at a point in time..it will have a createdTimeStamp
 */

@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class TimeStampedBaseObject extends IdBaseObject {

    private long creationDate;

    protected void load(JsonObject object, JsonDeserializationContext context)
    {
        super.load(object,context);
        if (!object.has("created"))
            throw new IllegalStateException("This is not a valid object");

        creationDate = Utilities.getAsTimestamp(object.get("created"));
    }
}
