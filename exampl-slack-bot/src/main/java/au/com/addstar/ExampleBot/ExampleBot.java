package au.com.addstar.ExampleBot;

import au.com.addstar.slackapi.RealTimeSession;
import au.com.addstar.slackapi.SlackAPI;
import au.com.addstar.slackapi.exceptions.SlackException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
public class ExampleBot {
    private static final String TOKEN = System.getenv("SLACK_TOKEN");
    private static RealTimeSession session;

    public static void main(String[] args) {
        if (TOKEN == null || TOKEN.isEmpty()) return;
        SlackAPI api = new SlackAPI(TOKEN);
        SlackAPI.setDebug(true);
        Executors.newSingleThreadExecutor().execute(
                () -> {
                    try {
                        session = api.startRTSession();
                    } catch (SlackException | IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
