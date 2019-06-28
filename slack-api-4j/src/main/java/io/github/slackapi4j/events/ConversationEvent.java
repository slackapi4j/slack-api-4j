package io.github.slackapi4j.events;

import io.github.slackapi4j.objects.ObjectID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Event when a conversations details or members change or is created / removed etc
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
@RequiredArgsConstructor
@Getter
@ToString
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
