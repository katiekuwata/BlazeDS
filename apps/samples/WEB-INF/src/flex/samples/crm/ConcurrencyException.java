package flex.samples.crm;

public class ConcurrencyException extends Exception
{
	private static final long serialVersionUID = -6405818907028247079L;

	public ConcurrencyException(String message)
	{
		super(message);
	}

	public ConcurrencyException(Throwable cause)
	{
		super(cause);
	}

	public ConcurrencyException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
