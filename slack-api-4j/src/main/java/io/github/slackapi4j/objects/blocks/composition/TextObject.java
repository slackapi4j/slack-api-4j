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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.slackapi4j.internal.SlackUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by benjamincharlton on 20/02/2019.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class TextObject extends CompositionObject {

  @Builder.Default
  private TextType type = TextType.PLAIN;
  private Boolean emoji;
  private Boolean verbatim;
  private String text;

  public TextObject() {
    super();
  }

  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    type = TextType.getTextType(root.get("type").getAsString());
    emoji = SlackUtil.getAsBoolean(root.get("emoji"), false);
    verbatim = SlackUtil.getAsBoolean(root.get("verbatim"), false);
    text = root.get("text").getAsString();
  }

  @Override
  protected JsonElement save(final JsonObject root, final JsonSerializationContext context) {
    root.addProperty("type", type.getValue());
    if (emoji != null && type == TextType.PLAIN) {
      root.addProperty("emoji", emoji);
    }
    if (verbatim != null) {
      root.addProperty("verbatim", verbatim);
    }
    root.addProperty("text", text);
    return root;
  }

  public enum TextType {
    PLAIN("plain_text"),
    MARKDOWN("mrkdwn");
    @Getter
    private final String value;

    TextType(final String value) {
      this.value = value;
    }

    /**
     * Returns a TextType from a String value.
     *
     * @param val a String to parse
     * @return TextType
     */
    public static TextType getTextType(final String val) {
      if (PLAIN.value.equals(val)) {
        return PLAIN;
      }
      if (MARKDOWN.value.equals(val)) {
        return MARKDOWN;
      } else {
        throw new JsonParseException("Invalid TextObject Type: " + val);
      }
    }
  }
}
