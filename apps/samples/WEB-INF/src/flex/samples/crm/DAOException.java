package flex.samples.crm;

public class DAOException extends RuntimeException
{
	private static final long serialVersionUID = -8852593974738250673L;

	public DAOException(String message)
	{
		super(message);
	}

	public DAOException(Throwable cause)
	{
		super(cause);
	}

	public DAOException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
