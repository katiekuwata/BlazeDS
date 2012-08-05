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

package flex.messaging;

import java.util.List;
import java.util.Map;

import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.ConfigurationException;
import flex.messaging.factories.JavaFactory;
import flex.messaging.services.HTTPProxyService;
import flex.messaging.services.MessageService;
import flex.messaging.services.RemotingService;
import flex.messaging.services.Service;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MessageBrokerTest extends TestCase
{

    protected MessageBroker broker;

    public MessageBrokerTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(MessageBrokerTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        broker = new MessageBroker(false);
        broker.initThreadLocals();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testAddFactory()
    {
        String id = "java2";
        JavaFactory expected = new JavaFactory();        
        broker.addFactory("java2", expected);
        
        JavaFactory actual = (JavaFactory)broker.getFactory(id);
        Assert.assertEquals(expected, actual);        
    }
    
    public void testAddSameFactoryIdTwice()
    {
        String id = "java";
        JavaFactory expected = new JavaFactory();        
        try
        {
            broker.addFactory(id, expected);
            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.DUPLICATE_COMPONENT_ID;
            Assert.assertTrue(ce.getNumber() == error);
        }                
    }
    
    public void testAddSameFactoryInstanceTwice()
    {
        String id = "java2";
        JavaFactory expected = new JavaFactory();        
        broker.addFactory("java2", expected);
        broker.addFactory("java2", expected);
        
        JavaFactory actual = (JavaFactory)broker.getFactory(id);
        Assert.assertEquals(expected, actual);                
    }
    
    public void testAddFactoryNullId()
    {
        String id = null;
        JavaFactory expected = new JavaFactory();     
        try
        {
            broker.addFactory(id, expected);
            fail("ConfigurationException expected");
        } 
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.NULL_COMPONENT_ID;
            Assert.assertTrue(ce.getNumber() == error);
        }
    }    
    
    public void testGetFactories()
    {
        String id = "java2";
        JavaFactory expected = new JavaFactory();        
        broker.addFactory(id, expected);
        
        int size = broker.getFactories().size();
        Assert.assertEquals(2, size);
    }
    
    public void testRemoveFactory()
    {
        String id = "java";
        broker.removeFactory(id);
        FlexFactory actual = broker.getFactory(id);

        Service expected = null;
        Assert.assertEquals(expected, actual);        
    }
    
    public void testRemoveFactoryNonexistent()
    {
        String id = "nonexistent";
        FlexFactory actual = broker.removeFactory(id);
        
        Assert.assertNull(actual);
    }
    
    
    public void testAddService()
    {
        String id = "remoting-service";
        RemotingService expected = new RemotingService();
        expected.setId(id);
        broker.addService(expected);

        Service actual = broker.getService(id);
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expected.isManaged(), actual.isManaged());
    }

    public void testAddServiceNonManaged()
    {
        String id = "remoting-service";
        RemotingService expected = new RemotingService(false);
        expected.setId(id);
        broker.addService(expected);

        Service actual = broker.getService(id);
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expected.isManaged(), actual.isManaged());
    }    
    
    public void testAddServiceNull()
    {
        try
        {
            broker.addService(null);

            fail("ConfigurationException expected");
        } 
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.NULL_COMPONENT;
            Assert.assertTrue(ce.getNumber() == error);
        }
    }

    public void testAddServiceNullId()
    {
        try
        {
            RemotingService svc = new RemotingService();
            broker.addService(svc);

            fail("ConfigurationException expected");
        } 
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.NULL_COMPONENT_ID;
            Assert.assertTrue(ce.getNumber() == error);
        }
    }

    public void testAddServiceExists()
    {
        try
        {
            String id = "remoting-service";
            RemotingService svc1 = new RemotingService();
            svc1.setId(id);
            broker.addService(svc1);
            HTTPProxyService svc2 = new HTTPProxyService();
            svc2.setId(id);
            broker.addService(svc2);

            fail("ConfigurationException expected");
        } 
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.DUPLICATE_COMPONENT_ID;
            Assert.assertTrue(ce.getNumber() == error);
        }
    }

    public void testCreateService()
    {

        String serviceId = "remoting-service";
        String serviceClass = "flex.messaging.services.RemotingService";
        Service expected = broker.createService(serviceId, serviceClass);

        Service actual = broker.getService(serviceId);
        Assert.assertEquals(expected, actual);
    }

    public void testCreateServiceNonexistingClass()
    {
        String serviceId = "remoting-service";
        String serviceClass = "NonexistingClass";
        
        try
        {
            broker.createService(serviceId, serviceClass);
            fail ("MessageException expected");
        }
        catch (MessageException me)
        {
            int error = 10008;  //ClassUtil.TYPE_NOT_FOUND
            Assert.assertEquals(me.getNumber(), error);
        }

    }
        
    public void testCreateServiceWithExistingId()
    {
        String id = "remoting-service";
        String serviceClass = "flex.messaging.services.RemotingService";
        
        broker.createService(id, serviceClass);
        try
        {
            broker.createService(id, serviceClass);
            
            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.DUPLICATE_COMPONENT_ID; 
            Assert.assertEquals(ce.getNumber(), error);
        }       
    }
    
    public void testRemoveService()
    {
        String id = "remoting-service";
        RemotingService svc = new RemotingService();
        svc.setId(id);
        broker.addService(svc);
        broker.removeService(id);
        Service actual = broker.getService(id);

        Service expected = null;
        Assert.assertEquals(expected, actual);
    }
    
    public void testRemoveServiceNonexistent()
    {
        Service actual = broker.removeService("id");
        Assert.assertNull(actual);                        
    }
    
    public void testGetService()
    {
        String id = "remoting-service";
        RemotingService expected = new RemotingService();
        expected.setId(id);
        broker.addService(expected);
        
        Service actual = broker.getService(id);
        Assert.assertEquals(expected, actual);
    }
    
 
    //Get services_no AUthentication
    public void testGetServices()
    {
        String id = "remoting-service";
        RemotingService expected1 = new RemotingService();
        expected1.setId(id);
        id = "message-service";
        MessageService expected2 = new MessageService();
        expected2.setId(id);
        
        broker.addService(expected1);
        broker.addService(expected2);
        
        Map actual = broker.getServices();
        Assert.assertEquals(expected1, actual.get("remoting-service"));
        Assert.assertEquals(expected2, actual.get("message-service"));
    }
    
    //GetServices should not return an AuthenticationService to the client
//    public void testGetServicesNoAuthenticationSvc()
//    {
//        String id = "remoting-service";
//        RemotingService expected = new RemotingService();
//        expected.setId(id);
//        id = "auth-service";
//        AuthenticationService notExpected = new AuthenticationService();
//        notExpected.setId(id);
//        
//        broker.addService(expected);
//        broker.addService(notExpected);
//        
//        List actual = broker.getServices();
//        Assert.assertEquals(expected, actual.get(0));
//        Assert.assertNotSame(notExpected, actual.get(1));
//        Assert.assertEquals(1, actual.size());
//    }
    
    public void testGetServiceNonExistent()
    {
        Service actual = broker.getService("non-existent-id");
        Assert.assertNull(actual);
    }
        
    public void testGetChannelIdsNull()
    {
        List channelIds = broker.getChannelIds();
        Assert.assertNull(channelIds);
    }
}
