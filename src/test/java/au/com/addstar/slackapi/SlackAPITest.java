package au.com.addstar.slackapi;

import au.com.addstar.slackapi.exceptions.SlackRequestLimitException;
import au.com.addstar.slackapi.internal.SlackConnection;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/02/2019.
 */
public class SlackAPITest {
    private static final String token = System.getenv("SLACK_TOKEN");

    @Test
    public void getSlack() {
        if(token.isEmpty())return;
        SlackAPI api = new SlackAPI(token);
        SlackConnection connection = api.getSlack();
        Map<String,Object> map = new HashMap<>();
        map.put("foo","bar");
        try {
            JsonElement result = connection.callMethod("api.test", map);
            JsonObject i = result.getAsJsonObject();
            String val = i.get("args").getAsJsonObject().get("foo").getAsString();
            assertEquals("bar",val);
        } catch (SlackRequestLimitException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
