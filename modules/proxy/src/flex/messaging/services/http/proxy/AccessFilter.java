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

import flex.messaging.FlexContext;

import javax.servlet.http.HttpServletRequest;

/**
 * @exclude
 * Determines whether overall access to the proxy is allowed for a request.
 */
public class AccessFilter extends ProxyFilter
{
    private static final int TOO_MANY_COOKIES = 10703;

    /**
     * Invokes the filter with the context.
     * 
     * @param context The proxy context.
     */
    public void invoke(ProxyContext context)
    {
        HttpServletRequest clientRequest = FlexContext.getHttpRequest();

        // as requested by @stake, limit the number of cookies that can be sent from the endpoint to prevent
        // as denial of service attack.  It seems our processing of Flex-mangled cookies bogs down the server.
        // We set the cookie limit to 200, but it can be changed via -Dflex.cookieLimit
        if (clientRequest != null)
        {
            javax.servlet.http.Cookie[] cookies = clientRequest.getCookies();
            if (cookies != null && cookies.length > context.getCookieLimit())
            {
                ProxyException e = new ProxyException();
                e.setMessage(TOO_MANY_COOKIES, new Object[] { "" + cookies.length });
                throw e;
            }
        }

        if (next != null)
        {
            next.invoke(context);
        }
    }
}
