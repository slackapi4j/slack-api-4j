package au.com.addstar.slackapi.exceptions;

import java.io.IOException;

public class SlackRequestLimitException extends IOException
{
    private static final long serialVersionUID = 3219366585654462054L;

    private long endTime;
    
    public SlackRequestLimitException(final long endTime)
    {
        super("Too many requests made in a short time");
    }
    
    public long getRetryTime()
    {
        return this.endTime;
    }
}
