package au.com.addstar.slackapi.events;

import au.com.addstar.slackapi.objects.ObjectID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Event when a conversations details or members change or is created / removed etc
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
@RequiredArgsConstructor
@Getter
public class ConversationEvent extends RealTimeEvent {
    /**
     * This object may be incomplete as
     */
    private final @NonNull ObjectID conversationID;
    private final @NonNull EventType type;

    @SuppressWarnings("unused")
    public enum EventType {
        Join,
        Leave,
        Create,
        Delete,
        Rename,
        Archive,
        Unarchive,
        HistoryChange,
        Open,
        Close
    }

}
