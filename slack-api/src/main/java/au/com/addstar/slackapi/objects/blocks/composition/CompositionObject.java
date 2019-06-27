package au.com.addstar.slackapi.objects.blocks.composition;

import java.lang.reflect.Type;

import au.com.addstar.slackapi.objects.BaseObject;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
public abstract class CompositionObject extends BaseObject {
    
    
    public static void addGsonAdapters(GsonBuilder builder)
    {
        builder.registerTypeAdapter(TextObject.class, TextObject.getGsonAdapter());
        builder.registerTypeAdapter(Option.class, Option.getGsonAdapter());
        builder.registerTypeAdapter(ConfirmObject.class,ConfirmObject.getGsonAdapter());
        builder.registerTypeAdapter(OptionGroup.class,OptionGroup.getGsonAdapter());
    }
    
    public static CompositionObjectJSONAdapter getGsonAdapter(){
        return new CompositionObjectJSONAdapter();
    }
    
    private static class CompositionObjectJSONAdapter implements JsonDeserializer<CompositionObject>, JsonSerializer<CompositionObject> {
        @Override
        public JsonElement serialize(CompositionObject compositionObject, Type type, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            compositionObject.save(root,context);
            return root;
        }
    
        @Override
        public CompositionObject deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonObject))
                throw new JsonParseException("Expected JSONObject as channel root");
            JsonObject root = (JsonObject)json;
            CompositionObject object;
            if(type.equals(TextObject.class)){
                object = new TextObject();
            } else if (type.equals(Option.class)){
                object = new Option();
            } else if(type.equals(ConfirmObject.class)){
                object = new ConfirmObject();
            } else if(type.equals(OptionGroup.class)){
                object = new OptionGroup();
            } else {
                throw new JsonParseException("Cant load unknown Composition type");
            }
            object.load(root,context);
            return object;
        }
    }
    
    protected abstract JsonElement save(JsonObject root, JsonSerializationContext context);
}
