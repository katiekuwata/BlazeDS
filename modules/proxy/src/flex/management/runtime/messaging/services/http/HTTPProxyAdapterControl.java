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
package flex.management.runtime.messaging.services.http;

import flex.management.BaseControl;
import flex.management.runtime.messaging.services.ServiceAdapterControl;
import flex.messaging.services.http.HTTPProxyAdapter;

/**
 * @exclude
 * The <code>HTTPProxyAdapterControl</code> class is the MBean implemenation
 * for monitoring and managing <code>HTTPProxyAdapter</code>s at runtime.
 * 
 * @author shodgson
 */
public class HTTPProxyAdapterControl extends ServiceAdapterControl implements
        HTTPProxyAdapterControlMBean
{
    private static final String TYPE = "HTTPProxyAdapter";
    
    /**
     * Constructs a <code>HTTPProxyAdapterControl</code>, assigning its id, managed
     * <code>HTTPProxyAdapter</code> and parent MBean.
     * 
     * @param serviceAdapter The <code>HTTPProxyAdapter</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public HTTPProxyAdapterControl(HTTPProxyAdapter serviceAdapter, BaseControl parent)
    {
        super(serviceAdapter, parent);
    }

    /**
     * @exclude
     *
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getType()
     */
    public String getType()
    {
        return TYPE;
    }
}
