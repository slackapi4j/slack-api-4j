package au.com.addstar.slackapi.objects;


import lombok.*;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

/**
 * An object that contains a unique identifier
 */
@Getter
@NoArgsConstructor(access= AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper=true)
public abstract class IdBaseObject extends BaseObject
{
    @Nonnull
    private @NonNull ObjectID id;

    protected void load(JsonObject root, JsonDeserializationContext context)
    {
        id = new ObjectID(root.get("id").getAsString());
    }



}
