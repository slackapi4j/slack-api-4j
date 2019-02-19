package au.com.addstar.slackapi;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.exceptions.SlackRequestLimitException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.objects.User;
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

    @Test
    public void startRTSession() {
    }
    @Test
    public void sendMessage() {
        SlackAPI api = new SlackAPI("xoxb-3854431244-OorTjHoCcuO6a3bHcZgqrrcT");
        SlackConnection connection = api.getSlack();
        try {
            RealTimeSession session = api.startRTSession();
            for(User user: session.getUsers()){
            }
        } catch (SlackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getSlack() {
        SlackAPI api = new SlackAPI("xoxb-3854431244-OorTjHoCcuO6a3bHcZgqrrcT");
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
