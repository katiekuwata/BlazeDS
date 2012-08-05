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

import flex.messaging.log.Log;
import flex.messaging.services.HTTPProxyService;

/**
 * @exclude
 * Request methods shared by J2EE and .NET.
 *
 * @author Brian Deitte
 */
public class RequestUtil
{
    public static CookieInfo createCookie(CookieInfo cookie, SharedProxyContext context, String targetHost,
                                          String targetPath)
    {
        String path = cookie.path;
        String name = cookie.name;
        if (path == null)
        {
            path = "/";
        }
        String domain = null, actualName = null;

        // FIXME: append domain+path to existing path instead of doing cookie hack?

        //Cookie name format: COOKIE_PREFIX[COOKIE_SEPARATOR]domain[COOKIE_SEPARATOR]path[COOKIE_SEPARATOR]name
        if (name.startsWith(ProxyConstants.COOKIE_PREFIX) && name.indexOf(ProxyConstants.COOKIE_SEPARATOR) != -1)
        {
            //use indexOf and substring instead of split or string tokenizer for performance
            int domainHash;
            int pathHash;
            int startIndex = name.indexOf(ProxyConstants.COOKIE_SEPARATOR) + 1;
            int endIndex = name.indexOf(ProxyConstants.COOKIE_SEPARATOR, startIndex);
            if (endIndex == -1) return null;
            try
            {
                domainHash = Integer.parseInt(name.substring(startIndex, endIndex));
                startIndex = endIndex + 1;
                endIndex = name.indexOf(ProxyConstants.COOKIE_SEPARATOR, startIndex);
                if (endIndex == -1) return null;
                pathHash = Integer.parseInt(name.substring(startIndex, endIndex));
            }
            catch (NumberFormatException e)
            {
                Log.getLogger(HTTPProxyService.LOG_CATEGORY).error("Could not parse cookie hash value in: " + name + " (may be beta cookie)");
                return null;
            }
            startIndex = endIndex + 1;
            if (name.length() <= startIndex) return null;
            actualName = name.substring(startIndex);
            //see if the context.target domain fuzzy matches the cookie's domain
            domain = targetHost;
            while (domain != null && domainHash != domain.hashCode())
            {
                int dotIndex = domain.indexOf(".", 1);
                if (dotIndex != -1)
                {
                    domain = domain.substring(dotIndex);
                }
                else
                {
                    domain = null;
                }
            }
            if (domain == null) return null;

            //see if the context.target path fuzzy matches the cookie's path.  i think this has to be done the long way
            //to make sure we match cases where the path might have ended with a / or not. perhaps
            //we could do it slightly more efficiently by testing /foo/ and /foo in one go but not testing /fo
            path = targetPath;
            while (path != null && path.length() != 0 && pathHash != path.hashCode())
            {
                path = path.substring(0, path.length() - 1);
            }
            if (path == null || path.length() == 0) return null;
        }
        else if (context.isLocalDomain())
        {
            domain = cookie.domain;
            if (domain == null)
            {
                domain = targetHost;
            }
            actualName = cookie.name;
        }
        else
        {
            return null;
        }

        CookieInfo cookieInfo = new CookieInfo(name, domain, actualName, cookie.value, path,
                cookie.maxAge, cookie.maxAgeObj, cookie.secure);
        return cookieInfo;
    }

    public static boolean ignoreHeader(String headerName, SharedProxyContext context)
    {
        boolean ignoreHeader = false;

        // FIXME: do we really want to disallow Host- what does this do?
        if ("Host".equalsIgnoreCase(headerName) ||
                // FIXME: we should really ALWAYS send this header and handle compression within
                // the proxy.  Would save bandwidth when the endpoint could handle it
                "Accept-Encoding".equalsIgnoreCase(headerName) ||
                "Content-Length".equalsIgnoreCase(headerName) ||
                "Set-Cookie".equalsIgnoreCase(headerName) ||
                "Set-Cookie2".equalsIgnoreCase(headerName) ||
                "Cookie".equalsIgnoreCase(headerName) ||
                "Connection".equalsIgnoreCase(headerName) ||
                ProxyConstants.HEADER_CREDENTIALS.equalsIgnoreCase(headerName) ||
                ("Authorization".equalsIgnoreCase(headerName) && (context.hasAuthorization() || !context.isLocalDomain())))
        {

            ignoreHeader = true;
        }
        return ignoreHeader;
    }


}
