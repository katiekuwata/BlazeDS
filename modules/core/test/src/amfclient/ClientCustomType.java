package amfclient;

/**
 * The client side object used by the AMFConnectionTest. There is a corresponding
 * server side object.
 */
public class ClientCustomType
{
    private int id;

    public ClientCustomType()
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
        return "ClientCustomType: " + id;
    }
}
