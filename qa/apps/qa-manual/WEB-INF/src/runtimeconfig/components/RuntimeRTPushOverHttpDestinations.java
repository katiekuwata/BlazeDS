/*************************************************************************
*
* ADOBE CONFIDENTIAL
* __________________
*
*  [2002] - [2007] Adobe Systems Incorporated
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
***************************************************************************/
package runtimeconfig.components;

import flex.messaging.MessageDestination;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.NetworkSettings;
import flex.messaging.config.ServerSettings;
import flex.messaging.services.AbstractBootstrapService;
import flex.messaging.services.MessageService;

/*
 * The purpose of this class is to create dynamic destinations that use the
 * dynamic endpoints defined in RuntimeEnpointsAndFactory class.
 * This test verifies the order of bootstrap services in services-config.xml:  this service
 * must run after the RuntimeEnpointsAndFactory service.
 */
public class RuntimeRTPushOverHttpDestinations extends AbstractBootstrapService
{    

    /*
     * The following destinations replace static versions in messaging-config.xml.
     * These are specifically used for the Realtime Push over HTTP feature.
     * The destination definition used to be:
		<destination id="testhttp1_dest1" parent="service">    
		    <properties>
		        <network>
		            <session-timeout>0</session-timeout>
		        </network>
		        <server>
		            <max-cache-size>1000</max-cache-size>
		            <message-time-to-live>0</message-time-to-live>  
		            <durable>false</durable>    
		        </server>
		    </properties>    
		
		    <channels>
		    	<channel ref="data-testhttp1"/>
		    </channels>    
		</destination>
    */
    public void initialize(String id, ConfigMap properties)
    {
        //Create destination and add to the Service
        MessageService service = (MessageService) broker.getService("message-service");
        String dest = "HTTPLongPollDestination";
        String channel = "data-http-long-poll";
        createDestination(dest, channel, service);
        
        dest = "HTTPWaitingPollRequestDestination";
        channel = "data-http-waiting-poll-requests";
        createDestination(dest, channel, service);
        
        dest = "HTTPPollSecureDestination";
        channel = "data-secure-http-polling";
        createDestination(dest, channel, service);
    }

    private MessageDestination createDestination(String id, String channel, MessageService messageService)
    {
        MessageDestination msgDest;
        msgDest = (MessageDestination)messageService.createDestination(id);

        // <network>
        NetworkSettings ns = new NetworkSettings();
        ns.setSubscriptionTimeoutMinutes(0);
        msgDest.setNetworkSettings(ns);
        
        // <server>
        ServerSettings ss = new ServerSettings();
        ss.setMessageTTL(0);
        ss.setDurable(false);
        msgDest.setServerSettings(ss);
        
        msgDest.addChannel(channel);
        
        return msgDest;
    }
    
    
    public void start()
    {
    	//No-op
    }

    
    public void stop()
    {
    	//No-op
    }
    
}


