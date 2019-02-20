package au.com.addstar.slackapi.objects;

import java.lang.reflect.Type;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper=true)
public abstract class IdBaseObject extends BaseObject
{
	private ObjectID id;

	protected void load(JsonObject root, JsonDeserializationContext context)
	{
		id = new ObjectID(root.get("id").getAsString());
	}
	


}
