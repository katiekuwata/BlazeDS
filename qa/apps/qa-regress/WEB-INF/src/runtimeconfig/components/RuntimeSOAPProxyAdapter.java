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
import flex.messaging.services.HTTPProxyService;
import flex.messaging.services.http.HTTPProxyDestination;
import flex.messaging.services.http.HTTPConnectionManagerSettings;
import flex.messaging.services.http.SOAPProxyAdapter;

/*
 * The purpose of this class is to create runtime-configured SOAP Proxy destinations
 * that are part of qa-regress automated testing.
 */
public class RuntimeSOAPProxyAdapter extends AbstractBootstrapService
{
    
   public void initialize(String id, ConfigMap properties)
    {
        // Get the instance of the proxy-service from MessageBroker
        HTTPProxyService proxyService = (HTTPProxyService)broker.getService("proxy-service");
        
        // Create destination and add to the Service
        id = "SOAPProxyDest_startup";        
        HTTPProxyDestination proxyDest = createProxyDestination(id, proxyService);                   
        
        // This is needed to set the properties on the adapter: after both service 
        // and destination exist
        createAdapter(proxyDest); 
    }

    /*
     * This method duplicates the echoService destination, as defined below.
     * It is also used to add a channel that is not defined in services-config.sml, which causes a warning during startup
        <destination id="echoService">
        
            <properties>
                <wsdl>http://10.60.144.67:8080/axis/services/echo?wsdl</wsdl>
                <soap>http://10.60.144.67:8080/axis/services/echo</soap>
            </properties>
            
            <security>
                <!-- <run-as username="freddie" password="nightmare"/> -->
            </security>
            
            <adapter ref="soap-proxy"></adapter>
        </destination>
     */   
    private HTTPProxyDestination createProxyDestination(String id, HTTPProxyService proxyService)
    {
        HTTPProxyDestination proxyDest = new HTTPProxyDestination(true);
        proxyDest.setId(id);
        //proxyDest.setService(proxyService);
        proxyService.addDestination(proxyDest);
       
        //Test warning "No channel with id '{0}' is known by the MessageBroker. Not adding the channel.",
        proxyDest.addChannel("bogus-channel-test");
         
        // set destination properties
        proxyDest.setDefaultUrl("http://10.60.144.67:8080/axis/services/echo?wsdl");
        proxyDest.addDynamicUrl("http://10.60.144.67:8080/axis/services/echo");
               
        return proxyDest;
    }
   
    
    /* This method defines the following properties in the adapter itself, which are NOT part
     * of the service, as the proxy-config.xml would have you think.
        <properties>
            <connection-manager>
                <max-total-connections>100</max-total-connections>
                <default-max-connections-per-host>2</default-max-connections-per-host>
            </connection-manager>
            <allow-lax-ssl>true</allow-lax-ssl>
        </properties>

     */
    private void createAdapter(HTTPProxyDestination proxyDest)
    {
        // Create an adapter for the destination
        SOAPProxyAdapter proxyAdapter = new SOAPProxyAdapter();
       
        // Set adapter's id
        proxyAdapter.setId("runtime-soap-proxy");
       
        // Set adapter's management property
        proxyAdapter.setManaged(true);
       
        // Set adapter's parent (which also sets destination's adapter)
        //proxyAdapter.setDestination(proxyDest);
       
        // Alternatively, we could have set destination's adapter
        proxyDest.setAdapter(proxyAdapter);
       
        // Set some adapter properties
        proxyAdapter.setAllowLaxSSL(true);
       
        int maxTotal = 100;
        int defaultMaxConnsPerHost = 2;
        HTTPConnectionManagerSettings connectionParams = new HTTPConnectionManagerSettings();
        connectionParams.setMaxTotalConnections(maxTotal);
        connectionParams.setDefaultMaxConnectionsPerHost(defaultMaxConnsPerHost);   
       
        proxyAdapter.setConnectionManagerSettings(connectionParams);
    }     
    

    public void start()
    {
        // Get the instance of the proxy-service from MessageBroker
        HTTPProxyService proxyService = (HTTPProxyService)broker.getService("proxy-service");
        
        // Create destination and add to the Service
        String id = "SOAPProxyDest_runtime";        
        HTTPProxyDestination proxyDest = createProxyDestination(id, proxyService);          
        // This is needed to set the properties on the adapter: after both service 
        // and destination exist
        //createAdapter(proxyDest); 
        
        //Must start destination in order to be usable
        proxyDest.start();
    }
    
    public void stop()
    {
        // No-op
    }
}


