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
package flex.messaging.services.messaging.adapters;

import java.util.Set;

import flex.messaging.services.MessageService;
import flex.messaging.services.messaging.SubscriptionManager;
import flex.messaging.messages.Message;
import flex.messaging.Destination;
import flex.messaging.MessageDestination;
import flex.management.runtime.messaging.services.messaging.adapters.ActionScriptAdapterControl;

/**
 * An ActionScript object based adapter for the MessageService
 * that supports simple publish/subscribe messaging between
 * ActionScript based clients.
 */
public class ActionScriptAdapter extends MessagingAdapter
{
    private ActionScriptAdapterControl controller;

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a default <code>ActionScriptAdapter</code>.
     */
    public ActionScriptAdapter()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    // Public Getters and Setters for ServiceAdapter properties
    //                             
    //--------------------------------------------------------------------------

    /**
     * Casts the <code>Destination</code> into <code>MessageDestination</code>
     * and calls super.setDestination.
     * 
     * @param destination
     */
    public void setDestination(Destination destination)
    {
        Destination dest = (MessageDestination)destination;
        super.setDestination(dest);
    }
    
    //--------------------------------------------------------------------------
    //
    // Other Public APIs
    //                 
    //--------------------------------------------------------------------------
    
    /**
     * Handle a data message intended for this adapter.
     */
    public Object invoke(Message message)
    {
        MessageDestination destination = (MessageDestination)getDestination();
        MessageService msgService = (MessageService)destination.getService();

        SubscriptionManager subscriptionManager = destination.getSubscriptionManager();
        Set subscriberIds = subscriptionManager.getSubscriberIds(message, true /*evalSelector*/);
        if (subscriberIds != null && !subscriberIds.isEmpty())
        {
            /* We have already filtered based on the selector and so pass false below */
            msgService.pushMessageToClients(destination, subscriberIds, message, false);
        }
        msgService.sendPushMessageFromPeer(message, destination, true);

        return null;
    }

    /**
     * Invoked automatically to allow the <code>ActionScriptAdapter</code> to setup its corresponding
     * MBean control.
     * 
     * @param broker The <code>Destination</code> that manages this <code>ActionScriptAdapter</code>.
     */
    protected void setupAdapterControl(Destination destination)
    {
        controller = new ActionScriptAdapterControl(this, destination.getControl());
        controller.register();
        setControl(controller);
    }
}
