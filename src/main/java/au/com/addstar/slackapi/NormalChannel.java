package au.com.addstar.slackapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import au.com.addstar.slackapi.internal.Utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper=true)
public class NormalChannel extends BaseChannel
{
    private String name;
    private ObjectID creationUserId;
    private boolean isArchived;
    private boolean isGeneral;
    
    private List<ObjectID> memberIds;
    private boolean isClientMember;
    
    private String topic;
    private ObjectID topicUpdateUserId;
    private long topicUpdateDate;
    
    private String purpose;
    private ObjectID purposeUpdateUserId;
    private long purposeUpdateDate;
    
    // Member only values
    private long lastRead;
    private int unreadCount;
    private int unreadCountDisplay;
    
    private Message latest;
    
    @Override
    protected void load(final JsonObject root, final JsonDeserializationContext context )
    {
        super.load(root, context);
        
        this.name = Utilities.getAsString(root.get("name"));
        this.creationUserId = new ObjectID(root.get("creator").getAsString());
        this.isArchived = Utilities.getAsBoolean(root.get("is_archived"), false);
        this.isGeneral = Utilities.getAsBoolean(root.get("is_general"), false);
        
        if (root.has("members"))
        {
            final JsonArray members = root.get("members").getAsJsonArray();
            final List<ObjectID> memberList = new ArrayList<>(members.size());
            for (final JsonElement member : members) {
                memberList.add(new ObjectID(member.getAsString()));
            }
            this.memberIds = memberList;
        }
        else {
            this.memberIds = Collections.emptyList();
        }
        
        if (root.has("topic"))
        {
            final JsonObject topic = root.get("topic").getAsJsonObject();
            this.topic = Utilities.getAsString(topic.get("value"));
            this.topicUpdateDate = Utilities.getAsTimestamp(topic.get("last_set"));
            this.topicUpdateUserId = new ObjectID(topic.get("creator").getAsString());
        }
        
        if (root.has("purpose"))
        {
            final JsonObject purpose = root.get("purpose").getAsJsonObject();
            this.purpose = Utilities.getAsString(purpose.get("value"));
            this.purposeUpdateDate = Utilities.getAsTimestamp(purpose.get("last_set"));
            this.purposeUpdateUserId = new ObjectID(purpose.get("creator").getAsString());
        }
        
        if (root.has("is_member")) {
            this.isClientMember = Utilities.getAsBoolean(root.get("is_member"), false);
        } else {
            this.isClientMember = root.has("last_read");
        }
        
        if (this.isClientMember && root.has("last_read"))
        {
            this.lastRead = Utilities.getAsTimestamp(root.get("last_read"));
            this.latest = context.deserialize(root.get("latest"), Message.class);
            this.unreadCount = Utilities.getAsInt(root.get("unread_count"));
            this.unreadCountDisplay = Utilities.getAsInt(root.get("unread_count_display"));
        }
    }
}
