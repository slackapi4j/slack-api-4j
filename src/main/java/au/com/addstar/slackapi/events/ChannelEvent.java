package au.com.addstar.slackapi.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import au.com.addstar.slackapi.Channel;

@RequiredArgsConstructor
@Getter
public class ChannelEvent extends RealTimeEvent
{
	private final Channel channel;
	private final EventType type;
	
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
