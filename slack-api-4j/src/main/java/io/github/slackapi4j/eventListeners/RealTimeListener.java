package io.github.slackapi4j.eventListeners;

import io.github.slackapi4j.events.RealTimeEvent;
import io.github.slackapi4j.exceptions.SlackRTException;

@SuppressWarnings("WeakerAccess")
public interface RealTimeListener
{
    void onLoginComplete();
    void onEvent(RealTimeEvent event);
    void onError(SlackRTException cause);
    void onClose();
}
