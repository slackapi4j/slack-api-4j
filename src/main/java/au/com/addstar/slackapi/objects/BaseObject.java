package au.com.addstar.slackapi.objects;

import java.lang.reflect.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@Getter
@EqualsAndHashCode
public abstract class BaseObject
{
	private ObjectID id;

	protected void load(JsonObject object, JsonDeserializationContext context)
	{
		id = new ObjectID(object.get("id").getAsString());
	}
	
	public static Object getGsonAdapter()
	{
		return new ChannelJsonAdapter();
	}
	
	private static class ChannelJsonAdapter implements JsonDeserializer<BaseObject>
	{
		@Override
		public BaseObject deserialize(JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException
		{
			if (!(element instanceof JsonObject))
				throw new JsonParseException("Expected JSONObject as channel root");
			
			JsonObject root = (JsonObject)element;
			
			BaseObject object;
			if(type.equals(User.class))
				object = new User();
			else if(type.equals(Conversation.class))
				object = new Conversation();
			else if (type.equals(GroupChannel.class))
				object = new GroupChannel();
			else if (type.equals(NormalChannel.class))
				object = new NormalChannel();
			else if (type.equals(DirectChannel.class))
				object = new DirectChannel();
			else
				throw new JsonParseException("Cant load unknown channel type");

			object.load(root, context);
			return object;
		}
	}
}
