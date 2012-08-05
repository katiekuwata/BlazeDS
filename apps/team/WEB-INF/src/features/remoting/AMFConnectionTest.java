package features.remoting;

import flex.messaging.io.amf.client.AMFConnection;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;

/**
 * An AMFConnection sample that talks to a remoting destination.
 */
public class AMFConnectionTest
{
    private static final String DEFAULT_URL = "http://localhost:8400/team/messagebroker/amf";
    private static final String DEFAULT_DESTINATION_ID = "remoting_AMF";

    /**
     * Given a remote method name, returns the AMF connection call needed using
     * the default destination id.
     */
    private static String getOperationCall(String method)
    {
        return DEFAULT_DESTINATION_ID + "." + method;
    }

    // Not a test, just an example to show how to use AMFConnection.
    public static void main(String[] args2)
    {
        // Create the AMF connection.
        AMFConnection amfConnection = new AMFConnection();

        // Connect to the remote url.
        try
        {
            amfConnection.connect(DEFAULT_URL);
        }
        catch (ClientStatusException cse)
        {
            cse.printStackTrace();
        }

        // Make a remoting call and retrieve the result.
        try
        {
            Object result = amfConnection.call(getOperationCall("echo"), "Foo");
            System.out.println("Result: " + result);
        }
        catch (ClientStatusException cse)
        {
            cse.printStackTrace();
        }
        catch (ServerStatusException sse)
        {
            sse.printStackTrace();
        }
        finally
        {
            System.out.println("Done");
            amfConnection.close();
        }
    }
}
