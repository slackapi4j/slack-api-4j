package io.github.slackapi4j.objects;

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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * The Basic object - that support serialization and deserialization via
 * {@link com.google.gson.Gson}
 * Created by benjamincharlton on 20/02/2019.
 */
public abstract class BaseObject {

  public static Object getGsonAdapter() {
    return new ChannelJsonAdapter();
  }

  protected abstract void load(JsonObject root, JsonDeserializationContext context);

  private static class ChannelJsonAdapter implements JsonDeserializer<BaseObject> {
    @Override
    @SuppressWarnings("deprecation")
    public BaseObject deserialize(final JsonElement element, final Type type,
                                  final JsonDeserializationContext context)
        throws JsonParseException {
      if (!(element instanceof JsonObject)) {
        throw new JsonParseException("Expected JSONObject as channel root");
      }

      final JsonObject root = (JsonObject) element;

      final BaseObject object;
      if (type.equals(User.class)) {
        object = new User();
      } else if (type.equals(Conversation.class)) {
        object = new Conversation();
      } else if (type.equals(GroupChannel.class)) {
        object = new GroupChannel();
      } else if (type.equals(NormalChannel.class)) {
        object = new NormalChannel();
      } else if (type.equals(DirectChannel.class)) {
        object = new DirectChannel();
      } else {
        throw new JsonParseException("Cant load unknown channel type");
      }

      object.load(root, context);
      return object;
    }
  }
}
