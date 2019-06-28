package au.com.addstar.slackapi.events;

import au.com.addstar.slackapi.objects.ObjectID;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Events where a conversation modification was caused by a specific user.
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
@Getter
@ToString(callSuper = true)
public class UserConversationEvent extends ConversationEvent {

    private final @NonNull ObjectID userID;

    public UserConversationEvent(final @NonNull ObjectID conversationID, final @NonNull ObjectID userID, final EventType type) {
        super(conversationID, type);
        this.userID = userID;
    }
}
