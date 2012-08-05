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

package flex.messaging.services;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import flex.messaging.MessageException;
import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.ConfigurationException;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.services.http.HTTPProxyDestination;
import flex.messaging.services.remoting.RemotingDestination;

public class AbstractServiceTest extends TestCase
{
    protected AbstractService service;
    protected MessageBroker broker;
    
    public AbstractServiceTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(AbstractServiceTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        service = new RemotingService();
        service.setId("remoting-service");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public void testSetMessageBroker()
    {
        broker = new MessageBroker(false);
        service.setMessageBroker(broker);
        
        Service actual = broker.getService(service.getId());        
        Assert.assertEquals(service, actual);
        
        MessageBroker actual2 = service.getMessageBroker();
        Assert.assertEquals(broker, actual2);
    }
        
    public void testSetMessageBrokerTwice()
    {
        broker = new MessageBroker(false);
        service.setMessageBroker(broker);
        service.setMessageBroker(broker);
        
        Service actual = broker.getService(service.getId());        
        Assert.assertEquals(service, actual);
    }

    public void testSetMessageBrokerAndAddService()
    {
        broker = new MessageBroker(false);
        service.setMessageBroker(broker);
        broker.addService(service);
        
        Service actual = broker.getService(service.getId());        
        Assert.assertEquals(service, actual);
    }
    
    public void testAddDefaultChannelNotStarted()
    {
        String id = "default-channel";
        service.addDefaultChannel(id);
        
        boolean contains = service.getDefaultChannels().contains(id);
        Assert.assertTrue(contains);
    }
        
    public void testAddDefaultChannelStartedBrokerKnows()
    {         
        start();
        String id = "default-channel";
        Map csMap = new HashMap();
        csMap.put(id, null);
        broker.setChannelSettings(csMap);
        service.addDefaultChannel(id);
        
        boolean contains = service.getDefaultChannels().contains(id);
        Assert.assertTrue(contains);
    }
    
    public void testRemoveDefaultChannel()
    {
        String id = "default-channel";
        service.addDefaultChannel(id);
        service.removeDefaultChannel(id);
        
        boolean contains = service.getDefaultChannels().contains(id);
        Assert.assertFalse(contains);
        
    }
    
    public void testRemoveDefaultChannelNonexistent()
    {
        boolean actual = service.removeDefaultChannel("non-existent-id");
        Assert.assertFalse(actual);
    }
        
    public void testAddDestination()
    {
        String id = "destId";
        Destination expected = new RemotingDestination();
        expected.setId(id);
        broker = new MessageBroker(false);
        broker.addService(service);        
        service.addDestination(expected);

        Destination actual = service.getDestination(id);
        Assert.assertEquals(expected, actual);
    }

    public void testAddDestinationNull()
    {
        try
        {
            service.addDestination(null);

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.NULL_COMPONENT;
            Assert.assertEquals(ce.getNumber(), error);
        }
    }

    public void testAddDestinationNullId()
    {
        try
        {
            Destination destination = new RemotingDestination();
            service.addDestination(destination);

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.NULL_COMPONENT_ID;
            Assert.assertEquals(ce.getNumber(), error);
        }
    }

    public void testAddDestinationWrongType()
    {
        try
        {
            Destination destination = new HTTPProxyDestination();
            service.addDestination(destination);
            
            fail("ClassCastException expected");
        }
        catch (ClassCastException ce)
        {
        }
    }
    
    public void testGetDestinationFromMessage()
    {
        String id = "destId";
        Destination expected = new RemotingDestination();
        expected.setId(id);
        broker = new MessageBroker(false);
        broker.addService(service);
        service.addDestination(expected);
                
        AsyncMessage msg = new AsyncMessage();
        msg.setDestination(id);
        
        Destination actual = service.getDestination(msg);
        Assert.assertEquals(expected, actual);                
    }
    
    public void testGetDestinationFromMessageNonexistent()
    {
        String id = "destId";
        AsyncMessage msg = new AsyncMessage();
        msg.setDestination(id);
        
        try
        {
            service.getDestination(msg);
        }
        catch (MessageException me)
        {
            int error = 0;
            Assert.assertEquals(error, me.getNumber());
        }
    }
    
    public void testAddDestinationExists()
    {
        try
        {
            String id = "destId";
            Destination dest1 = new RemotingDestination();
            dest1.setId(id);
            
            broker = new MessageBroker(false);
            broker.addService(service);
            service.addDestination(dest1);
            
            Destination dest2 = new RemotingDestination();
            dest2.setId(id);
            service.addDestination(dest2);

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.DUPLICATE_DEST_ID;
            Assert.assertEquals(ce.getNumber(), error);
        }
    }

    public void testCreateDestination()
    {
        String id = "destId";
        broker = new MessageBroker(false);
        broker.addService(service);
        Destination expected = service.createDestination(id);

        Destination actual = service.getDestination(id);
        Assert.assertEquals(expected, actual);
    }
        
    public void testCreateDestinationWithExistingId()
    {
        String id = "destId";
        broker = new MessageBroker(false);
        broker.addService(service);
        service.createDestination(id);            
        try
        {
            service.createDestination(id);
            
            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.DUPLICATE_DEST_ID; 
            Assert.assertEquals(ce.getNumber(), error);
        }        
    }

    public void testRemoveDestination()
    {
        String id = "destId";
        Destination dest = new RemotingDestination();
        dest.setId(id);
        broker = new MessageBroker(false);
        broker.addService(service);
        service.addDestination(dest);
        service.removeDestination(id);
        Destination actual = service.getDestination(id);

        Assert.assertNull(actual);
    }

    public void testRemoveDestinationNonexistent()
    {
        Destination actual = service.removeDestination("non-existent");
        Assert.assertNull(actual);
    }
    
    public void testRegisterAdapter()
    {
        String id = "id";
        String expected = "adapterClass";
        service.registerAdapter(id, expected);
        Map adapters = service.getRegisteredAdapters();

        String actual = (String) adapters.get(id);
        Assert.assertEquals(expected, actual);

    }

    public void testRegisterAdapterExisting()
    {
        String id = "id";
        service.registerAdapter(id, "adapterClass");
        String expected = "adapterClass2";
        service.registerAdapter(id, expected);
        Map adapters = service.getRegisteredAdapters();

        String actual = (String) adapters.get(id);
        Assert.assertEquals(expected, actual);
    }

    public void testUnregisterAdapter()
    {
        String id = "id";
        service.registerAdapter(id, "adapterClass");
        service.unregisterAdapter(id);
        Map adapters = service.getRegisteredAdapters();
        String actual = (String) adapters.get(id);

        Assert.assertNull(actual);
    }

    public void testSetDefaultAdapterWithRegisteredAdapter()
    {
        String expected = "id";
        service.registerAdapter(expected, "adapterClass");
        service.setDefaultAdapter(expected);

        String actual = service.getDefaultAdapter();
        Assert.assertEquals(expected, actual);
    }

    public void testSetDefaultAdapterWithWrongId()
    {
        try
        {
            String id = "NonExistantId";
            service.setDefaultAdapter(id);

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.UNREGISTERED_ADAPTER;
            Assert.assertEquals(ce.getNumber(), error);
        }
    }

    public void testGetDefaultAdapterAfterUnregisteringAdapter()
    {
        String id = "id";
        service.registerAdapter(id, "adapterClass");
        service.setDefaultAdapter(id);
        service.unregisterAdapter(id);
        String actual = service.getDefaultAdapter();

        String expected = null;
        Assert.assertEquals(expected, actual);
    }
    
    private void start()
    {
        broker = new MessageBroker(false);
        service.setMessageBroker(broker);    
        service.start();
    }
}

