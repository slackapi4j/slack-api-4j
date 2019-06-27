package au.com.addstar.slackapi.exceptions;

public class SlackRestrictedException extends SlackException
{
    private static final long serialVersionUID = -1211496255230143081L;

    public SlackRestrictedException(final String code)
    {
        super(code);
    }
}
