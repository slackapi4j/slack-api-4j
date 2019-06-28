package io.github.slackapi4j.internal;

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
import io.github.slackapi4j.exceptions.SlackMesssageInvalidException;
import com.google.gson.JsonObject;

import java.util.Arrays;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 26/06/2019.
 */
public class MessageValidator {
    /**
     * This will validate a object has the correct paramaters for a particular endpoint
     *
     * @param object   the object being checked
     * @param constant the endpoint your checking for.
     * @throws SlackException if not valid
     */
    public static void validateMessage(final JsonObject object, final SlackConstants constant) throws SlackMesssageInvalidException {
        try {
            switch (constant) {
                case HOST:
                case API_TEST:
                case AUTH_TEST:
                case RTM_START:
                case USER_LIST:
                    throw new SlackMesssageInvalidException("ENDPOINT INVALID", " is not a valid message method");
                case CHAT_POSTEMPHEMERAL:
                    test(object, "channel");
                    test(object, "user");
                    testEitherOr(object, "text", "attachment");
                default:
                    //valid
            }
        } catch (final SlackMesssageInvalidException e) {
            throw new SlackMesssageInvalidException(e.getCode(), constant + " : " + e.getMessage());
        }
    }

    private static void test(final JsonObject object, final String member) throws SlackMesssageInvalidException {
        if (object.has(member)) {
            return;
        }
        throw new SlackMesssageInvalidException("INVALID OBJECT", "EndPoint: requires the " + member);
    }

    /**
     * This will validate if the object has ANY of the members should be used when the message must have 1 of the params
     *
     * @param object the object to validate
     * @param member the list of string to check
     * @throws SlackMesssageInvalidException if invalid
     */
    private static void testEitherOr(final JsonObject object, final String... member) throws SlackMesssageInvalidException {
        for (final String m : member) {
            if (object.has(m)) {
                return;
            }
        }
        throw new SlackMesssageInvalidException("INVALID OBJECT", "EndPoint: requires on of " + Arrays.toString(member));
    }
}
