package au.com.addstar.slackapi;

import java.lang.reflect.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@Getter
@EqualsAndHashCode
public abstract class BaseChannel
{
	private ObjectID id;
	private long creationDate;
	
	protected void load(JsonObject object, JsonDeserializationContext context)
	{
		id = new ObjectID(object.get("id").getAsString());
		if (!object.has("created"))
			throw new IllegalStateException("This is not a valid channel");
		
		creationDate = Utilities.getAsTimestamp(object.get("created"));
	}
	
	static Object getGsonAdapter()
	{
		return new ChannelJsonAdapter();
	}
	
	private static class ChannelJsonAdapter implements JsonDeserializer<BaseChannel>
	{
		@Override
		public BaseChannel deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException
		{
			if (!(element instanceof JsonObject))
				throw new JsonParseException("Expected JSONObject as channel root");
			
			JsonObject root = (JsonObject)element;
			
			BaseChannel channel;
			if (type.equals(GroupChannel.class))
				channel = new GroupChannel();
			else if (type.equals(NormalChannel.class))
				channel = new NormalChannel();
			else if (type.equals(DirectChannel.class))
				channel = new DirectChannel();
			else
				throw new JsonParseException("Cant load unknown channel type");
			
			channel.load(root, context);
			return channel;
		}
	}
}
