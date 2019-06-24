package au.com.addstar.slackapi.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import au.com.addstar.slackapi.objects.NormalChannel;

@RequiredArgsConstructor
@Getter
public class ChannelEvent extends RealTimeEvent
{
    private final NormalChannel channel;
    private final EventType type;
    
    @SuppressWarnings("unused")
    public enum EventType
    {
        Join,
        Leave,
        Create,
        Delete,
        Rename,
        Archive,
        Unarchive,
        HistoryChange
    }
}
