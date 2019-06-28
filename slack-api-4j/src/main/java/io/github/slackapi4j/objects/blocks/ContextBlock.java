package io.github.slackapi4j.objects.blocks;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.slackapi4j.internal.Utilities;
import io.github.slackapi4j.objects.BaseObject;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import io.github.slackapi4j.objects.blocks.elements.ButtonElement;
import io.github.slackapi4j.objects.blocks.elements.Element;
import io.github.slackapi4j.objects.blocks.elements.ImageElement;
import io.github.slackapi4j.objects.blocks.elements.SelectElement;
import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ContextBlock extends Block {
    private List<BaseObject> elements;

    public ContextBlock() {
        this.setType(BlockType.CONTEXT);
    }
    
    private static List<BaseObject> deserializeContextElements(JsonArray array, JsonDeserializationContext context){
        List<BaseObject> o = new ArrayList<>();
        for (JsonElement el:array){
            Type type = null;
            try {
                Element.ElementType elType = Element.ElementType.valueOf(Utilities.getAsString(el.getAsJsonObject().get("type")).toUpperCase());
                switch (elType){
                    case IMAGE:
                        type = ImageElement.class;
                        break;
                    case BUTTON:
                        type = ButtonElement.class;
                        break;
                    case STATIC_SELECT:
                        type = SelectElement.class;
                    default:
                        throw new JsonParseException("Could not decode element");
                }
            } catch (IllegalArgumentException ignored) {
            }
            try{
                TextObject.TextType textType = TextObject.TextType.getTextType(Utilities.getAsString(el.getAsJsonObject().get("type")));
                switch (textType) {
                    case PLAIN:
                    case MARKDOWN:
                        type = TextObject.class;
                    default:
                        throw new JsonParseException("Could not decode element");
                }
            } catch (IllegalArgumentException ignored) {
            }
            if(type == null) {
                throw new JsonParseException("Could not decode element");
            }
            BaseObject obj = context.deserialize(el,type);
            o.add(obj);
        }
        return o;
    }

    public boolean addElement(BaseObject obj) {
        if (obj instanceof TextObject || obj instanceof Element) {
            return this.elements.add(obj);
        }
        return false;
    }
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        this.elements = deserializeContextElements(root.getAsJsonArray("elements"),context);
        
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        super.save(root, context);
        JsonArray array = new JsonArray();
        for (BaseObject o : this.elements) {
            JsonElement obj;
            if(o instanceof TextObject) {
                obj = context.serialize(o, TextObject.class);
            } else if (o instanceof ImageElement) {
                obj = context.serialize(o,ImageElement.class);
            } else if (o instanceof SelectElement){
                obj =  context.serialize(o,SelectElement.class);
            } else if (o instanceof ButtonElement) {
                obj = context.serialize(o,ButtonElement.class);
            } else{
                throw new JsonParseException("ContextElement not serialize:" +o.getClass());
            }
            array.add(obj);
        }
        root.add("elements",array);
        return root;
    }
    
}
