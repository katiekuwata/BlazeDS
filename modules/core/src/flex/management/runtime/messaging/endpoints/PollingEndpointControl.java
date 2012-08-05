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
package flex.management.runtime.messaging.endpoints;

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.messaging.endpoints.BasePollingHTTPEndpoint;

/**
 * The <tt>PollingEndpointControl</tt> class is the base MBean implementation
 * for monitoring and managing a <tt>BasePollingHTTPEndpoint</tt> at runtime.
 */
public abstract class PollingEndpointControl extends EndpointControl implements
        PollingEndpointControlMBean
{
    /**
     * Constructs a <tt>PollingEndpointControl</tt>, assigning managed message
     * endpoint and parent MBean.
     *
     * @param endpoint The <code>BasePollingHTTPEndpoint</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public PollingEndpointControl(BasePollingHTTPEndpoint endpoint, BaseControl parent)
    {
        super(endpoint, parent);
    }

    protected void onRegistrationComplete()
    {
        super.onRegistrationComplete();

        String name = this.getObjectName().getCanonicalName();
        String[] generalPollables = {"WaitingPollRequestsCount"};

        getRegistrar().registerObjects(AdminConsoleTypes.ENDPOINT_POLLABLE, name, generalPollables);
        getRegistrar().registerObject(AdminConsoleTypes.ENDPOINT_SCALAR, name, "MaxWaitingPollRequests");
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.messaging.endpoints.PollingEndpointControlMBean#getMaxWaitingPollRequests()
     */
    public Integer getMaxWaitingPollRequests()
    {
        int maxWaitingPollRequests = ((BasePollingHTTPEndpoint)endpoint).getMaxWaitingPollRequests();
        return new Integer(maxWaitingPollRequests);
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.messaging.endpoints.PollingEndpointControlMBean#getWaitingPollRequestsCount()
     */
    public Integer getWaitingPollRequestsCount()
    {
        int waitingPollRequestsCount = ((BasePollingHTTPEndpoint)endpoint).getWaitingPollRequestsCount();
        return new Integer(waitingPollRequestsCount);
    }
}
