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
    
    static Object getGsonAdapter()
    {
        return new ChannelJsonAdapter();
    }
    
    protected void load(final JsonObject object, final JsonDeserializationContext context)
    {
        this.id = new ObjectID(object.get("id").getAsString());
        if (!object.has("created")) {
            throw new IllegalStateException("This is not a valid channel");
        }
        
        this.creationDate = Utilities.getAsTimestamp(object.get("created"));
    }
    
    private static class ChannelJsonAdapter implements JsonDeserializer<BaseChannel>
    {
        @Override
        public BaseChannel deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context ) throws JsonParseException
        {
            if (!(element instanceof JsonObject)) {
                throw new JsonParseException("Expected JSONObject as channel root");
            }
            
            final JsonObject root = (JsonObject)element;
            
            final BaseChannel channel;
            if (type.equals(GroupChannel.class)) {
                channel = new GroupChannel();
            } else if (type.equals(NormalChannel.class)) {
                channel = new NormalChannel();
            } else if (type.equals(DirectChannel.class)) {
                channel = new DirectChannel();
            } else {
                throw new JsonParseException("Cant load unknown channel type");
            }
            
            channel.load(root, context);
            return channel;
        }
    }
}
