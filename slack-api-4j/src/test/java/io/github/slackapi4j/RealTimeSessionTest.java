package io.github.slackapi4j;

import com.google.gson.JsonObject;
import io.github.slackapi4j.eventListeners.ConversationEventListener;
import io.github.slackapi4j.eventListeners.RealTimeListener;
import io.github.slackapi4j.events.ConversationEvent;
import io.github.slackapi4j.events.RealTimeEvent;
import io.github.slackapi4j.exceptions.SlackRTException;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;


/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 29/06/2019.
 */
public class RealTimeSessionTest {
    private RealTimeSession session;
     void Setup() throws IOException {
        SlackAPI api =  new SlackAPI(SlackAPITest.TEST_TOKEN);
        session = new RealTimeSession(new JsonObject(),api);
         WebSocketClient client = session.getClient();

    }

    @Test
    public void addListener() {
        RealTimeListener listener = new ConversationEventListener() {
            public RealTimeEvent event;
            private boolean logged = false;
            @Override
            protected void onEvent(ConversationEvent event) {
                this.event = event;
            }

            @Override
            public void onLoginComplete() {
                logged = true;
            }

            @Override
            public void onError(SlackRTException cause) {

            }

            @Override
            public void onClose() {
                logged = false;
            }
        };
        session.addListener(listener);
    }
}
