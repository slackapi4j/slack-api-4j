package au.com.addstar.slackapi.eventListeners;

import au.com.addstar.slackapi.events.MessageEvent;
import au.com.addstar.slackapi.events.RealTimeEvent;

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
