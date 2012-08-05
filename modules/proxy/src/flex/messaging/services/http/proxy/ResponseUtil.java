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

/**
 * @exclude
 * Response methods shared by J2EE and .NET.
 *
 * @author Brian Deitte
 */
public class ResponseUtil
{
    public static String getCookieName(ProxyContext context, String path, String name, String domain)
    {
        String clientName;
        if (context.isLocalDomain() && (path == null || path.equals("/")))
        {
            clientName = name;
        }
        else
        {
            //Cookie name format: COOKIE_PREFIX[COOKIE_SEPARATOR]domain[COOKIE_SEPARATOR]path[COOKIE_SEPARATOR]name
            StringBuffer nameBuf = new StringBuffer(40); //estimated length to usually avoid the buffer needing to grow
            nameBuf.append(ProxyConstants.COOKIE_PREFIX);
            nameBuf.append(ProxyConstants.COOKIE_SEPARATOR);
            nameBuf.append(domain.hashCode());
            nameBuf.append(ProxyConstants.COOKIE_SEPARATOR);
            nameBuf.append(path.hashCode());
            nameBuf.append(ProxyConstants.COOKIE_SEPARATOR);
            nameBuf.append(name);
            clientName = nameBuf.toString();
        }
        return clientName;
    }


    public static boolean ignoreHeader(String name, ProxyContext context)
    {
        boolean ignoreHeader = false;
        if ("Content-Length".equalsIgnoreCase(name) ||
                "Set-Cookie".equalsIgnoreCase(name) ||
                "Set-Cookie2".equalsIgnoreCase(name) ||
                "Cookie".equalsIgnoreCase(name) ||
                "Transfer-Encoding".equalsIgnoreCase(name) ||
                // cmurphy - copying "Connection" was causing problems with WebLogic 8.1
                // brian- Connection header specifies what type of connection is wanted, ie keep-alive.
                // From what I've read, it is perfectly acceptible for a proxy to ignore this header
                "Connection".equalsIgnoreCase(name) ||
                // ignore caching headers if we want to stop caching on this request
                (context.disableCaching() && ("Cache-Control".equalsIgnoreCase(name) ||
                "Expires".equalsIgnoreCase(name) || "Pragma".equalsIgnoreCase(name)))
        )
        {
            ignoreHeader = true;
        }
        return ignoreHeader;
    }
}
