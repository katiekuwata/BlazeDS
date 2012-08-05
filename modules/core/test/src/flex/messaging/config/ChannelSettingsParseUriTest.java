/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

package flex.messaging.config;

import flex.messaging.endpoints.AMFEndpoint;
import flex.messaging.endpoints.Endpoint;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ChannelSettingsParseUriTest extends TestCase
{

    private static String confirm = "/messagebroker/amf";
    private static String data1 = "http://{server.name}:{server.port}/{context.root}/messagebroker/amf";
    private static String data2 = "http://{server.name}:{server.port}/{context-root}/messagebroker/amf";
    private static String data3 = "{context.root}/messagebroker/amf";
    private static String data4 = "/{context.root}/messagebroker/amf";

    // Client Channel Test Data
    private static String clientConfirm1 = "http://{server.name}:{server.port}/messagebroker/amf";
    private static String clientData1a = "http://{server.name}:{server.port}/{context.root}/messagebroker/amf";
    private static String clientData1b = "http://{server.name}:{server.port}/{context-root}/messagebroker/amf";

    private static String clientConfirm2 = "http://{server.name}:{server.port}/ctx/messagebroker/amf";
    private static String clientData2a = "http://{server.name}:{server.port}/{context.root}/messagebroker/amf";
    private static String clientData2b = "http://{server.name}:{server.port}/{context-root}/messagebroker/amf";

    private static String clientConfirm3 = "/messagebroker/amf";
    private static String clientData3a = "{context.root}/messagebroker/amf";
    private static String clientData3b = "/{context.root}/messagebroker/amf";

    private static String clientConfirm4 = "/ctx/messagebroker/amf";
    private static String clientData4a = "{context.root}/messagebroker/amf";
    private static String clientData4b = "/{context.root}/messagebroker/amf";

    private static String clientConfirm5 = "/";
    private static String clientData5a = "{context.root}";
    private static String clientData5b = "/{context.root}";

    private static String clientConfirm6 = "/ctx";
    private static String clientData6a = "{context.root}";
    private static String clientData6b = "/{context.root}";

    
    public ChannelSettingsParseUriTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ChannelSettingsParseUriTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testReplaceContextRoot()
    {
        Endpoint fooEndpoint = new AMFEndpoint();
        fooEndpoint.setUrl(data1);     
        String actual = fooEndpoint.getParsedUrl("/ctx");
        Assert.assertEquals(confirm, actual);
    }

    public void testReplaceContextRootDash()
    {
        Endpoint fooEndpoint = new AMFEndpoint();
        fooEndpoint.setUrl(data2);        
        String actual = fooEndpoint.getParsedUrl("/ctx");
        Assert.assertEquals(confirm, actual);
    }

    public void testReplaceContextRootStart()
    {
        Endpoint fooEndpoint = new AMFEndpoint();
        fooEndpoint.setUrl(data3);        
        String actual = fooEndpoint.getParsedUrl("/ctx");
        Assert.assertEquals(confirm, actual);
    }

    public void testReplaceContextRootSlashStart()
    {
        Endpoint fooEndpoint = new AMFEndpoint();
        fooEndpoint.setUrl(data4);        
        String actual = fooEndpoint.getParsedUrl("/ctx");
        Assert.assertEquals(confirm, actual);
    }
    
    // Client Channel Tests
    
    public void testClientReplaceContextRoot()
    {
        ChannelSettings fooChannel = new ChannelSettings("channel-one");
        fooChannel.setUri(clientData1a);        
        String actual = fooChannel.getClientParsedUri("/");
        Assert.assertEquals(clientConfirm1, actual);

        fooChannel.setUri(clientData1a);        
        actual = fooChannel.getClientParsedUri("");
        Assert.assertEquals(clientConfirm1, actual);
        
        fooChannel.setUri(clientData2a);        
        actual = fooChannel.getClientParsedUri("/ctx");
        Assert.assertEquals(clientConfirm2, actual);
    }

    public void testClientReplaceContextRootDash()
    {
        ChannelSettings fooChannel = new ChannelSettings("channel-one");
        fooChannel.setUri(clientData1b);        
        String actual = fooChannel.getClientParsedUri("/");
        Assert.assertEquals(clientConfirm1, actual);

        fooChannel.setUri(clientData1b);        
        actual = fooChannel.getClientParsedUri("");
        Assert.assertEquals(clientConfirm1, actual);

        fooChannel.setUri(clientData2b);        
        actual = fooChannel.getClientParsedUri("/ctx");
        Assert.assertEquals(clientConfirm2, actual);
    }
    
    public void testClientReplaceContextRootStart()
    {
        ChannelSettings fooChannel = new ChannelSettings("channel-one");
        fooChannel.setUri(clientData3a);        
        String actual = fooChannel.getClientParsedUri("/");
        Assert.assertEquals(clientConfirm3, actual);

        fooChannel = new ChannelSettings("channel-one");
        fooChannel.setUri(clientData4a);        
        actual = fooChannel.getClientParsedUri("/ctx");
        Assert.assertEquals(clientConfirm4, actual);

    }
    
    public void testClientReplaceContextRootSlashStart()
    {
        ChannelSettings fooChannel = new ChannelSettings("channel-one");
        fooChannel.setUri(clientData3b);        
        String actual = fooChannel.getClientParsedUri("");
        Assert.assertEquals(clientConfirm3, actual);

        fooChannel.setUri(clientData4b);        
        actual = fooChannel.getClientParsedUri("/ctx");
        Assert.assertEquals(clientConfirm4, actual);
    }
    
    public void testClientReplaceContextRootOnly()
    {
        ChannelSettings fooChannel = new ChannelSettings("channel-one");
        fooChannel.setUri(clientData5a);        
        String actual = fooChannel.getClientParsedUri("/");
        Assert.assertEquals(clientConfirm5, actual);

        fooChannel.setUri(clientData6a);        
        actual = fooChannel.getClientParsedUri("/ctx");
        Assert.assertEquals(clientConfirm6, actual);
    }
    
    public void testClientReplaceSlashContextRootOnly()
    {
        ChannelSettings fooChannel = new ChannelSettings("channel-one");
        fooChannel.setUri(clientData5b);        
        String actual = fooChannel.getClientParsedUri("/");
        Assert.assertEquals(clientConfirm5, actual);

        fooChannel.setUri(clientData6b);        
        actual = fooChannel.getClientParsedUri("/ctx");
        Assert.assertEquals(clientConfirm6, actual);
    }
}