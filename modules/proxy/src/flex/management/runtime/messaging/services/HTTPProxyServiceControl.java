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
package flex.management.runtime.messaging.services;

import flex.management.BaseControl;
import flex.messaging.services.HTTPProxyService;

/**
 * @exclude
 * The <code>HTTPProxyServiceControl</code> class is the MBean implementation
 * for monitoring and managing a <code>HTTPProxyService</code> at runtime.
 *
 * @author shodgson
 */
public class HTTPProxyServiceControl extends ServiceControl implements
        HTTPProxyServiceControlMBean
{
    private static final String TYPE = "HTTPProxyService";

    /**
     * Constructs a <code>HTTPProxyServiceControl</code>, assigning its id, managed
     * HTTP proxy service and parent MBean.
     *
     * @param service The <code>HTTPProxyService</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public HTTPProxyServiceControl(HTTPProxyService service, BaseControl parent)
    {
        super(service, parent);
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
