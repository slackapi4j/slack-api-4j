package io.github.slackapi4j.objects.blocks;

/*-
 * #%L
 * slack-api-4j
 * %%
 * Copyright (C) 2018 - 2019 SlackApi4J
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
