package io.github.slackapi4j.objects.blocks.composition;

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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.slackapi4j.objects.BaseObject;

import java.lang.reflect.Type;

/**
 * These objects are used to compose message blocks
 * Created by Narimm on 21/02/2019.
 */
public abstract class CompositionObject extends BaseObject {

  /**
   * Add Json adapters to a {@link GsonBuilder} for this class.
   *
   * @param builder the builder to modify
   */
  public static void addGsonAdapters(final GsonBuilder builder) {
    builder.registerTypeAdapter(TextObject.class, getGsonAdapter());
    builder.registerTypeAdapter(Option.class, getGsonAdapter());
    builder.registerTypeAdapter(ConfirmObject.class, getGsonAdapter());
    builder.registerTypeAdapter(OptionGroup.class, getGsonAdapter());
  }

  public static CompositionObjectJsonAdapter getGsonAdapter() {
    return new CompositionObjectJsonAdapter();
  }

  protected abstract JsonElement save(JsonObject root, JsonSerializationContext context);

  private static class CompositionObjectJsonAdapter
      implements JsonDeserializer<CompositionObject>, JsonSerializer<CompositionObject> {
    @Override
    public JsonElement serialize(final CompositionObject compositionObject,
                                 final Type type, final JsonSerializationContext context) {
      final JsonObject root = new JsonObject();
      compositionObject.save(root, context);
      return root;
    }

    @Override
    public CompositionObject deserialize(final JsonElement json, final Type type,
                                         final JsonDeserializationContext context)
        throws JsonParseException {
      if (!(json instanceof JsonObject)) {
        throw new JsonParseException("Expected JSONObject as channel root");
      }
      final JsonObject root = (JsonObject) json;
      final CompositionObject object;
      if (type.equals(TextObject.class)) {
        object = new TextObject();
      } else if (type.equals(Option.class)) {
        object = new Option();
      } else if (type.equals(ConfirmObject.class)) {
        object = new ConfirmObject();
      } else if (type.equals(OptionGroup.class)) {
        object = new OptionGroup();
      } else {
        throw new JsonParseException("Cant load unknown Composition type");
      }
      object.load(root, context);
      return object;
    }
  }
}
