package au.com.addstar.slackapi.objects.blocks.elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import au.com.addstar.slackapi.internal.Utilities;
import au.com.addstar.slackapi.objects.BaseObject;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/02/2019.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public abstract class Element extends BaseObject {

    private ElementType type;
    
    public static void addGsonAdapters(GsonBuilder builder)
    {
        builder.registerTypeAdapter(SelectElement.class, SelectElement.getGsonAdapter());
        builder.registerTypeAdapter(ImageElement.class,ImageElement.getGsonAdapter());
        builder.registerTypeAdapter(ButtonElement.class,ButtonElement.getGsonAdapter());
    }
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
       type = ElementType.valueOf(Utilities.getAsString(root.get("type")).toUpperCase());
    }
    
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        root.addProperty("type",type.toString().toLowerCase());
        return root;
    }
    
    public static ElementJSONAdaptor getGsonAdapter(){
        return new ElementJSONAdaptor();
    }
    
    private static class ElementJSONAdaptor implements JsonDeserializer<Element>, JsonSerializer<Element> {
        
        @Override
        public Element deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonObject))
                throw new JsonParseException("Expected JSONObject as channel root");
            final JsonObject root = (JsonObject)json;
            final Element object;
            if(type.equals(ImageElement.class)) {
                object = new ImageElement();
            } else  if(type.equals(SelectElement.class)) {
                object = new SelectElement();
            } else  if(type.equals(ButtonElement.class)) {
                object = new ButtonElement();
            }
            else {
                throw new JsonParseException("Cant load unknown channel type");
            }
            object.load(root,context);
            return object;
        }
        
        @Override
        public JsonElement serialize(Element element, Type type, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            element.save(root,context);
            return root;
        }
    }
    
    public static List<Element> deserialize(JsonArray arr, JsonDeserializationContext context){
        List<Element> result = new ArrayList<>();
        for(JsonElement el:arr) {
            Element obj;
            try {
                ElementType type = ElementType.valueOf(Utilities.getAsString(el.getAsJsonObject().get("type")).toUpperCase());
                switch(type){
                    case IMAGE:
                        obj = context.deserialize(el,ImageElement.class);
                        break;
                    case BUTTON:
                        obj = context.deserialize(el,ButtonElement.class);
                        break;
                    case STATIC_SELECT:
                        obj = context.deserialize(el,SelectElement.class);
                    default:
                        throw new JsonParseException("Could not decode element");
                };
            }catch (IllegalArgumentException e){
                throw new JsonParseException("Could not parse Element: "+e.getMessage());
            }
            result.add(obj);
        }
        return result;
    }
    
    public enum ElementType{
        IMAGE,
        STATIC_SELECT,
        BUTTON,
    }
}
