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

import java.io.IOException;

/**
 * Defines the runtime monitoring and management interface for managed polling
 * endpoints.
 */
public interface PollingEndpointControlMBean extends EndpointControlMBean
{
    /**
     * Returns the maximum number of server poll response threads that will be
     * waiting for messages to arrive for clients.
     *
     * @return The maximum number of server poll response threads that will be
     * waiting for messages to arrive for clients.
     * @throws IOException Throws IOException.
     */
    Integer getMaxWaitingPollRequests() throws IOException;

    /**
     * Returns the number of request threads that are currently in the wait state
     * (including those on their way into or out of it).
     *
     * @return The number of request threads that are currently in the wait state.
     * @throws IOException Throws IOException.
     */
    Integer getWaitingPollRequestsCount() throws IOException;
}
