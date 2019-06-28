package io.github.slackapi4j.objects.blocks.elements;

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
        builder.registerTypeAdapter(SelectElement.class, getGsonAdapter());
        builder.registerTypeAdapter(ImageElement.class,ImageElement.getGsonAdapter());
        builder.registerTypeAdapter(ButtonElement.class, getGsonAdapter());
    }

    public static List<Element> deserialize(JsonArray arr, JsonDeserializationContext context) {
        List<Element> result = new ArrayList<>();
        for (JsonElement el : arr) {
            Element obj;
            try {
                ElementType type = ElementType.valueOf(Utilities.getAsString(el.getAsJsonObject().get("type")).toUpperCase());
                switch (type) {
                    case IMAGE:
                        obj = context.deserialize(el, ImageElement.class);
                        break;
                    case BUTTON:
                        obj = context.deserialize(el, ButtonElement.class);
                        break;
                    case STATIC_SELECT:
                        obj = context.deserialize(el, SelectElement.class);
                    default:
                        throw new JsonParseException("Could not decode element");
                }
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Could not parse Element: " + e.getMessage());
            }
            result.add(obj);
        }
        return result;
    }
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        this.type = ElementType.valueOf(Utilities.getAsString(root.get("type")).toUpperCase());
    }

    public static ElementJSONAdaptor getGsonAdapter() {
        return new ElementJSONAdaptor();
    }
    
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        root.addProperty("type", this.type.toString().toLowerCase());
        return root;
    }
    
    private static class ElementJSONAdaptor implements JsonDeserializer<Element>, JsonSerializer<Element> {

        @Override
        public Element deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonObject)) {
                throw new JsonParseException("Expected JSONObject as channel root");
            }
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
    
    public enum ElementType{
        IMAGE,
        STATIC_SELECT,
        BUTTON,
    }
}
