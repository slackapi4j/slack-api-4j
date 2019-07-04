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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.slackapi4j.internal.SlackUtil;
import io.github.slackapi4j.objects.BaseObject;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import io.github.slackapi4j.objects.blocks.elements.ButtonElement;
import io.github.slackapi4j.objects.blocks.elements.Element;
import io.github.slackapi4j.objects.blocks.elements.ImageElement;
import io.github.slackapi4j.objects.blocks.elements.SelectElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Context Blocks provide elements that allow a contextual response.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ContextBlock extends Block {
  private List<BaseObject> elements;

  public ContextBlock() {
    super();
    setType(BlockType.CONTEXT);
  }

  private static List<BaseObject> deserializeContextElements(
      final JsonArray array, final JsonDeserializationContext context) {
    final List<BaseObject> o = new ArrayList<>();
    for (final JsonElement el : array) {
      Type type = null;
      try {
        final Element.ElementType elType = Element.ElementType.valueOf(
            SlackUtil.getAsString(el.getAsJsonObject().get("type")).toUpperCase());
        switch (elType) {
          case IMAGE:
            type = ImageElement.class;
            break;
          case BUTTON:
            type = ButtonElement.class;
            break;
          case STATIC_SELECT:
            type = SelectElement.class;
            break;
          default:
            throw new JsonParseException("Could not decode element");
        }
      } catch (final IllegalArgumentException ignored) {
        //actually shouldn't happen
      }
      try {
        final TextObject.TextType textType = TextObject.TextType.getTextType(
            SlackUtil.getAsString(el.getAsJsonObject().get("type")));
        switch (textType) {
          case PLAIN:
          case MARKDOWN:
            type = TextObject.class;
            break;
          default:
            throw new JsonParseException("Could not decode element");
        }
      } catch (final IllegalArgumentException ignored) {
        //actually shouldn't happen
      }
      if (type == null) {
        throw new JsonParseException("Could not decode element");
      }
      final BaseObject obj = context.deserialize(el, type);
      o.add(obj);
    }
    return o;
  }

  /**
   * Add a object to the element array.
   *
   * @param obj the base object
   * @return true if successful
   */
  public boolean addElement(final BaseObject obj) {
    if (obj instanceof TextObject || obj instanceof Element) {
      return elements.add(obj);
    }
    return false;
  }

  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    super.load(root, context);
    elements = deserializeContextElements(root.getAsJsonArray("elements"), context);

  }

  @Override
  protected JsonObject save(final JsonObject root, final JsonSerializationContext context) {
    super.save(root, context);
    final JsonArray array = new JsonArray();
    for (final BaseObject o : elements) {
      final JsonElement obj;
      if (o instanceof TextObject) {
        obj = context.serialize(o, TextObject.class);
      } else if (o instanceof ImageElement) {
        obj = context.serialize(o, ImageElement.class);
      } else if (o instanceof SelectElement) {
        obj = context.serialize(o, SelectElement.class);
      } else if (o instanceof ButtonElement) {
        obj = context.serialize(o, ButtonElement.class);
      } else {
        throw new JsonParseException("ContextElement not serialize:" + o.getClass());
      }
      array.add(obj);
    }
    root.add("elements", array);
    return root;
  }

}
