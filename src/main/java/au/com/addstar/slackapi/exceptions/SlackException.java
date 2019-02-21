package au.com.addstar.slackapi.exceptions;

public class SlackException extends Exception
{
    private static final long serialVersionUID = -6107026452270704333L;
    
    private final String code;

    public SlackException(final String code)
    {
        super("Slack returned an error code: " + code);
        this.code = code;
    }
    
    protected SlackException(final String code, final String message)
    {
        super(message);
        this.code = code;
    }
    
    public final String getCode()
    {
        return this.code;
    }
}
