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
package flex.messaging.services.messaging;

import flex.messaging.MessageClient;
import flex.messaging.MessageDestination;

import java.util.Iterator;

/**
 * @exclude
 */
public class RemoteMessageClient extends MessageClient
{
    /**
     * @exclude
     */
    private static final long serialVersionUID = -4743740983792418491L;

    /**
     * Constructor.
     * 
     * @param clientId The client id.
     * @param destination The message destination.
     * @param endpointId The endpoint id.
     */
    public RemoteMessageClient(Object clientId, MessageDestination destination, String endpointId)
    {
        super(clientId, destination, endpointId, false /* do not use session */);
    }

    /**
     * Invalidates the RemoteMessageClient.
     */
    public void invalidate()
    {
        synchronized (lock)
        {
            if (!valid)
                return;
        }

        if (destination instanceof MessageDestination)
        {
            MessageDestination msgDestination = (MessageDestination)destination;
            for (Iterator it = subscriptions.iterator(); it.hasNext(); )
            {
                SubscriptionInfo si = (SubscriptionInfo)it.next();
                msgDestination.getRemoteSubscriptionManager().removeSubscriber(clientId,
                        si.selector, si.subtopic, null);
            }
        }
    }
}
