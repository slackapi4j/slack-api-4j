package io.github.slackapi4j.eventListeners;

import io.github.slackapi4j.events.MessageEvent;
import io.github.slackapi4j.events.RealTimeEvent;

/**
 * Only handles message events
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
public abstract class MessageListener implements RealTimeListener {


    abstract void onEvent(MessageEvent event);

    @Override
    public final void onEvent(final RealTimeEvent event) {
        if (event instanceof MessageEvent) {
            this.onEvent((MessageEvent) event);
        }
    }
}
