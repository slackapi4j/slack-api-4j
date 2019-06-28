package io.github.slackapi4j;


import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConversationType;
import au.com.addstar.slackapi.objects.*;
import io.github.slackapi4j.objects.Conversation;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/02/2019.
 */
public class ConversationsManagerTest {
    private static final String TOKEN = System.getenv("SLACK_TOKEN");

    @Test
    @SuppressWarnings("deprecation")
    public void sendMessage() {
        if (TOKEN == null | TOKEN.isEmpty()) {
            return;
        }
        SlackAPI api = new SlackAPI(TOKEN);
        try {
            List<SlackConversationType> types = new ArrayList<>();
            types.add(SlackConversationType.PRIVATE);
            types.add(SlackConversationType.PUBLIC);
            types.add(SlackConversationType.IM);
            types.add(SlackConversationType.MPIM);
            List<Conversation> conversations = api.getConversations().listConversations(types);
            for(Conversation c:conversations){
                if ("test".equals(c.getName())) {
                    Message message = api.sendMessage("Testing Conversation API",c);
                    assertTrue(message.getConversationID() != null);
                }
            }
        } catch (SlackException | IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void createDM() throws IOException, SlackException {
        if (TOKEN == null || TOKEN.isEmpty()) {
            return;
        }

        SlackAPI api = new SlackAPI(TOKEN);
        SlackAPI.setDebug(true);
        RealTimeSession session = api.startRTSession();
        if (session.isOpen()) {
            Set<User> users = session.getUsers();
            List<User> out = new ArrayList<>();
            for (User user : users) {
                if ("Narimm".equals(user.getRealName())) {
                    out.add(user);
                }
            }
            ConversationsManager manager = api.getConversations();
            Conversation conversation = manager.createDMConversation(out);
            Message message = Message.builder()
                    .conversationID(conversation.getId())
                    .userId(session.getSelf().getId())
                    .as_user(false)
                    .subtype(Message.MessageType.Normal)
                    .text("Test API Message")
                    .build();
            api.sendMessage(message);
            session.sendMessage("Test SESSION Message", conversation);
            api.getConversations().closeMultiPartyMessage(conversation);
        }
        session.close();
    }
}
