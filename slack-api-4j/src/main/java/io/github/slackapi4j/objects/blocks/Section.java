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
import com.google.gson.JsonSerializationContext;
import io.github.slackapi4j.internal.SlackUtil;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import io.github.slackapi4j.objects.blocks.elements.Element;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamincharlton on 20/02/2019.
 */

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Section extends Block {
  private TextObject text;
  private List<TextObject> fields;
  private Element accessory;

  public Section() {
    super();
    super.setType(BlockType.SECTION);
  }

  @Override
  protected JsonObject save(final JsonObject root, final JsonSerializationContext context) {
    super.save(root, context);
    root.add("text", context.serialize(text));
    if (fields != null && !fields.isEmpty()) {
      final JsonArray out = new JsonArray();
      for (final TextObject t : fields) {
        out.add(context.serialize(t));
      }
      root.add("fields", out);
    }
    if (accessory != null) {
      root.add("accessory", context.serialize(accessory));
    }
    return root;
  }

  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    super.load(root, context);
    setText(context.deserialize(root.get("text"), TextObject.class));
    if (root.has("fields")) {
      final JsonArray ar = root.getAsJsonArray("fields");
      setFields(new ArrayList<>());
      for (final JsonElement el : ar) {
        fields.add(SlackUtil.getTextObject(el, context, null));
      }
    } else {
      setFields(null);
    }
    if (root.has("accessory")) {
      setAccessory(context.deserialize(root.get("accessory"), Element.class));
    }
  }
}
