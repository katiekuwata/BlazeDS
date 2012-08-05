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

package runtimeconfig.components;

import flex.messaging.config.ConfigMap;
import flex.messaging.services.AbstractBootstrapService;
import flex.messaging.services.RemotingService;
import flex.messaging.services.remoting.RemotingDestination;

/*
 * The purpose of this class is to duplicate the WeatherService remoting destination 
 * using runtime configuration.  This service creates two destinations, one during server startup,
 * the other after the server has started (at runtime).
 */
public class RuntimeRemotingDestination extends AbstractBootstrapService
{

    public void initialize(String id, ConfigMap properties)
    {
        //Create destination and add to the Service
        RemotingService service = (RemotingService) broker.getService("remoting-service");
        String dest = "RemotingDest_startup";
        createDestination(dest, service);
    }

    /*
     * The following is a destination in the eqa application, under messaging-config.xml
     * The method below implements it at runtime
     * 
         <destination id="WeatherService">
            <properties>
                <source>dev.weather.WeatherService</source>
            </properties>
         </destination>
    */

    private RemotingDestination createDestination(String id, RemotingService messageService)
    {
        RemotingDestination remoteDest = (RemotingDestination)messageService.createDestination(id);
        remoteDest.setSource("dev.weather.WeatherService");
        
        return remoteDest;
    }
    
    public void start()
    {
        RemotingService service = (RemotingService) broker.getService("remoting-service");
        String id = "RemotingDest_runtime";
        RemotingDestination destination = createDestination(id, service);
        destination.start();
        
    }
    
    public void stop()
    {
        // No-op
    }
    
}


