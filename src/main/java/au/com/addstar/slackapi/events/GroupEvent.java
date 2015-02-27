package au.com.addstar.slackapi.events;

import au.com.addstar.slackapi.Group;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GroupEvent extends RealTimeEvent
{
	private final Group channel;
	private final EventType type;
	
	public enum EventType
	{
		Join,
		Leave,
		Open,
		Close,
		Rename,
		Archive,
		Unarchive,
		HistoryChange
	}
}
