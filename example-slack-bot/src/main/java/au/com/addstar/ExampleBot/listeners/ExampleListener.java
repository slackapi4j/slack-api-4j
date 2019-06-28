package au.com.addstar.ExampleBot.listeners;

import au.com.addstar.slackapi.eventListeners.RealTimeListener;
import au.com.addstar.slackapi.RealTimeSession;
import au.com.addstar.slackapi.events.ConversationEvent;
import au.com.addstar.slackapi.events.MessageEvent;
import au.com.addstar.slackapi.events.RealTimeEvent;
import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.exceptions.SlackRTException;
import au.com.addstar.slackapi.objects.Message;
import au.com.addstar.slackapi.objects.blocks.Section;
import au.com.addstar.slackapi.objects.blocks.composition.TextObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
public class ExampleListener implements RealTimeListener {

    private RealTimeSession session;
    private Logger log = Logger.getAnonymousLogger();

    public ExampleListener(final RealTimeSession session) {
        this.session = session;
        session.addListener(this);
    }

    @Override
    public void onLoginComplete() {
        log.info("Logged into Slack as " + session.getSelf().getName());
        //further tasks once logged in here.
    }

    @Override
    public void onEvent(RealTimeEvent event) {
        if (event instanceof MessageEvent)
            onMessage((MessageEvent) event);
        if (event instanceof ConversationEvent)
            onConversationEvent((ConversationEvent) event);
    }

    private void onMessage(MessageEvent event) {
        log.info(event.getMessage().getText());
        System.out.println(event);
        if (event.getMessage().getText().contains("!PING")) { //this can mach for emoji so watch out.
            Message message = Message.builder()
                    .text("")
                    .conversationID(event.getMessage().getConversationID())
                    .userId(event.getMessage().getUserId()) //if you want to send ephemerally you need this
                    .blocks(new ArrayList<>())
                    .as_user(false)
                    .build();
            TextObject text = new TextObject();
            text.setText("#PONG!!!# :baseball: ");
            text.setType(TextObject.TextType.MARKDOWN);
            text.setEmoji(true);
            Section section = new Section();
            section.setText(text);
            message.addBlock(section);
            try {
                System.out.println("Sent:" + message.getTimestamp());
                Message sent = session.getApi().sendEphemeral(message);
                System.out.println("Sent:" + sent.getTimestamp());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SlackException e) {
                e.printStackTrace();
            }

        }
    }

    private void onConversationEvent(ConversationEvent event) {
        log.info(event.getType().name() + ":" + event.getConversationID());
        System.out.println(event);
        if (event.getType().equals(ConversationEvent.EventType.Join)) {
            Message message = Message.builder()
                    .text("If you !PING I will pong!")
                    .conversationID(event.getConversationID())
                    .as_user(false)
                    .build();
            this.session.sendMessage(message);
        }
    }

    private void onMessage(ConversationEvent event) {
        log.info("Event from " + event.getConversationID());
        System.out.println(event);
    }

    @Override
    public void onError(SlackRTException cause) {
        log.info(cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void onClose() {

    }
}
