package io.github.slackapi4j.exceptions;

public class SlackRTException extends Exception
{
    private static final long serialVersionUID = 5189878895672200892L;

    private final int errorCode;
    
    public SlackRTException(final int code, final String message)
    {
        super(message);
        this.errorCode = code;
    }
    
    public int getErrorCode()
    {
        return this.errorCode;
    }
    
    @Override
    public String toString()
    {
        final String s = this.getClass().getName();
        final String message = this.getLocalizedMessage();
        return s + ": " + this.errorCode + (message != null ? " - " + message : s);
    }
}
