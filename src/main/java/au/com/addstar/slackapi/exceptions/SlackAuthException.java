package au.com.addstar.slackapi.exceptions;

public class SlackAuthException extends SlackException
{
    private static final long serialVersionUID = -5144087555160180738L;

    public SlackAuthException(final String code)
    {
        super(code);
    }
}
