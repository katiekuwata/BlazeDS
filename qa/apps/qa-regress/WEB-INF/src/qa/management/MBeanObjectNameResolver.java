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

package qa.management;

import java.util.ArrayList;
import java.util.Collection;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;

import flex.management.BaseControl;
import flex.management.jmx.MBeanServerGateway;
import flex.messaging.services.Service;
import flex.messaging.MessageBroker;
import flex.messaging.Destination;
import flex.messaging.MessageDestination;
import flex.messaging.services.ServiceAdapter;
import flex.messaging.endpoints.Endpoint;

/*
 *  helper class to resolve MBean ObjectName
 */
public class MBeanObjectNameResolver
{
    private MBeanServerGateway gateway; 
    private MessageBroker mb;
    
    public MBeanObjectNameResolver()
    {
        mb = MessageBroker.getMessageBroker(null);
        gateway = new MBeanServerGateway();
    }
   
    /*
     *   resolve the MessageBroker MBean name
     *   @return An objectName for the MessageBroker MBean.
     */
    public String getObjectNameForMessageBroker() throws Exception
    {           
        BaseControl bc = mb.getControl(); 
        return bc.getObjectName().toString();
    }  
   
    /*
     *   resolve a service MBean name
     *   @param serviceName The target service name
     *   @return An objectName for the service MBean.
     */
    public String getObjectNameForService(String serviceName) throws Exception
    {         
        Service service = (Service) mb.getService(serviceName);
        if (service == null)
            throw new Exception("Service " + serviceName + " not found"); 
        BaseControl bc = service.getControl(); 
        return bc.getObjectName().toString();
    }     
    
    /*
     *   resolve a destination MBean name
     *   @param serviceName The target service name 
     *   @param destinationName  The target destination name.
     *   @return An objectName for the destination MBean.
     */
    public String getObjectNameForDestination(String serviceName, String destinationName) throws Exception
    {          
        Destination dest = getDestination(serviceName, destinationName);
        BaseControl bc = dest.getControl(); 
        return bc.getObjectName().toString();
    } 
    
    /*
     *   resolve a destination MBean name
     *   @param serviceName The target service name 
     *   @param destinationName  The target destination name.
     *   @param destinationName  The target adapterName name.
     *   @return An objectName for the destination MBean.
     */
    public String getObjectNameForAdapter(String serviceName, String destinationName, String adapterName) throws Exception
    { 
        Destination dest = getDestination(serviceName, destinationName);
        ServiceAdapter adp = dest.getAdapter();
        
        if (adp == null)
            throw new Exception("Adapter " + adapterName + " not found");
        
        BaseControl bc = adp.getControl(); 
        return bc.getObjectName().toString();
    }
    
    /*
     *   resolve a SubscriptionManager MBean name for MessageDestination
     *   @param serviceName The target service name 
     *   @param destinationName  The target destination name.
     *   @return An objectName for the SubscriptionManager MBean.
     */
    public String getObjectNameForSubscriptionManager(String serviceName, String destinationName) throws Exception
    {         
        Destination dest = getDestination(serviceName, destinationName);
        if (!(dest instanceof MessageDestination))
            throw new Exception(destinationName + " not a MessageDestination");
        MessageDestination mdest = (MessageDestination)dest;

        BaseControl bc = mdest.getSubscriptionManager().getControl();     
        return bc.getObjectName().toString();
    }
    
    /*
     *   resolve a ThrottleManager MBean name for MessageDestination
     *   @param serviceName The target service name 
     *   @param destinationName  The target destination name.
     *   @return An objectName for the ThrottleManager MBean.
     */
    public String getObjectNameForThrottleManager(String serviceName, String destinationName) throws Exception
    {         
        Destination dest = getDestination(serviceName, destinationName);
        if (!(dest instanceof MessageDestination))
            throw new Exception(destinationName + " not a MessageDestination");

        MessageDestination mdest = (MessageDestination)dest;
        BaseControl bc = mdest.getThrottleManager().getControl();      
        return bc.getObjectName().toString();
    }
    
    private Destination getDestination(String serviceName, String destinationName) throws Exception
    {
        Service service = (Service) mb.getService(serviceName); 
        if (service == null)
            throw new Exception("Service " + serviceName + " not found"); 
        
        Destination dest = service.getDestination(destinationName); 
        if (dest == null)
            throw new Exception("Destination " + destinationName + " not found");

        return dest;
    }

    /*
     *   resolve an endpoint MBean name
     *   @param endpointName The target endpoint name
     *   @return An objectName for the endpoint MBean.
     */
    public String getObjectNameForEndpoint(String endpointName) throws Exception
    { 
        Endpoint endpoint = (Endpoint) mb.getEndpoint(endpointName);
        if (endpoint == null)
            throw new Exception("Endpoint " + endpointName + " not found"); 
        
        BaseControl bc = endpoint.getControl(); 
        return bc.getObjectName().toString();
    }
    
    /*
     *   resolve FlexClientManagerControl MBean name
     */
    public String getObjectNameForFlexClientManager()
    {
        return mb.getFlexClientManager().getControl().getObjectName().toString();
    }
    
    /*
     *   resolve LogManagerControl MBean name
     */
    public String getObjectNameForLogManager()
    {
        return mb.getLogManager().getControl().getObjectName().toString();
    }
    
    /*
     *   retrieve a list of allowed operation for a given MBean
     *   @param objectName The target MBean ObjectName name
     *   @return A collection of allowed operation for the MBean.
     */    
    public Collection<String> getMBeanOperationList(String objectName)
    {
        ArrayList<String> list = new ArrayList<String>(); 
        
        try 
        {     
            MBeanInfo info = getMBeanInfo(objectName);
            MBeanOperationInfo[] ops = info.getOperations();
            for (int i=0;i<ops.length;i++)
            {
                list.add(ops[i].getName());                
            }
        }
        catch (Exception e)
        {
            //ignore
        }
        return list;
    }
    
    /*
     *   retrieve a list of attributes for a given MBean
     *   @param objectName The target MBean ObjectName name
     *   @return A collection of attributes for the MBean.
     */
    public Collection<String> getMBeanAttributeList(String objectName)
    {
        ArrayList<String> list = new ArrayList<String>(); 
                
        try 
        {  
            MBeanInfo info = getMBeanInfo(objectName);
            MBeanAttributeInfo[] ops = info.getAttributes();
            for (int i=0;i<ops.length;i++)
            {
                list.add(ops[i].getName());                
            }
        }
        catch (Exception e)
        {
           // ignore
        }
        return list;
    } 
    
    /*
     *   retrieve MBeanInfo from the gateway
     */
    private MBeanInfo getMBeanInfo(String objectName)
    {                
        return gateway.getMBeanInfo(objectName).toMBeanInfo();
    }
    
}
