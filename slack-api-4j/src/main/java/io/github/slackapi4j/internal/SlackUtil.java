package io.github.slackapi4j.internal;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.slackapi4j.objects.blocks.composition.TextObject;

import java.util.Collections;
import java.util.Map;

public class SlackUtil {
  /**
   * So I dont have to force type Collections.emptyMap() for parameters
   */
  public static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();

  /**
   * Parses the element as a unix timestamp.
   *
   * @param element The element to convert
   * @return The time in milliseconds
   */
  public static long getAsTimestamp(final JsonElement element) {
    final String raw = element.getAsString();

    final long time;
    if (raw.contains(".")) {
      final double precTime = Double.parseDouble(raw);
      time = (long) (precTime * 1000);
    } else {
      time = Long.parseLong(raw) * 1000;
    }

    return time;
  }

  /**
   * Retrieve a TextObject.
   *
   * @param element the JSon Element
   * @param context JsonDeserializationContext
   * @param type    Type of object
   * @return TextObject
   */

  public static TextObject getTextObject(final JsonElement element,
                                         final JsonDeserializationContext context,
                                         final TextObject.TextType type) {
    final TextObject textObject = context.deserialize(element, TextObject.class);
    if (type == null) {
      return textObject;
    } else {
      if (textObject.getType() != type) {
        throw new JsonParseException("Invalid textType: " + textObject.getType().name());
      } else {
        return textObject;
      }
    }
  }

  /**
   * Serialize the object.
   *
   * @param root    the root to add the object oo
   * @param name    the name of the object
   * @param object  the object to serialize
   * @param context JsonSerializationContext
   */
  public static void serializeTextObject(final JsonObject root, final String name,
                                         final TextObject object,
                                         final JsonSerializationContext context) {
    if (object == null) {
      return;
    }
    root.add(name, context.serialize(object, TextObject.class));
  }

  /**
   * Similar to String.
   *
   * @param element the element to retrieve the string for
   * @return a String from the element
   */
  public static String getAsString(final JsonElement element) {
    if (element == null || element instanceof JsonNull) {
      return null;
    }

    return element.getAsString();
  }

  /**
   * Get as a boolean.
   *
   * @param element the element to retrieve the boolean for
   * @param def     the default if the element is null
   * @return the value or the default provided
   */

  public static boolean getAsBoolean(final JsonElement element, final boolean def) {
    if (element == null || element instanceof JsonNull) {
      return def;
    }

    return element.getAsBoolean();
  }

  /**
   * Get as a int.
   *
   * @param element the element to retrieve the int for
   * @return the value
   **/

  public static int getAsInt(final JsonElement element) {
    if (element == null || element instanceof JsonNull) {
      return 0;
    }

    return element.getAsInt();
  }
}
