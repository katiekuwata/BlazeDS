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
package flex.messaging.services.http.proxy;

import org.apache.commons.httpclient.auth.AuthScope;

/**
 * @exclude
 * Methods used by multiple proxy classes.
 */
public class ProxyUtil
{
    private static AuthScope anyAuthScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);

    /**
     * Returns the default authScope.
     * 
     * @return The default authScope.
     */
    public static AuthScope getDefaultAuthScope()
    {
        return anyAuthScope;
    }
}
