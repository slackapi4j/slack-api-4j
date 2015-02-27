package au.com.addstar.slackapi.exceptions;

public class SlackRTException extends Exception
{
	private static final long serialVersionUID = 5189878895672200892L;

	private int errorCode;
	
	public SlackRTException(int code, String message)
	{
		super(message);
		errorCode = code;
	}
	
	public int getErrorCode()
	{
		return errorCode;
	}
	
	@Override
	public String toString()
	{
		String s = getClass().getName();
        String message = getLocalizedMessage();
        return s + ": " + errorCode + (message != null ? " - " + message : s);
	}
}
