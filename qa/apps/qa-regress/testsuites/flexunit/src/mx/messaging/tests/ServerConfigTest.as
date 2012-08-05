/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/


package mx.messaging.tests
{

import flash.utils.*;
import flexunit.framework.*;
import mx.messaging.config.*;
import mx.messaging.channels.*;
import mx.messaging.*;
import mx.messaging.messages.*;
import mx.messaging.errors.*;

public class ServerConfigTest extends ConfigurationBasedTestCase
{
	////////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    //////////////////////////////////////////////////////////////////////////// 
    public function ServerConfigTest(methodName:String)
    {
        super(methodName);
    }
    ////////////////////////////////////////////////////////////////////////////
    //
    // TestSuite
    //
    ////////////////////////////////////////////////////////////////////////////
     public static function suite():TestSuite
    {
        var result:TestSuite = new TestSuite();
        result.addTest(new ServerConfigTest("testInvalidChannelError"));
        result.addTest(new ServerConfigTest("testGetThenGetChannel"));
        result.addTest(new ServerConfigTest("testGetChannelSet"));
        result.addTest(new ServerConfigTest("testGetChannelSetErrorForBadMessage"));
        result.addTest(new ServerConfigTest("testGetChannelSetErrorForBadDestination"));
        return result;
    }
	////////////////////////////////////////////////////////////////////////////
    //
    // SetUp
    //
    ////////////////////////////////////////////////////////////////////////////
    override public function setUp():void
    {
        super.setUp();
    }
	////////////////////////////////////////////////////////////////////////////
    //
    // Tests
    //
    ////////////////////////////////////////////////////////////////////////////
    public function testInvalidChannelError():void
    {
        try
        {
            ServerConfig.getChannel("invalid-id");
            fail("Didn't throw an exception.");
        }
        catch(e:InvalidChannelError)
        {
            assertTrue("e is InvalidChannelError", e is InvalidChannelError);
        }
        catch(e:Error)
        {
            fail("e is not InvalidChannelError: " + e.toString());
        }
    }

    public function testGetThenGetChannel():void
    {
        var channel:Channel = ServerConfig.getChannel("qa-amf");
        assertTrue("ServerConfig.getChannel('qa-amf') is AMFChannel", channel is AMFChannel);
        assertEquals("channel.uri == 'http://localhost:8400/qa-regress/messagebroker/amf'", "http://localhost:8400/qa-regress/messagebroker/amf", channel.uri);
        var channel2:Channel = ServerConfig.getChannel("qa-amf");
        assertEquals("channel == channel2", channel, channel2);
    }
    
    public function testGetChannelSet():void
    {
        var asyncMsg:AsyncMessage = new AsyncMessage();
        var cs:ChannelSet = ServerConfig.getChannelSet("MyTopic");
        assertTrue("ServerConfig.getChannelSet('MyTopic') is ChannelSet", cs is ChannelSet);
    }
    
    public function testGetChannelSetErrorForBadMessage():void
    {
        try
        {
            var cs:ChannelSet = ServerConfig.getChannelSet("MyTopic");
        }
        catch (e:InvalidDestinationError)
        {
            assertTrue("e is InvalidDestinationError", e is InvalidDestinationError);
        }
        catch (e:Error)
        {
            fail("e is not InvalidDestinationError: " + e.toString());
        }
    }
    
    public function testGetChannelSetErrorForBadDestination():void
    {
        try
        {
            var cs:ChannelSet = ServerConfig.getChannelSet("bad-destination");
        }
        catch (e:InvalidDestinationError)
        {
            assertTrue("e is InvalidDestinationError", e is InvalidDestinationError);
        }
        catch (e:Error)
        {
            fail("e is not InvalidDestinationError: " + e.toString());
        }
    }
}

}
