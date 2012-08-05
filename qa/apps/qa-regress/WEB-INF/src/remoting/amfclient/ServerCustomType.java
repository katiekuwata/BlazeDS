package remoting.amfclient;

/**
 * The server side object used by AMFConnectionTestService. There is a 
 * corresponding client side object.
 */
public class ServerCustomType
{
    private int id;

    public ServerCustomType()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String toString()
    {
        return "ServerCustomType: " + id;
    }
}
