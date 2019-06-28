package au.com.addstar.slackapi.eventListeners;

import au.com.addstar.slackapi.events.ConversationEvent;
import au.com.addstar.slackapi.events.RealTimeEvent;

/**
 * Extending this class means your listener handles on Conversation Events
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
public abstract class ConversationEventListener implements RealTimeListener {

    abstract void onEvent(ConversationEvent event);

    @Override
    public final void onEvent(final RealTimeEvent event) {
        if (event instanceof ConversationEvent) {
            this.onEvent((ConversationEvent) event);
        }
    }
}
