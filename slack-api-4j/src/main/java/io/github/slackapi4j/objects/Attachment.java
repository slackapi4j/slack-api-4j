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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.slackapi4j.internal.Utilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.lang.reflect.Type;
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
  private URL titleLink;
  // Author
  private String authorName;
  private URL authorLink;
  private URL authorIcon;
  // Body
  private String text;
  private URL image;
  private boolean formatPretext;
  private boolean formatText;
  private boolean formatFields;

  public Attachment(final String fallback) {
    this.fallback = fallback;
  }

  public static void addGsonAdapters(final GsonBuilder builder) {
    builder.registerTypeAdapter(Attachment.class, new AttachmentJsonAdapter());
    builder.registerTypeAdapter(AttachmentField.class, new AttachmentFieldJsonAdapter());
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

  private static class AttachmentJsonAdapter implements JsonDeserializer<Attachment>,
      JsonSerializer<Attachment> {
    @Override
    public JsonElement serialize(final Attachment src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
      final JsonObject root = new JsonObject();
      root.addProperty("fallback", src.fallback);

      if (src.color != null) {
        root.addProperty("color", src.color);
      }
      if (src.pretext != null) {
        root.addProperty("pretext", src.pretext);
      }
      if (src.text != null) {
        root.addProperty("text", src.text);
      }

      if (src.authorName != null) {
        root.addProperty("author_name", src.authorName);
        if (src.authorLink != null) {
          root.addProperty("author_link", src.authorLink.toExternalForm());
        }
        if (src.authorIcon != null) {
          root.addProperty("author_icon", src.authorIcon.toExternalForm());
        }
      }

      if (src.title != null) {
        root.addProperty("title", src.title);

        if (src.titleLink != null) {
          root.addProperty("title_link", src.titleLink.toExternalForm());
        }
      }

      if (src.image != null) {
        root.addProperty("image_url", src.image.toExternalForm());
      }

      if (!src.fields.isEmpty()) {
        final JsonArray fields = new JsonArray();
        for (final AttachmentField field : src.fields) {
          fields.add(context.serialize(field));
        }
        root.add("fields", fields);
      }

      final JsonArray formats = new JsonArray();
      if (src.formatPretext) {
        formats.add(new JsonPrimitive("pretext"));
      }
      if (src.formatText) {
        formats.add(new JsonPrimitive("text"));
      }
      if (src.formatFields) {
        formats.add(new JsonPrimitive("fields"));
      }

      if (formats.size() > 0) {
        root.add("mrkdwn_in", formats);
      }

      return root;
    }

    @Override
    public Attachment deserialize(final JsonElement json, final Type typeOfT,
                                  final JsonDeserializationContext context)
        throws JsonParseException {
      final JsonObject object = json.getAsJsonObject();

      final Attachment attachment = new Attachment(object.get("fallback").getAsString());
      attachment.color = Utilities.getAsString(object.get("color"));
      attachment.pretext = Utilities.getAsString(object.get("pretext"));
      attachment.text = Utilities.getAsString(object.get("text"));

      attachment.authorName = Utilities.getAsString(object.get("author_name"));
      if (object.has("author_link")) {
        attachment.authorLink = context.deserialize(object.get("author_link"), URL.class);
      }
      if (object.has("author_icon")) {
        attachment.authorIcon = context.deserialize(object.get("author_link"), URL.class);
      }

      attachment.title = Utilities.getAsString(object.get("title"));
      if (object.has("title_link")) {
        attachment.titleLink = context.deserialize(object.get("title_link"), URL.class);
      }

      if (object.has("image_url")) {
        attachment.image = context.deserialize(object.get("image_url"), URL.class);
      }

      if (object.has("fields")) {
        final JsonArray fields = object.getAsJsonArray("fields");
        for (final JsonElement rawField : fields) {
          attachment.addField(context.deserialize(rawField, AttachmentField.class));
        }
      }

      return attachment;
    }
  }

  private static class AttachmentFieldJsonAdapter implements JsonSerializer<AttachmentField>,
      JsonDeserializer<AttachmentField> {
    @Override
    public AttachmentField deserialize(final JsonElement json, final Type typeOfT,
                                       final JsonDeserializationContext context)
        throws JsonParseException {
      final JsonObject root = json.getAsJsonObject();
      return new AttachmentField(root.get("title").getAsString(), root.get("value").getAsString(),
          Utilities.getAsBoolean(root.get("short"), false));
    }

    @Override
    public JsonElement serialize(final AttachmentField src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
      final JsonObject root = new JsonObject();
      root.addProperty("title", src.title);
      root.addProperty("value", src.value);
      root.addProperty("short", src.isShort);

      return root;
    }

  }
}
