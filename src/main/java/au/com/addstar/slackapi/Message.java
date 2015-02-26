package au.com.addstar.slackapi;

import java.lang.reflect.Type;

import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Message
{
	private String userId;
	private String text;
	private String sourceId;
	private long timestamp;
	private String subtype;
	
	private String editUserId;
	private long editTimestamp;
	
	static Object getGsonAdapter()
	{
		return new MessageJsonAdapter();
	}
	
	private static class MessageJsonAdapter implements JsonDeserializer<Message>
	{
		@Override
		public Message deserialize( JsonElement element, Type typeOfT, JsonDeserializationContext context ) throws JsonParseException
		{
			if (!(element instanceof JsonObject))
				throw new JsonParseException("Expected JSONObject as message root");
			
			JsonObject root = (JsonObject)element;
			
			Message message = new Message();
			message.userId = root.get("user").getAsString();
			message.text = root.get("text").getAsString();
			message.timestamp = Utilities.getAsTimestamp(root.get("ts"));
			if (root.has("channel")) {
				message.sourceId = root.get("channel").getAsString();
			}
			
			if (root.has("edited"))
			{
				JsonObject edited = root.getAsJsonObject("edited");
				message.editUserId = edited.get("user").getAsString();
				message.editTimestamp = Utilities.getAsTimestamp(edited.get("ts"));
			}
			
			if (root.has("subtype"))
				message.subtype = root.get("subtype").getAsString();
			else
				message.subtype = "";
			
			return message;
		}
	}
}
