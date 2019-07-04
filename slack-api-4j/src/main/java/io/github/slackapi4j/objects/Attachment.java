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

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.net.URL;
import java.util.List;

@Data
@Deprecated
public class Attachment {
  private final List<AttachmentField> fields = Lists.newArrayList();
  // Header
  private @NonNull String fallback;
  private String color;
  private String pretext;
  // Title
  private String title;
  @SerializedName("title_link")
  private URL titleLink;
  // Author
  @SerializedName("author_name")
  private String authorName;
  @SerializedName("author_link")
  private URL authorLink;
  @SerializedName("author_icon")
  private URL authorIcon;
  // Body
  private String text;
  @SerializedName("image_url")
  private URL image;
  @SerializedName("mrkdwn_in")
  private MarkDownFormats formats;

  public Attachment(final String fallback) {
    this.fallback = fallback;
  }

  public void addField(final AttachmentField field) {
    fields.add(field);
  }

  @Data
  @AllArgsConstructor
  @EqualsAndHashCode
  @ToString
  @Deprecated
  public static class AttachmentField {
    private @NonNull String title;
    private @NonNull String value;
    private boolean isShort;
  }

  @Data
  @NoArgsConstructor
  @EqualsAndHashCode
  @ToString
  @Deprecated
  public static class MarkDownFormats {
    @SerializedName("pretext")
    private boolean formatPretext;
    @SerializedName("text")
    private boolean formatText;
    @SerializedName("fields")
    private boolean formatFields;
  }

}
