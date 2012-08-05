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
package flex.management;

import flex.messaging.MessageBroker;

import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Helper class for managing MBean lifecycles externally from the core server
 * components where necessary.
 * 
 * @author shodgson
 */
public class MBeanLifecycleManager
{
    /**
     * Unregisters all runtime MBeans that are registered in the same domain as the 
     * MessageBrokerControl for the target MessageBroker. 
     *  
     * @param broker The MessageBroker component that has been stopped.
     */
    public static void unregisterRuntimeMBeans(MessageBroker broker)
    {        
        MBeanServer server = MBeanServerLocatorFactory.getMBeanServerLocator().getMBeanServer();
        ObjectName brokerMBean = broker.getControl().getObjectName();
        String domain = brokerMBean.getDomain();
        try
        {
            ObjectName pattern = new ObjectName(domain + ":*");
            Set names = server.queryNames(pattern, null);
            Iterator iter = names.iterator();
            while (iter.hasNext())
            {
                ObjectName on = (ObjectName)iter.next();
                server.unregisterMBean(on);
            }
        }
        catch (Exception e)
        {
            // We're generally unregistering these during shutdown (possibly JVM shutdown)
            // so there's nothing to really do here because we aren't guaranteed access to
            // resources like system log files, localized messaging, etc.
        }
    }
    
}
