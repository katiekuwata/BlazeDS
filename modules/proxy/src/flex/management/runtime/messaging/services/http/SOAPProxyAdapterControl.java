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
import flex.messaging.services.http.SOAPProxyAdapter;

/**
 * @exclude
 * The <code>SOAPProxyAdapterControl</code> class is the MBean implementation
 * for monitoring and managing <code>SOAPProxyAdapter</code>s at runtime.
 * 
 * @author shodgson
 */
public class SOAPProxyAdapterControl extends HTTPProxyAdapterControl implements
        SOAPProxyAdapterControlMBean
{
    private static final String TYPE = "SOAPProxyAdapter";
    
    /**
     * Constructs a <code>SOAPProxyAdapterControl</code>, assigning its id, managed
     * <code>SOAPProxyAdapter</code> and parent MBean.
     * 
     * @param serviceAdapter The <code>SOAPProxyAdapter</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public SOAPProxyAdapterControl(SOAPProxyAdapter serviceAdapter, BaseControl parent)
    {
        super(serviceAdapter, parent);
    }

    /**
     * @exclude
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getType()
     */
    public String getType()
    {
        return TYPE;
    }
}
