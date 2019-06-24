package au.com.addstar.slackapi.objects;

import au.com.addstar.slackapi.internal.Utilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String topic;
    private ObjectID topicUpdateUserId;
    private long topicUpdateDate;
    private boolean isUserDeleted;

    private String purpose;
    private ObjectID purposeUpdateUserId;
    private long purposeUpdateDate;

    private String normalized_name;
    private List<String> previous_names;
    private int num_members;


    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        isIM = Utilities.getAsBoolean(root.get("is_im"),false);
        members = new ArrayList<>();
        if(isIM == true){
            //this message is on a direct channel and as a result it wont have certain params
            creationUserId = new ObjectID(root.get("user").getAsString());
            members.add(creationUserId);
        }
        isChannel = Utilities.getAsBoolean(root.get("is_channel"),false);
        if(isChannel){
            name = root.get("name").getAsString();
            creationUserId = new ObjectID(root.get("creator").getAsString());
            isArchived = Utilities.getAsBoolean(root.get("is_archived"), false);
            isGeneral = Utilities.getAsBoolean(root.get("is_general"), false);
        }
        if (root.has("members"))
        {
            JsonArray memberArray = root.get("members").getAsJsonArray();
            List<ObjectID> memberList = new ArrayList<ObjectID>(memberArray.size());
            for (JsonElement member : memberArray)
                memberList.add(new ObjectID(member.getAsString()));
            members = memberList;
        }
        if (root.has("topic"))
        {
            JsonObject topic = root.get("topic").getAsJsonObject();
            this.topic = topic.get("value").getAsString();
            topicUpdateDate = Utilities.getAsTimestamp(topic.get("last_set"));
            topicUpdateUserId = new ObjectID(topic.get("creator").getAsString());
        }

        if (root.has("purpose"))
        {
            JsonObject purpose = root.get("purpose").getAsJsonObject();
            this.purpose = purpose.get("value").getAsString();
            purposeUpdateDate = Utilities.getAsTimestamp(purpose.get("last_set"));
            purposeUpdateUserId = new ObjectID(purpose.get("creator").getAsString());
        }
        isMember = Utilities.getAsBoolean(root.get("is_member"),false);
        isShared = Utilities.getAsBoolean(root.get("is_shared"),false);
        isPrivate = Utilities.getAsBoolean(root.get("is_private"),false);
        isPrivate = Utilities.getAsBoolean(root.get("is_private"),false);
        isOrgShared = Utilities.getAsBoolean(root.get("is_org_shared"),false);
        isMPIM = Utilities.getAsBoolean(root.get("is_mpim"),false);
        if(root.has("name_normalized")){
            normalized_name = root.get("name_normalized").getAsString();
        }
        if(root.has("previous_names")){
            JsonArray array = root.getAsJsonArray("previous_names");
            List<String> names = new ArrayList<>(array.size());
            for(JsonElement object: array){
                names.add(object.getAsString());
            }
            previous_names = names;
        }else{
            previous_names = Collections.emptyList();
        }
        if(root.has("num_member")){
            num_members = root.get("num_members").getAsInt();
        }
    }
}
