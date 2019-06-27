package au.com.addstar.slackapi.internal;

import au.com.addstar.slackapi.exceptions.SlackException;
import au.com.addstar.slackapi.exceptions.SlackMesssageInvalidException;
import au.com.addstar.slackapi.objects.Message;
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
            throw new SlackMesssageInvalidException(e.getCode(), constant.toString() + " : " + e.getMessage());
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
