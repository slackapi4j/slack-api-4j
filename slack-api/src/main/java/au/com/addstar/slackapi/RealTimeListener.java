package au.com.addstar.slackapi;

import au.com.addstar.slackapi.events.RealTimeEvent;
import au.com.addstar.slackapi.exceptions.SlackRTException;

@SuppressWarnings("WeakerAccess")
public interface RealTimeListener
{
    void onLoginComplete();
    void onEvent(RealTimeEvent event);
    void onError(SlackRTException cause);
    void onClose();
}
