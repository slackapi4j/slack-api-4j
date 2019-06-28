package io.github.slackapi4j.objects;


import lombok.*;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

/**
 * An object that contains a unique identifier
 */
@Getter
@NoArgsConstructor(access= AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper=true)
public abstract class IdBaseObject extends BaseObject
{
    @NotNull
    private ObjectID id;

    protected void load(JsonObject root, JsonDeserializationContext context)
    {
        this.id = new ObjectID(root.get("id").getAsString());
    }



}
