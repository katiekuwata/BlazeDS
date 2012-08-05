/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL
 * __________________
 * 
 *  Copyright 2007 Adobe Systems Incorporated 
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
 */

package runtimeconfig.remoteobjects;

import flex.messaging.MessageBroker;
import flex.messaging.MessageDestination;
import flex.messaging.config.NetworkSettings;
import flex.messaging.config.ServerSettings;
import flex.messaging.config.ThrottleSettings;
import flex.messaging.services.MessageService;

/*
 * The purpose of this remote object is to allow a client to remove a destination and recreate it.
 * The original destination 
 * The roMessageDestinationTest invokes this object passing the destination id.
 */
public class ROMessageDestination
{    
	private MessageService service;
	
    public ROMessageDestination()
    {
        MessageBroker broker = MessageBroker.getMessageBroker(null);
        //Get the service
        service = (MessageService) broker.getService("message-service");    	
    }

    // Remove the destination and check that its ThrottleManager and SubscriptionManager are removed (use ds-console app for this)
    public void removeDestination(String id)
    {
        //Remove the destination 
        service.removeDestination(id);
    }   
     
    public void createDestination(String id)
    {
        MessageDestination msgDest = (MessageDestination)service.createDestination(id);
        
        // <network>
        NetworkSettings ns = new NetworkSettings();
        ns.setSubscriptionTimeoutMinutes(30);
        ns.setSharedBackend(true);
        ns.setClusterId(null);
        ThrottleSettings ts = new ThrottleSettings();
        ts.setInboundPolicy(ThrottleSettings.Policy.ERROR);
        ts.setIncomingClientFrequency(0);
        ts.setOutboundPolicy(ThrottleSettings.Policy.NONE);
        ts.setOutgoingClientFrequency(0);
        ns.setThrottleSettings(ts);   
        msgDest.setNetworkSettings(ns);
        
        // <server>
        ServerSettings ss = new ServerSettings();
        ss.setMessageTTL(100);
        ss.setDurable(false);
        ss.setAllowSubtopics(true);
        msgDest.setServerSettings(ss);
        
        // <channels>
        msgDest.addChannel("qa-http-polling");
        
        msgDest.start();  
    }
}


