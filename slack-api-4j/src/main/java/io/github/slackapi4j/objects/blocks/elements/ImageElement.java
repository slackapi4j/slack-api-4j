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
import com.google.gson.annotations.SerializedName;
import io.github.slackapi4j.internal.SlackUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Narimm on 20/02/2019.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ImageElement extends Element {
  @SerializedName("imageURL")
  private URL imageUrl;
  @SerializedName("alt_text")
  private String altText;

  public ImageElement() {
    super();
    setType(ElementType.IMAGE);
  }

  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    super.load(root, context);
    try {
      imageUrl = new URL(root.get("imageURL").getAsString());
    } catch (final MalformedURLException e) {
      throw new JsonParseException("URL could not be decoded", e);
    }
    altText = SlackUtil.getAsString(root.get("alt_text"));
  }

  @Override
  protected JsonObject save(final JsonObject root, final JsonSerializationContext context) {
    super.save(root, context);
    if (imageUrl != null) {
      root.addProperty("imageUrl", imageUrl.toString());
    }
    if (altText != null) {
      root.addProperty("alt_text", altText);
    }
    return root;
  }
}
