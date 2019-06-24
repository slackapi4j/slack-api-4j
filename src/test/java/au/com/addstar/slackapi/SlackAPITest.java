package au.com.addstar.slackapi;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.exceptions.SlackRequestLimitException;
import au.com.addstar.slackapi.internal.SlackConnection;
import au.com.addstar.slackapi.objects.Conversation;
import au.com.addstar.slackapi.objects.Message;
import au.com.addstar.slackapi.objects.User;
import au.com.addstar.slackapi.objects.blocks.*;
import au.com.addstar.slackapi.objects.blocks.composition.ConfirmObject;
import au.com.addstar.slackapi.objects.blocks.composition.Option;
import au.com.addstar.slackapi.objects.blocks.composition.TextObject;
import au.com.addstar.slackapi.objects.blocks.elements.Element;
import au.com.addstar.slackapi.objects.blocks.elements.SelectElement;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/02/2019.
 */
public class SlackAPITest {
    private static final String token = System.getenv("SLACK_TOKEN");

    @Test
    public void getSlack() {
        if(token == null | token.isEmpty())return;
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

    @Test void testMessaging() {
        if(token == null | token.isEmpty())return;
        SlackAPI api = new SlackAPI(token);
        try {
            RealTimeSession session = api.startRTSession();
            Conversation c = session.getChannel("bot-test");
            if(c==null)
                //exit we had the wrong channel for the test token....stop rather than fail.
                return;
            User u = session.getSelf();
            session.close();
            Message message = new Message("Plain Message Test",c);
            message.setUserId(u.getId());
           // Message response = api.sendMessage(message);
            Message test = Message.builder().
                    sourceId(c.getId())
                    .userId(u.getId())
                    .as_user(true)
                    .blocks(new ArrayList<>())
                    .subtype(Message.MessageType.Normal)
                   // .thread_ts(response.getTs())
                    .build();
            ImageBlock image = new ImageBlock();
            TextObject title = new TextObject();
            title.setText("Some Random Image :thumbsup:");
            title.setType(TextObject.TextType.PLAIN);
            title.setEmoji(true);
            image.setTitle(title);
            image.setImageUrl(new URL("http://1.bp.blogspot.com/-fgm36vPUvMI/Tmz08xaOSEI/AAAAAAAAA6w/su3tQSL7yBc/s1600/food-art7.jpg"));
            image.setAltText("A random Apple");
            Section section = new Section();
            TextObject text = new TextObject();
            text.setText("~Formatted Block Text~ :thumbsup:");
            text.setType(TextObject.TextType.MARKDOWN);
            text.setVerbatim(false);
            section.setText(text);
            test.addBlock(section);
            test.addBlock(new Divider());
            test.addBlock(image);
            Message sent = api.sendMessage(test);
            assertTrue(sent.getTimestamp() >0);
            Message question = Message.builder()
                    .as_user(true)
                    .blocks(new ArrayList<>())
                    .sourceId(c.getId())
                    .build();
            ActionBlock actions = new ActionBlock();
            List<Element> elements = new ArrayList<>();
            SelectElement select  = new SelectElement();
            List<Option> options = new ArrayList<>();
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
        } catch (SlackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
