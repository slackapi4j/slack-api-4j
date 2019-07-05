package io.github.slackapi4j;

/*-
 * #%L
 * slack-api-4j
 * %%
 * Copyright (C) 2018 - 2019 SlackApi4J
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.internal.SlackConversationType;

import io.github.slackapi4j.objects.Conversation;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by benjamincharlton on 19/02/2019.
 */
public class ConversationsManagerTest {
    private static final String TOKEN = System.getenv("SLACK_TOKEN");

    @Test
    @SuppressWarnings("deprecation")
    public void sendMessage() {
        if (TOKEN == null || TOKEN.isEmpty()) {
          assertTrue(true);
          return;
        }
      final SlackApi api = new SlackApi(TOKEN);
        try {
          final List<SlackConversationType> types = new ArrayList<>();
            types.add(SlackConversationType.PRIVATE);
            types.add(SlackConversationType.PUBLIC);
            types.add(SlackConversationType.IM);
            types.add(SlackConversationType.MPIM);
          final List<Conversation> conversations = api.getConversations().listConversations(types);
          for (final Conversation c : conversations) {
                if ("test".equals(c.getName())) {
                  final Message message = api.sendMessage("Testing Conversation API", c);
                  assertNotNull(message.getConversationID());
                }
            }
        } catch (final SlackException | IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void createDM() throws IOException, SlackException {
        if (TOKEN == null || TOKEN.isEmpty()) {
            return;
        }

      final SlackApi api = new SlackApi(TOKEN);
      SlackApi.setDebug(true);
      final RealTimeSession session = api.startRtSession();
        if (session.isOpen()) {
          final Set<User> users = session.getUsers();
          final List<User> out = new ArrayList<>();
          for (final User user : users) {
                if ("Narimm".equals(user.getRealName())) {
                    out.add(user);
                }
            }
          final ConversationsManager manager = api.getConversations();
          final Conversation conversation = manager.createDmConversation(out);
          final Message message = Message.builder()
                    .conversationID(conversation.getId())
                    .userId(session.getSelf().getId())
              .asUser(false)
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
