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

import io.github.slackapi4j.internal.Utilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
@EqualsAndHashCode(callSuper=true)
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
    private boolean isMPIM;
    private boolean isIM;
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
    private String normalized_name;
    private List<String> previous_names;
    private int num_members;


    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        this.isIM = Utilities.getAsBoolean(root.get("is_im"), false);
        this.members = new ArrayList<>();
        if (this.isIM) {
            //this message is on a direct channel and as a result it wont have certain params
            this.creationUserId = new ObjectID(root.get("user").getAsString());
            this.members.add(this.creationUserId);
        }
        this.isChannel = Utilities.getAsBoolean(root.get("is_channel"), false);
        if (this.isChannel) {
            this.name = root.get("name").getAsString();
            this.creationUserId = new ObjectID(root.get("creator").getAsString());
            this.isArchived = Utilities.getAsBoolean(root.get("is_archived"), false);
            this.isGeneral = Utilities.getAsBoolean(root.get("is_general"), false);
        }
        if (root.has("members"))
        {
            JsonArray memberArray = root.get("members").getAsJsonArray();
            List<ObjectID> memberList = new ArrayList<>(memberArray.size());
            for (JsonElement member : memberArray) {
                memberList.add(new ObjectID(member.getAsString()));
            }
            this.members = memberList;
        }
        if (root.has("topic"))
        {
            JsonObject topic = root.get("topic").getAsJsonObject();
            this.topic = topic.get("value").getAsString();
            this.topicUpdateDate = Utilities.getAsTimestamp(topic.get("last_set"));
            this.topicUpdateUserId = new ObjectID(topic.get("creator").getAsString());
        }

        if (root.has("purpose"))
        {
            JsonObject purpose = root.get("purpose").getAsJsonObject();
            this.purpose = purpose.get("value").getAsString();
            this.purposeUpdateDate = Utilities.getAsTimestamp(purpose.get("last_set"));
            this.purposeUpdateUserId = new ObjectID(purpose.get("creator").getAsString());
        }
        this.isMember = Utilities.getAsBoolean(root.get("is_member"), false);
        this.isShared = Utilities.getAsBoolean(root.get("is_shared"), false);
        this.isPrivate = Utilities.getAsBoolean(root.get("is_private"), false);
        this.isPrivate = Utilities.getAsBoolean(root.get("is_private"), false);
        this.isOrgShared = Utilities.getAsBoolean(root.get("is_org_shared"), false);
        this.isMPIM = Utilities.getAsBoolean(root.get("is_mpim"), false);
        if(root.has("name_normalized")){
            this.normalized_name = root.get("name_normalized").getAsString();
        }
        if(root.has("previous_names")){
            JsonArray array = root.getAsJsonArray("previous_names");
            List<String> names = new ArrayList<>(array.size());
            for(JsonElement object: array){
                names.add(object.getAsString());
            }
            this.previous_names = names;
        }else{
            this.previous_names = Collections.emptyList();
        }
        this.num_members = Utilities.getAsInt(root.get("num_members"));

    }
}
