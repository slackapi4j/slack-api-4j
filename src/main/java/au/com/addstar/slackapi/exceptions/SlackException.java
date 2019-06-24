package au.com.addstar.slackapi.exceptions;

public class SlackException extends Exception
{
	private static final long serialVersionUID = -6107026452270704333L;
	
	private String code;
	private String codeMessage;

	public SlackException(String code)
	{
		super("Slack returned an error code: " + code);
		this.code = code;
	}
	
	protected SlackException(String code, String message)
	{
		super(message);
		this.code = code;
	}
	
	public final String getCode()
	{
		return code;
	}
}
