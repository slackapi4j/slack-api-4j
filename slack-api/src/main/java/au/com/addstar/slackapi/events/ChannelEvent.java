package au.com.addstar.slackapi.events;

import au.com.addstar.slackapi.objects.Conversation;
import au.com.addstar.slackapi.objects.ObjectID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import au.com.addstar.slackapi.objects.NormalChannel;

/**
 * @deprecated use @code{ConversationEvent.class}
 */
@Deprecated
public class ChannelEvent extends ConversationEvent {
    public ChannelEvent(ObjectID conversationID, EventType type) {
        super(conversationID, type);
    }
}
