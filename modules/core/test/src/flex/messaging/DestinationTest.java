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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.ConfigurationException;
import flex.messaging.config.NetworkSettings;
import flex.messaging.config.SecurityConstraint;
import flex.messaging.services.HTTPProxyService;
import flex.messaging.services.RemotingService;
import flex.messaging.services.ServiceAdapter;
import flex.messaging.services.http.HTTPProxyAdapter;
import flex.messaging.services.http.HTTPProxyDestination;
import flex.messaging.services.remoting.RemotingDestination;
import flex.messaging.services.remoting.adapters.JavaAdapter;

public class DestinationTest extends TestCase {


    protected Destination destination;
    protected MessageBroker broker;

    public DestinationTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(DestinationTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        destination = new RemotingDestination();
        destination.setId("destId");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testSetService()
    {
        MessageBroker broker = new MessageBroker(false);
        broker.initThreadLocals();

        RemotingService service = new RemotingService();
        service.setId("remoting-service");
        broker.addService(service);
        destination.setService(service);

        RemotingService actualSvc = (RemotingService)destination.getService();

        Assert.assertEquals(service, actualSvc);

        Destination actualDest = service.getDestination(destination.getId());
        Assert.assertEquals(destination, actualDest);
    }

    public void testSetServiceNull()
    {
        try
        {
            destination.setService(null);

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = 11116; //ManageableComponent.NULL_COMPONENT_PROPERTY;
            Assert.assertEquals(ce.getNumber(), error);
        }
    }

    public void testSetAdapter()
    {
        ServiceAdapter expected = new JavaAdapter();
        expected.setId("adapterId");
        destination.setAdapter(expected);

        ServiceAdapter actual = destination.getAdapter();
        Assert.assertEquals(expected, actual);
    }


    public void testSetAdapterNull()
    {
        destination.setAdapter(null);

        ServiceAdapter actual = destination.getAdapter();
        Assert.assertNull(actual);
    }

    public void testSetAdapterNullId()
    {

        ServiceAdapter adapter = new JavaAdapter();
        destination.setAdapter(adapter);

        ServiceAdapter actual = destination.getAdapter();
        Assert.assertEquals(adapter, actual);

    }

    public void testSetAdapterWrongType()
    {
        try
        {
            ServiceAdapter adapter = new HTTPProxyAdapter();
            destination.setAdapter(adapter);

            fail("ClassCastException expected");
        }
        catch (ClassCastException ce)
        {

        }
    }

    public void testAddChannelNotStarted()
    {
        String id = "default-channel";
        destination.addChannel(id);

        boolean contains = destination.getChannels().contains(id);
        Assert.assertTrue(contains);
    }

    /*
     * In running code, the channels would be added from the services-config.xml.
     * Here, we have to force them into the broker.
     */
    public void testAddChannelStartedBrokerKnows()
    {
        start();
        String id = "default-channel";
        Map csMap = new HashMap();
        csMap.put(id, null);
        broker.setChannelSettings(csMap);
        destination.addChannel(id);

        boolean contains = destination.getChannels().contains(id);
        Assert.assertTrue(contains);
    }

    public void testCreateAdapterRegisteredWithService ()
    {
        RemotingService service = new RemotingService();
        service.setId("remoting-service");
        broker = new MessageBroker(false);
        broker.addService(service);

        String adapterId = "id";
        String adapterClass = "flex.messaging.services.remoting.adapters.JavaAdapter";
        service.registerAdapter(adapterId, adapterClass);
        destination.setService(service);

        ServiceAdapter expected = destination.createAdapter(adapterId);

        ServiceAdapter actual = destination.getAdapter();
        Assert.assertEquals(expected, actual);
    }

    public void testCreateAdapterWithoutService()
    {
        try
        {
            destination.createAdapter("id");

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = 11117; // Destination.NO_SERVICE;
            Assert.assertEquals(ce.getNumber(), error);
        }
    }

    public void testCreateAdapterUnregisteredWithService()
    {
        RemotingService service = new RemotingService();
        service.setId("remoting-service");
        broker = new MessageBroker(false);
        broker.addService(service);
        destination.setService(service);

        try
        {
            destination.createAdapter("id");

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.UNREGISTERED_ADAPTER;
            Assert.assertEquals(ce.getNumber(), error);
        }

    }

    public void testCreateAdapterWithExistingId()
    {
        String id = "java-adapter";

        start();

        try
        {
            destination.createAdapter(id);

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = ConfigurationConstants.UNREGISTERED_ADAPTER;
            Assert.assertEquals(error, ce.getNumber());
        }
    }


    public void testSetNetworkSettings()
    {
        NetworkSettings ns = new NetworkSettings();
        ns.setSubscriptionTimeoutMinutes(1);
        destination.setNetworkSettings(ns);

        NetworkSettings actual = destination.getNetworkSettings();
        Assert.assertEquals(ns, actual);
    }

    public void testSetSecurityConstraint()
    {
        SecurityConstraint sc = new SecurityConstraint();
        destination.setSecurityConstraint(sc);

        SecurityConstraint actual = destination.getSecurityConstraint();
        Assert.assertEquals(sc, actual);
    }

    public void testSetSecurityConstraintRefNotStarted()
    {
        String ref = "sample-security";
        destination.setSecurityConstraint(ref);

        SecurityConstraint sc = destination.getSecurityConstraint();
        Assert.assertNull(sc);
    }

    public void testStop()
    {
        start();
        destination.stop();

        boolean started = destination.isStarted();
        Assert.assertFalse(started);
    }

    public void testSetManaged()
    {
        destination.setManaged(true);

        boolean managed = destination.isManaged();
        Assert.assertTrue(managed);
    }

    public void testSetManagedParentUnmanaged()
    {
        RemotingService service = new RemotingService();
        service.setId("remoting-service");
        service.setManaged(false);
        broker = new MessageBroker(false);
        broker.addService(service);

        destination.setService(service);
        destination.setManaged(true);

        boolean managed = destination.isManaged();
        Assert.assertFalse(managed);
    }

    public void testGetLogCategory()
    {
        String logCat = destination.getLogCategory();
        String logCat2 = destination.getLogCategory();
        Assert.assertEquals(logCat, logCat2);
    }

    public void testExtraProperties()
    {
        String propertyName = "extraProperty";
        String propertyValue = "extraValue";


        MessageBroker broker = new MessageBroker(false);
        broker.initThreadLocals();

        RemotingService service = new RemotingService();
        service.setId("remoting-service");
        broker.addService(service);
        destination.setService(service);
        destination.addExtraProperty(propertyName, propertyValue);

        // retrieve the destination and see if the property values still match
        Destination actualDest = service.getDestination(destination.getId());
        Assert.assertEquals(actualDest.getExtraProperty(propertyName), propertyValue);
    }

    private void start()
    {
        broker = new MessageBroker(false);
        HTTPProxyService service = new HTTPProxyService();
        service.setId("proxy-service");
        service.setMessageBroker(broker);
        ServiceAdapter adapter = new HTTPProxyAdapter();
        adapter.setId("java-adapter");
        destination = new HTTPProxyDestination();
        destination.setId("http-proxy-dest");
        destination.setAdapter(adapter);
        destination.addChannel("some-Channel");
        destination.setService(service);
        destination.start();
    }
}
