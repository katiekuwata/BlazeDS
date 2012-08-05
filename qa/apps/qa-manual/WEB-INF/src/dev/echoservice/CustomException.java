package dev.echoservice;

public class CustomException extends Exception
{
    private String _reason;
    static final long serialVersionUID = 7867324767130586850L;

    public String getReason()
    {
        return _reason;
    }

    public void setReason(String r)
    {
        _reason = r;
    }

    public int code;

    public String toString()
    {
        return "CustomException: " + _reason;
    }
}
