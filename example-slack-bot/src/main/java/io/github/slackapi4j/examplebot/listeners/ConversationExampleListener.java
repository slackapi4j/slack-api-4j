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
import io.github.slackapi4j.eventlisteners.ConversationEventListener;
import io.github.slackapi4j.events.ConversationEvent;
import io.github.slackapi4j.exceptions.SlackRtException;
import io.github.slackapi4j.objects.Message;

import java.util.logging.Logger;

/**
 * Created by Narimm on 3/07/2019.
 */
public class ConversationExampleListener extends ConversationEventListener {
  private final RealTimeSession session;
  private final Logger log = Logger.getAnonymousLogger();

  /**
   * An Example of a ConversationEvent listener.
   *
   * @param session the session to listen in
   */
  public ConversationExampleListener(final RealTimeSession session) {
    super();
    this.session = session;
    session.addListener(this);
  }

  @Override
  public void onLoginComplete() {

  }

  @Override
  public void onConversation(final ConversationEvent event) {
    log.info(event.getType().name() + ':' + event.getConversationID());
    log.info(event.toString());
    if (event.getType() == ConversationEvent.EventType.Join) {
      final Message message = Message.builder()
          .text("If you !PING I will pong!")
          .conversationID(event.getConversationID())
          .asUser(false)
          .build();
      session.sendMessage(message);
    }
  }

  @Override
  public void onError(final SlackRtException cause) {

  }

  @Override
  public void onClose() {

  }
}
