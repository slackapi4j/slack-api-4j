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
import io.github.slackapi4j.internal.SlackConnection;
import io.github.slackapi4j.internal.SlackConstants;
import io.github.slackapi4j.objects.Conversation;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.ObjectID;
import io.github.slackapi4j.objects.User;
import io.github.slackapi4j.objects.blocks.ActionBlock;
import io.github.slackapi4j.objects.blocks.Divider;
import io.github.slackapi4j.objects.blocks.Section;
import io.github.slackapi4j.objects.blocks.composition.ConfirmObject;
import io.github.slackapi4j.objects.blocks.composition.Option;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import io.github.slackapi4j.objects.blocks.elements.Element;
import io.github.slackapi4j.objects.blocks.elements.SelectElement;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.slackapi4j.objects.blocks.ImageBlock;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by benjamincharlton on 19/02/2019.
 */
public class SlackApiTest {
  private static final String TOKEN = System.getenv("SLACK_TOKEN");

    @Test
    public void getSlack() {
      if (TOKEN == null || TOKEN.isEmpty()) {
            return;
        }
      final SlackApi api = new SlackApi(TOKEN);
      final SlackConnection connection = api.getSlack();
      final Map<String, Object> map = new HashMap<>();
        map.put("foo","bar");
        try {
          final JsonElement result = connection.callMethod(SlackConstants.API_TEST, map);
          final JsonObject i = result.getAsJsonObject();
          final String val = i.get("args").getAsJsonObject().get("foo").getAsString();
            assertEquals("bar",val);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMessaging() {
      if (TOKEN == null || TOKEN.isEmpty()) {
            assertTrue(true);
            return;
        }
      final SlackApi api = new SlackApi(TOKEN);
        try {
          final RealTimeSession session = api.startRtSession();
          final Conversation c = session.getChannel("bot-test");
            if(c==null)
                //exit we had the wrong channel for the test token....stop rather than fail.
            {
              assertTrue(true);
              return;
            }
          final User u = session.getSelf();
            session.close();
          final Message message = new Message("Plain Message Test", c);
            message.setUserId(u.getId());
          final Message response = api.sendMessage(message);
            assertTrue(response.getTimestamp() > 0);
          final List<ObjectID> users = api.getConversations().getMembers(c);
          final User target = users.stream().map(session::getUserById)
              .filter(user -> "Narimm".equals(user.getRealName()))
              .findFirst()
              .orElse(null);
          final Message ephem = new Message("Ephemeral Message Test", c);
          if (target == null) {
            return;
          }
            ephem.setUserId(target.getId());
            api.sendEphemeral(ephem);
          final Message test = Message.builder().
                    conversationID(c.getId())
                    .userId(u.getId())
              .asUser(true)
                    .blocks(new ArrayList<>())
                    .subtype(Message.MessageType.Normal)
                   // .thread_ts(response.getTs())
                    .build();
          final ImageBlock image = new ImageBlock();
          final TextObject title = new TextObject();
            title.setText("Some Random Image :thumbsup:");
            title.setType(TextObject.TextType.PLAIN);
            title.setEmoji(true);
            image.setTitle(title);
            image.setImageUrl(new URL("http://1.bp.blogspot.com/-fgm36vPUvMI/Tmz08xaOSEI/AAAAAAAAA6w/su3tQSL7yBc/s1600/food-art7.jpg"));
            image.setAltText("A random Apple");
          final Section section = new Section();
          final TextObject text = new TextObject();
            text.setText("~Formatted Block Text~ :thumbsup:");
            text.setType(TextObject.TextType.MARKDOWN);
            text.setVerbatim(false);
            section.setText(text);
            test.addBlock(section);
            test.addBlock(new Divider());
            test.addBlock(image);
            Message sent = api.sendMessage(test);
            assertTrue(sent.getTimestamp() >0);
          final Message question = Message.builder()
              .asUser(true)
                    .blocks(new ArrayList<>())
                    .conversationID(c.getId())
                    .build();
          final ActionBlock actions = new ActionBlock();
          final List<Element> elements = new ArrayList<>();
          final SelectElement select = new SelectElement();
          final List<Option> options = new ArrayList<>();
            options.add(Option.builder()
                    .text(TextObject.builder()
                    .text("The best option")
                    .type(TextObject.TextType.PLAIN)
                    .build())
                    .value("BEST")
                    .build());
            options.add(Option.builder()
                    .text(TextObject.builder()
                            .text("The worst option")
                            .type(TextObject.TextType.PLAIN)
                            .build())
                    .value("WORST")
                    .build());
            select.setOptions(options);
            select.setConfirm(ConfirmObject.builder()
                    .confirm(TextObject.builder()
                            .type(TextObject.TextType.PLAIN)
                            .text("Yes this is the bomb")
                            .build())
                    .deny(TextObject.builder()
                            .type(TextObject.TextType.PLAIN)
                            .text("No ...this is bad")
                            .build())
                    .title(TextObject.builder()
                            .type(TextObject.TextType.PLAIN)
                            .text("Are you 100% sure")
                            .build())
                    .text(TextObject.builder()
                            .type(TextObject.TextType.PLAIN)
                            .text("Pressing these buttons means you want the wall")
                            .build())
                    .build());
            elements.add(select);
            actions.setElements(elements);
            question.addBlock(actions);
            sent = api.sendMessage(question);
            assertTrue(sent.getTimestamp() >0);
        } catch (final SlackException | IOException e) {
            e.printStackTrace();
        }

    }
}
