package io.github.slackapi4j.exceptions;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 26/06/2019.
 */
public class SlackMesssageInvalidException extends SlackException {
    public SlackMesssageInvalidException(final String errorCode, final String message) {
        super(errorCode, message);
    }
}
