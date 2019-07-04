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
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.slackapi4j.internal.SlackUtil;
import io.github.slackapi4j.objects.blocks.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * An object that represents a message sent in a conversation.
 * A builder is created for the object
 */
@Builder
@Getter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Message extends IdBaseObject {
  /**
   * The user object.
   */
  @Setter
  private ObjectID userId;
  /**
   * The text of  the message.
   */
  @Setter
  private String text;
  /**
   * The conversion that represents the source or destination of the message. -
   * generally a conversation (which could be private or public
   */
  private ObjectID conversationID;
  /**
   * When the message was created.
   */
  private long timestamp;
  /**
   * When the message was posted to the thread/conversation.
   */
  @Setter
  private String threadTs;
  /**
   * String representation of the timestamp.
   */
  private String ts;
  /**
   * The MessageType.
   */
  @Setter
  private MessageType subtype;
  /**
   * The last user to edit the message.
   */
  private ObjectID editUserId;
  /**
   * Time stamp of the edits.
   */
  private long editTimestamp;
  /**
   * Message sent as a user.
   */
  @Builder.Default
  private boolean asUser = false;
  /**
   * A list of attachments.
   *
   * @deprecated use {@code Message#blocks}
   */
  @Deprecated
  @Setter
  private List<Attachment> attachments;
  /**
   * A list of blocks to send.
   */
  @Setter
  private List<Block> blocks;

  /**
   * Create A Message ...generally its better to use the {@link Message#builder()}.
   */
  public Message() {
    super();
    subtype = MessageType.Normal;
    asUser = true;
  }

  /**
   * Constructs a message.
   *
   * @param text    the text
   * @param channel the channel to send it too
   */
  public Message(final String text, final IdBaseObject channel) {
    super();
    conversationID = channel.getId();
    this.text = text;
    subtype = MessageType.Sent;
    asUser = false;
  }

  public static Object getGsonAdapter() {
    return new MessageJsonAdapter();
  }

  public void addBlock(final Block block) {
    blocks.add(block);
  }

  @Override
  public String toString() {
    return String.format("%s: '%s' from %s", subtype, text, userId);
  }

  public enum MessageType {
    Normal(""),
    Sent(""),
    FromBot("bot_message"),
    FromMeCommand("me_message"),

    Edit("message_changed"),
    Delete("message_deleted"),

    ChannelJoin("channel_join"),
    ChannelLeave("channel_leave"),
    ChannelTopic("channel_topic"),
    ChannelPurpose("channel_purpose"),
    ChannelName("channel_name"),
    ChannelArchive("channel_archive"),
    ChannelUnarchive("channel_unarchive"),

    GroupJoin("group_join"),
    GroupLeave("group_leave"),
    GroupTopic("group_topic"),
    GroupPurpose("group_purpose"),
    GroupName("group_name"),
    GroupArchive("group_archive"),
    GroupUnarchive("group_unarchive"),

    FileShare("file_share"),
    FileComment("file_comment"),
    FileMention("file_mention");

    private final String id;

    MessageType(final String id) {
      this.id = id;
    }

    static MessageType fromId(final String id) {
      if (id == null) {
        return Normal;
      }

      for (final MessageType type : values()) {
        if (type.id.equals(id)) {
          return type;
        }
      }

      return Normal;
    }
  }

  private static class MessageJsonAdapter implements JsonDeserializer<Message>,
      JsonSerializer<Message> {
    @Override
    @SuppressWarnings("deprecation")
    public Message deserialize(final JsonElement element, final Type typeOfT,
                               final JsonDeserializationContext context) throws JsonParseException {
      if (!(element instanceof JsonObject)) {
        throw new JsonParseException("Expected JSONObject as message root");
      }

      final JsonObject root = (JsonObject) element;

      final Message message = new Message();
      if (root.has("user")) {
        message.userId = new ObjectID(root.get("user").getAsString());
      }

      message.text = SlackUtil.getAsString(root.get("text"));
      message.threadTs = SlackUtil.getAsString(root.get("thread_ts"));
      message.ts = SlackUtil.getAsString(root.get("ts"));
      message.asUser = SlackUtil.getAsBoolean(root.get("as_user"), true);
      message.timestamp = SlackUtil.getAsTimestamp(root.get("ts"));
      if (root.has("channel")) {
        message.conversationID = new ObjectID(root.get("channel").getAsString());
      }

      if (root.has("edited")) {
        final JsonObject edited = root.getAsJsonObject("edited");
        message.editUserId = new ObjectID(edited.get("user").getAsString());
        message.editTimestamp = SlackUtil.getAsTimestamp(edited.get("ts"));
      }

      message.subtype = MessageType.fromId(SlackUtil.getAsString(root.get("subtype")));

      if (root.has("attachments")) {
        message.attachments = Lists.newArrayList();
        final JsonArray attachments = root.getAsJsonArray("attachments");
        for (final JsonElement rawAttachment : attachments) {
          message.attachments.add(context.deserialize(rawAttachment, Attachment.class));
        }
      }
      if (root.has("blocks")) {
        message.blocks = Lists.newArrayList();
        final JsonArray blocks = root.getAsJsonArray("blocks");
        for (final JsonElement rawBlock : blocks) {
          message.blocks.add(context.deserialize(rawBlock, Block.class));
        }
      }
      return message;
    }

    @Override
    public JsonElement serialize(final Message src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
      final JsonObject object = new JsonObject();
      object.addProperty("type", "message");
      object.addProperty("channel", src.conversationID.toString());
      object.addProperty("text", src.text);
      object.addProperty("thread_ts", src.threadTs);
      object.addProperty("as_user", src.asUser);
      if (src.userId != null) {
        object.addProperty("user", src.userId.toString());
      }
      if (src.attachments != null) {
        final JsonArray attachments = new JsonArray();
        for (final Attachment attachment : src.attachments) {
          attachments.add(context.serialize(attachment));
        }
        object.add("attachments", attachments);
      }
      if (src.blocks != null) {
        final JsonArray blocks = new JsonArray();
        for (final Block block : src.blocks) {
          blocks.add(context.serialize(block));
        }
        object.add("blocks", blocks);
      }
      return object;
    }
  }
}
