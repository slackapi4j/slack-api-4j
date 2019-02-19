package au.com.addstar.slackapi;


import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConversationType;
import au.com.addstar.slackapi.objects.BaseObject;
import au.com.addstar.slackapi.objects.Conversation;
import com.google.gson.JsonObject;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/02/2019.
 */
public class ConversationsManagerTest {


    @Test
    public void joinConversation() {
        SlackAPI api = new SlackAPI("xoxb-3854431244-OorTjHoCcuO6a3bHcZgqrrcT");
        try {
            List<SlackConversationType> types = new ArrayList<>();
            types.add(SlackConversationType.PRIVATE);
            types.add(SlackConversationType.PUBLIC);
            types.add(SlackConversationType.IM);
            types.add(SlackConversationType.MPIM);
            List<Conversation> conversations = api.getConversations().listConversations(types);
            for(Conversation c:conversations){
                if(c.getName().equals("bot-test")){
                    assertTrue(api.getConversations().joinConversation(c));
                    api.sendMessage("Testing Conversation API",c);
                }
            }
        } catch (SlackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
