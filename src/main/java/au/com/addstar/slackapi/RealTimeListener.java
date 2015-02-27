package au.com.addstar.slackapi;

import au.com.addstar.slackapi.events.RealTimeEvent;
import au.com.addstar.slackapi.exceptions.SlackRTException;

public interface RealTimeListener
{
	public void onLoginComplete();
	public void onEvent(RealTimeEvent event);
	public void onError(SlackRTException cause);
}
