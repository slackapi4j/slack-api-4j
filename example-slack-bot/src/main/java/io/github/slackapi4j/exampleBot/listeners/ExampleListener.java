package io.github.slackapi4j.exampleBot.listeners;

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

import io.github.slackapi4j.eventListeners.RealTimeListener;
import io.github.slackapi4j.RealTimeSession;
import io.github.slackapi4j.events.RealTimeEvent;
import io.github.slackapi4j.exceptions.SlackRTException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 27/06/2019.
 */
public class ExampleListener implements RealTimeListener {

    private final RealTimeSession session;
    private final Logger log = Logger.getAnonymousLogger();

    public ExampleListener(final RealTimeSession session) {
        this.session = session;
        session.addListener(this);
    }

    @Override
    public void onLoginComplete() {
        this.log.info("Logged into Slack as " + this.session.getSelf().getName());
        //further tasks once logged in here.
    }

    @Override
    public void onEvent(RealTimeEvent event) {
        this.log.info(event.toString());
    }

    @Override
    public void onError(SlackRTException cause) {
        this.log.info(cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void onClose() {

    }
}
