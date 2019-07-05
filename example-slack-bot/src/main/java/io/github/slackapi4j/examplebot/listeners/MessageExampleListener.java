package io.github.slackapi4j.examplebot.listeners;

/*-
 * #%L
 * example-slack-bot
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

import io.github.slackapi4j.RealTimeSession;
import io.github.slackapi4j.eventlisteners.MessageListener;
import io.github.slackapi4j.events.MessageEvent;
import io.github.slackapi4j.exceptions.SlackException;
import io.github.slackapi4j.exceptions.SlackRtException;
import io.github.slackapi4j.objects.Message;
import io.github.slackapi4j.objects.blocks.Section;
import io.github.slackapi4j.objects.blocks.composition.TextObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created for the Charlton IT Project.
 * Created by Narimm on 3/07/2019.
 */
public class MessageExampleListener extends MessageListener {

  private final RealTimeSession session;
  private final Logger log = Logger.getAnonymousLogger();

  /**
   * Listens and generates a log of messages.
   *
   * @param session the session we are listening in
   */
  public MessageExampleListener(final RealTimeSession session) {
    super();
    this.session = session;
    session.addListener(this);
  }

  @Override
  public void onLoginComplete() {

  }

  @Override
  public void onError(final SlackRtException cause) {

  }

  @Override
  public void onClose() {

  }

  @Override
  public void onMessage(final MessageEvent event) {
    log.info(event.getMessage().getText());
    System.out.println(event);
    if (event.getMessage().getText().contains("!PING")) { //this can mach for emoji so watch out.
      final TextObject text = new TextObject();
      text.setText("#PONG!!!# :baseball: ");
      text.setType(TextObject.TextType.MARKDOWN);
      text.setEmoji(true);
      final Section section = new Section();
      section.setText(text);
      final Message message = Message.builder()
          .text("")
          .conversationID(event.getMessage().getConversationID())
          .userId(event.getMessage().getUserId()) //if you want to send ephemerally you need this
          .blocks(new ArrayList<>())
          .asUser(false)
          .build();
      message.addBlock(section);
      try {
        System.out.println("Sent:" + message.getTimestamp());
        final Message sent = session.getApi().sendEphemeral(message);
        System.out.println("Sent:" + sent.getTimestamp());
      } catch (final IOException | SlackException e) {
        Logger.getAnonymousLogger().warning(e.getMessage());
      }

    }
  }
}
