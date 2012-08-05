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
 * Contants related to the proxy (shared with .NET).
 *
 * @author Brian Deitte
 */
public class ProxyConstants
{
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";
    public static final String METHOD_CONNECT = "CONNECT";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String COOKIE_PREFIX = "FLEX";
    public static final String COOKIE_SEPARATOR = "_";
    public static String HEADER_CREDENTIALS = "credentials";
    public static String HEADER_AUTHENTICATE = "WWW-Authenticate";

    public static final String HTTP_AUTHENTICATION_ERROR = "%%401%%";
    public static final String HTTP_AUTHORIZATION_ERROR = "%%403%%Authorization failed at the remote url.";
    public static final String DOMAIN_ERROR = "The Flex proxy and the specified endpoint do not have the same domain, " +
            "and so basic authentication cannot be used.  Please specify use-custom-authentication or run-as for services not located " +
            "on the same domain as the Flex proxy.";

    public static final String PROXY_SECURITY = "PROXY SECURITY : ";
    public static final String NO_HTTPS_VIA_HTTP = "Invalid URL - can't access HTTPS URLs when accessing proxy via HTTP.";
    public static final String ONLY_HTTP_HTTPS = "Invalid URL - only HTTP or HTTPS URLs allowed";
}
