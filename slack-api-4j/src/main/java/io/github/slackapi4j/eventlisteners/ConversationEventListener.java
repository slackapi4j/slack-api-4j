package io.github.slackapi4j.eventlisteners;

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

import io.github.slackapi4j.events.ConversationEvent;
import io.github.slackapi4j.events.RealTimeEvent;

/**
 * Extending this class means your listener handles on Conversation Events
 * Created by Narimm on 27/06/2019.
 */
public abstract class ConversationEventListener implements RealTimeListener {
  /**
   * This event will be called with a ConversationEvent is received.
   * For event types {@link ConversationEvent.EventType}
   *
   * @param event the event to process
   */
  public abstract void onConversation(ConversationEvent event);

  @Override
  public final void onEvent(final RealTimeEvent event) {
    if (event instanceof ConversationEvent) {
      onConversation((ConversationEvent) event);
    }
  }
}
