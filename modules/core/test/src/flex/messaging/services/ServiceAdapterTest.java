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

import flex.messaging.Destination;
import flex.messaging.config.ConfigurationException;
import flex.messaging.services.http.HTTPProxyAdapter;
import flex.messaging.services.http.HTTPProxyDestination;
import flex.messaging.services.remoting.RemotingDestination;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ServiceAdapterTest extends TestCase
{
    protected ServiceAdapter adapter;
    protected Destination destination;
    
    public ServiceAdapterTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ServiceAdapterTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        adapter = new HTTPProxyAdapter();
        adapter.setId("proxy-adapter");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public void testSetDestination()
    {
        destination = new HTTPProxyDestination();
        adapter.setDestination(destination);
        
        Destination actual = adapter.getDestination();
        Assert.assertEquals(destination, actual);
        
        ServiceAdapter actual2 = destination.getAdapter();
        Assert.assertEquals(adapter, actual2);
    }
    
    public void testSetDestinationNull()
    {        
        try
        {
            adapter.setDestination(null);

            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            int error = 11116; // ManageableComponent.NULL_COMPONENT_PROPERTY;
            Assert.assertEquals(ce.getNumber(), error);
        }
    }
        
    public void testSetDestinationWrongType()
    {
        destination = new RemotingDestination();
        
        try 
        {
            adapter.setDestination(destination);
            
            fail("ClassCastException expected");
        }
        catch (ClassCastException e)
        {            
        }        
    }
    
    public void testSetManagedParentUnmanaged()
    {
        destination = new HTTPProxyDestination();
        destination.setManaged(false);
        destination.setAdapter(adapter);
        adapter.setManaged(true);

        boolean managed = adapter.isManaged();
        Assert.assertFalse(managed);
        
    }
       
    public void testSetManaged()
    {
        adapter.setManaged(true);
        
        boolean managed = adapter.isManaged();        
        Assert.assertTrue(managed);
    }

}

