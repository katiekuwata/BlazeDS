/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
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
package flex.management.runtime.messaging.services.http;

import java.util.Date;

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.management.runtime.messaging.DestinationControl;
import flex.messaging.services.http.HTTPProxyDestination;;

/**
 * @exclude
 * The <code>HTTPProxyDestinationControl</code> class is the MBean implementation for
 * monitoring and managing a <code>HTTPProxyDestination</code> at runtime.
 * 
 * @author shodgson
 */
public class HTTPProxyDestinationControl extends DestinationControl
    implements HTTPProxyDestinationControlMBean
{
    private static final String TYPE = "HTTPProxyDestination";
    
    private int invokeSOAPCount = 0;
    private Date lastInvokeSOAPTimestamp;
    private long invokeSOAPStart;
    private int invokeHTTPCount = 0;
    private Date lastInvokeHTTPTimestamp;
    private long invokeHTTPStart;
    
    /**
     * Constructs a new <code>HTTPProxyDestinationControl</code> instance.
     * 
     * @param destination The <code>HTTPProxyDestination</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public HTTPProxyDestinationControl(HTTPProxyDestination destination, BaseControl parent)
    {
        super(destination, parent);
        invokeSOAPStart = System.currentTimeMillis();
        invokeHTTPStart = invokeSOAPStart;
    }
    
    /** {@inheritDoc} */
    protected void onRegistrationComplete()
    {
        super.onRegistrationComplete();
        
        String name = this.getObjectName().getCanonicalName();
        
        String[] pollablePerInterval = { "InvokeHTTPCount", "InvokeSOAPCount" };
        String[] pollableGeneral = { "InvokeHTTPFrequency", "InvokeSOAPFrequency" };
        String[] destinationGeneral = { "LastInvokeHTTPTimestamp", "LastInvokeSOAPTimestamp" };
        
        getRegistrar().registerObjects(
                new int[] {AdminConsoleTypes.DESTINATION_POLLABLE, AdminConsoleTypes.GRAPH_BY_POLL_INTERVAL},
                name, pollablePerInterval);
        getRegistrar().registerObjects(AdminConsoleTypes.DESTINATION_POLLABLE, name,
                pollableGeneral);
        getRegistrar().registerObjects(AdminConsoleTypes.DESTINATION_GENERAL, name,
                destinationGeneral);
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#getInvokeSOAPCount()
     */
    public Integer getInvokeSOAPCount()
    {
        return new Integer(invokeSOAPCount);
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#resetInvokeSOAPCount()
     */
    public void resetInvokeSOAPCount()
    {
        invokeSOAPStart = System.currentTimeMillis();
        invokeSOAPCount = 0;
        lastInvokeSOAPTimestamp = null;
    }
    
    /**
     * Increments the count of Soap invocations.
     */
    public void incrementInvokeSOAPCount()
    {
        ++invokeSOAPCount;
        lastInvokeSOAPTimestamp = new Date();
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#getLastInvokeSOAPTimestamp()
     */
    public Date getLastInvokeSOAPTimestamp()
    {
        return lastInvokeSOAPTimestamp;
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#getInvokeSOAPFrequency()
     */
    public Double getInvokeSOAPFrequency()
    {
        if (invokeSOAPCount > 0)
        {
            double runtime = differenceInMinutes(invokeSOAPStart, System.currentTimeMillis());
            return new Double(invokeSOAPCount/runtime);
        }
        else
        {
            return new Double(0);
        }
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#getInvokeHTTPCount()
     */
    public Integer getInvokeHTTPCount()
    {
        return new Integer(invokeHTTPCount);
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#resetInvokeHTTPCount()
     */
    public void resetInvokeHTTPCount()
    {
        invokeHTTPStart = System.currentTimeMillis();
        invokeHTTPCount = 0;
        lastInvokeHTTPTimestamp = null;
    }
    
    /**
     * Increments the count of HTTP invocations.
     */
    public void incrementInvokeHTTPCount()
    {
        ++invokeHTTPCount;
        lastInvokeHTTPTimestamp = new Date();
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#getLastInvokeHTTPTimestamp()
     */
    public Date getLastInvokeHTTPTimestamp()
    {
        return lastInvokeHTTPTimestamp;
    }

    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.runtime.HTTPProxyServiceControlMBean#getInvokeHTTPFrequency()
     */
    public Double getInvokeHTTPFrequency()
    {
        if (invokeHTTPCount > 0)
        {
            double runtime = differenceInMinutes(invokeHTTPStart, System.currentTimeMillis());
            return new Double(invokeHTTPCount/runtime);
        }
        else
        {
            return new Double(0);
        }
    }
    
    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getType()
     */
    public String getType()
    {
        return TYPE;
    }
}
