package au.com.addstar.slackapi;


import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.internal.SlackConversationType;
import au.com.addstar.slackapi.objects.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/02/2019.
 */
public class ConversationsManagerTest {
    private static final String token = System.getenv("SLACK_TOKEN");

    @Test
    public void sendMessage() {
        if(token == null | token.isEmpty())return;
        SlackAPI api = new SlackAPI(token);
        try {
            List<SlackConversationType> types = new ArrayList<>();
            types.add(SlackConversationType.PRIVATE);
            types.add(SlackConversationType.PUBLIC);
            types.add(SlackConversationType.IM);
            types.add(SlackConversationType.MPIM);
            List<Conversation> conversations = api.getConversations().listConversations(types);
            for(Conversation c:conversations){
                if(c.getName().equals("test")){
                    Message message = api.sendMessage("Testing Conversation API",c);
                    assertTrue(message.getSourceId() != null);
                }
            }
        } catch (SlackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void createMPIM() throws IOException, SlackException {
        if(token == null | token.isEmpty())return;
        SlackAPI api = new SlackAPI(token);
        RealTimeSession session = api.startRTSession();
        Set<User> users= session.getUsers();
        List<User> out = new ArrayList<>();
        for(User user:users) {
            if(user.getName().equals("narimm")) {
                out.add(user);
            }
        }
        Conversation conversation = api.getConversations().createMultiPartyMessage(out);
        session.sendMessage("Test Message",conversation);
        api.getConversations().closeMultiPartyMessage(conversation);
        session.close();
    }
}
