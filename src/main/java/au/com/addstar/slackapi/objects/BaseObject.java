package au.com.addstar.slackapi.objects;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/02/2019.
 */
public abstract class BaseObject {

    protected abstract void load(JsonObject root, JsonDeserializationContext context);
    
    
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
