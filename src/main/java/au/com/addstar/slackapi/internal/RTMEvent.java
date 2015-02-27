package au.com.addstar.slackapi.internal;

import java.lang.reflect.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class RTMEvent
{
	private String type;
	private int replyTo = -1;
	
	private boolean isError;
	private int errorCode;
	private String errorMessage;
	
	private JsonObject data;
	
	public static Object getGsonAdapter()
	{
		return new RTMEventJsonAdapter();
	}
	
	private static class RTMEventJsonAdapter implements JsonDeserializer<RTMEvent>
	{
		@Override
		public RTMEvent deserialize( JsonElement json, Type typeOfT, JsonDeserializationContext context ) throws JsonParseException
		{
			if (!(json instanceof JsonObject))
				throw new JsonParseException("Expected json object for event root");
			
			JsonObject root = (JsonObject)json;
			
			RTMEvent event = new RTMEvent();
			event.type = root.get("type").getAsString();
			if (root.has("reply_to"))
				event.replyTo = root.get("reply_to").getAsInt();
			
			if (root.has("error"))
			{
				event.isError = true;
				JsonObject error = root.getAsJsonObject("error");
				event.errorCode = error.get("code").getAsInt();
				event.errorMessage = error.get("msg").getAsString();
			}
			
			event.data = root;
			
			return event;
		}
	}
}
