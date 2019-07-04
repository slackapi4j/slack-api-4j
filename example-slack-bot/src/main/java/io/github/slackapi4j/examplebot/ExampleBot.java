package io.github.slackapi4j.examplebot;

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
import io.github.slackapi4j.SlackApi;
import io.github.slackapi4j.examplebot.listeners.ExampleListener;
import io.github.slackapi4j.examplebot.listeners.MessageExampleListener;
import io.github.slackapi4j.exceptions.SlackException;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created by Narimm on 27/06/2019.
 */
public class ExampleBot {
  private static final String TOKEN = System.getenv("SLACK_TOKEN");
  private static RealTimeSession s_SESSION;

  /**
   * Our main entry point - no args.
   *
   * @param args a list of option args
   */
  public static void main(final String[] args) {
    if (TOKEN == null || TOKEN.isEmpty()) {
      return;
    }
    final SlackApi api = new SlackApi(TOKEN);
    SlackApi.setDebug(true);
    Executors.newSingleThreadExecutor().execute(
        () -> {
          try {
            s_SESSION = api.startRtSession();
            new ExampleListener(s_SESSION);
            new MessageExampleListener(s_SESSION);
          } catch (final SlackException | IOException e) {
            e.printStackTrace();
          }
        }
    );
  }
}
