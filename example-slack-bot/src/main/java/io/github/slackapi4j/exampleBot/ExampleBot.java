package io.github.slackapi4j.exampleBot;

import io.github.slackapi4j.exampleBot.listeners.ExampleListener;
import io.github.slackapi4j.RealTimeSession;
import io.github.slackapi4j.SlackAPI;
import io.github.slackapi4j.exceptions.SlackException;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
public class ExampleBot {
    private static final String TOKEN = System.getenv("SLACK_TOKEN");
    private static RealTimeSession session;

    public static void main(String[] args) {
        if (TOKEN == null || TOKEN.isEmpty()) {
            return;
        }
        SlackAPI api = new SlackAPI(TOKEN);
        SlackAPI.setDebug(true);
        Executors.newSingleThreadExecutor().execute(
                () -> {
                    try {
                        session = api.startRTSession();
                        new ExampleListener(session);
                    } catch (SlackException | IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
