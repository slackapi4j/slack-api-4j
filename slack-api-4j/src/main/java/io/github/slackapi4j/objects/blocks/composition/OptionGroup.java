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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.slackapi4j.internal.Utilities;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Narimm on 21/02/2019.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OptionGroup extends CompositionObject {
  private TextObject label;
  private List<Option> options;

  @Override
  protected JsonElement save(final JsonObject root, final JsonSerializationContext context) {
    Utilities.serializeTextObject(root, "label", label, context);
    final JsonArray arr = new JsonArray();
    for (final Option opt : options) {
      arr.add(context.serialize(opt, Option.class));
    }
    root.add("options", arr);
    return root;
  }

  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    label = Utilities.getTextObject(root.get("label"), context, TextObject.TextType.PLAIN);
    final JsonArray array = root.getAsJsonArray("options");
    options = new ArrayList<>();
    for (final JsonElement el : array) {
      options.add(context.deserialize(el, Option.class));
    }
  }
}
