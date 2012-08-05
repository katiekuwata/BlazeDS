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

import java.lang.reflect.Method;

import javax.management.MBeanServer;

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.util.ClassUtil;

/**
 * Custom MBeanServerLocator for use with WebSphere.
 * This class locates a MBean server instance via WebSphere's administration APIs.
 */
public class WebSphereMBeanServerLocator implements MBeanServerLocator
{
    //--------------------------------------------------------------------------
    //
    // Private Static Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     * Localized error constant.
     */
    private static final int FAILED_TO_LOCATE_MBEAN_SERVER = 10427;
    
    //--------------------------------------------------------------------------
    //
    // Private Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     * Reference to MBeanServer this locator found.
     */
    private MBeanServer server;

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------
    
    /** {@inheritDoc} */
    public synchronized MBeanServer getMBeanServer()
    {
        if (server == null)
        {
            Class adminServiceClass = ClassUtil.createClass("com.ibm.websphere.management.AdminServiceFactory");
            try
            {
                Method getMBeanFactoryMethod = adminServiceClass.getMethod("getMBeanFactory", new Class[0]);
                Object mbeanFactory = getMBeanFactoryMethod.invoke(null, new Object[0]);
                Method getMBeanServerMethod = mbeanFactory.getClass().getMethod("getMBeanServer", new Class[0]);
                server = (MBeanServer)getMBeanServerMethod.invoke(mbeanFactory, new Object[0]); 
            }
            catch (Exception e)
            {
                ManagementException me = new ManagementException();
                me.setMessage(FAILED_TO_LOCATE_MBEAN_SERVER, new Object[] {getClass().getName()});
                me.setRootCause(e);
                throw me;
            }
            if (Log.isDebug())
                Log.getLogger(LogCategories.MANAGEMENT_MBEANSERVER).debug("Using MBeanServer: " + server);
        }
        return server;
    }
}