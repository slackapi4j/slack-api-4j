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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.slackapi4j.internal.Utilities;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *Created by Narimm on 21/02/2019.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ButtonElement extends Element {

  private TextObject text;
  private String actionId;
  private URL url;
  private String value;
  private Object confirm;

  public ButtonElement() {
    super();
    setType(ElementType.BUTTON);
  }

  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    super.load(root, context);
    text = Utilities.getTextObject(root.get("text"), context, TextObject.TextType.PLAIN);
    actionId = Utilities.getAsString(root.get("action_id"));
    value = Utilities.getAsString(root.get("value"));
    try {
      url = new URL(root.get("url").getAsString());
    } catch (final MalformedURLException e) {
      throw new JsonParseException("URL could not be decoded");
    }
  }

  @Override
  protected JsonObject save(final JsonObject root, final JsonSerializationContext context) {
    return super.save(root, context);
  }
}
