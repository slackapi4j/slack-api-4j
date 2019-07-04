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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.slackapi4j.internal.SlackUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by benjamincharlton on 26/08/2018.
 * Conversations replace Channels, IM's and Group IMs
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class Conversation extends TimeStampedBaseObject {

  private String name;
  private boolean isChannel;
  private ObjectID creationUserId;
  private boolean isArchived;
  private boolean isGeneral;
  private boolean isShared;
  private boolean isOrgShared;
  private boolean isMember;
  private boolean isPrivate;
  private boolean isMpIm;
  private boolean isIm;
  private List<ObjectID> members;
  @Nullable
  private String topic;
  private ObjectID topicUpdateUserId;
  private long topicUpdateDate;
  private boolean isUserDeleted;
  @Nullable
  private String purpose;
  private ObjectID purposeUpdateUserId;
  private long purposeUpdateDate;
  @Nullable
  private String normalizedName;
  private List<String> previousNames;
  private int numMembers;


  @Override
  protected void load(final JsonObject root, final JsonDeserializationContext context) {
    super.load(root, context);
    isIm = SlackUtil.getAsBoolean(root.get("is_im"), false);
    members = new ArrayList<>();
    if (isIm) {
      //this message is on a direct channel and as a result it wont have certain params
      creationUserId = new ObjectID(root.get("user").getAsString());
      members.add(creationUserId);
    }
    isChannel = SlackUtil.getAsBoolean(root.get("is_channel"), false);
    if (isChannel) {
      name = root.get("name").getAsString();
      creationUserId = new ObjectID(root.get("creator").getAsString());
      isArchived = SlackUtil.getAsBoolean(root.get("is_archived"), false);
      isGeneral = SlackUtil.getAsBoolean(root.get("is_general"), false);
    }
    if (root.has("members")) {
      final JsonArray memberArray = root.get("members").getAsJsonArray();
      final List<ObjectID> memberList = new ArrayList<>(memberArray.size());
      for (final JsonElement member : memberArray) {
        memberList.add(new ObjectID(member.getAsString()));
      }
      members = memberList;
    }
    if (root.has("topic")) {
      final JsonObject topic = root.get("topic").getAsJsonObject();
      this.topic = topic.get("value").getAsString();
      topicUpdateDate = SlackUtil.getAsTimestamp(topic.get("last_set"));
      topicUpdateUserId = new ObjectID(topic.get("creator").getAsString());
    }

    if (root.has("purpose")) {
      final JsonObject purpose = root.get("purpose").getAsJsonObject();
      this.purpose = purpose.get("value").getAsString();
      purposeUpdateDate = SlackUtil.getAsTimestamp(purpose.get("last_set"));
      purposeUpdateUserId = new ObjectID(purpose.get("creator").getAsString());
    }
    isMember = SlackUtil.getAsBoolean(root.get("is_member"), false);
    isShared = SlackUtil.getAsBoolean(root.get("is_shared"), false);
    isPrivate = SlackUtil.getAsBoolean(root.get("is_private"), false);
    isPrivate = SlackUtil.getAsBoolean(root.get("is_private"), false);
    isOrgShared = SlackUtil.getAsBoolean(root.get("is_org_shared"), false);
    isMpIm = SlackUtil.getAsBoolean(root.get("is_mpim"), false);
    if (root.has("name_normalized")) {
      normalizedName = root.get("name_normalized").getAsString();
    }
    if (root.has("previous_names")) {
      final JsonArray array = root.getAsJsonArray("previous_names");
      final List<String> names = new ArrayList<>(array.size());
      for (final JsonElement object : array) {
        names.add(object.getAsString());
      }
      previousNames = names;
    } else {
      previousNames = Collections.emptyList();
    }
    numMembers = SlackUtil.getAsInt(root.get("num_members"));

  }
}
