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
package flex.messaging.services.http;

/**
 * External Proxy Settings for a proxy service.
 */
public class ExternalProxySettings
{
    /** @exclude **/
    public static final int DEFAULT_PROXY_PORT = 80;
    /** @exclude **/
    public static final String PORT = "port";
    /** @exclude **/
    public static final String EXTERNAL_PROXY = "external-proxy";
    /** @exclude **/
    public static final String SERVER = "server";
    /** @exclude **/
    public static final String NT_DOMAIN = "nt-domain";
    /** @exclude **/
    public static final String USERNAME = "username";
    /** @exclude **/
    public static final String PASSWORD = "password";    
    private static final String HTTP = "http://";

    private String proxyServer;
    private int proxyPort = DEFAULT_PROXY_PORT;
    private String username;
    private String password;
    private String ntDomain;

    /**
     * Creates a default <code>ExternalProxySettings</code> instance.
     */
    public ExternalProxySettings()
    {
    }

    /**
     * Returns the <code>server</code> property.
     * 
     * @return the property as a string
     */
    public String getProxyServer()
    {
        return proxyServer;
    }

    /**
     * Sets the <code>server</code> property.
     * 
     * @param s The IP or server name of the proxy server.
     */
    public void setProxyServer(String s)
    {
        if (s != null)
        {
            if (s.endsWith("/"))
                s = s.substring(0, s.length() - 1);

            boolean hasProtocol = s.indexOf("://") != -1;
            if (!hasProtocol && isDotNet())
                s = HTTP + s;

            if (!isDotNet() && hasProtocol)
            {
                if (s.startsWith(HTTP))
                    s = s.substring(HTTP.length());
                else
                    throw new IllegalArgumentException("A protocol cannot be specified for the proxy element: " + s);
                // FIXME: Should we throw an exception if a port is specified in the proxy server name?
            }
        }

        proxyServer = s;
    }

    /**
     * Returns the <code>port</code> property.
     * 
     * @return the <code>port</code>
     */
    public int getProxyPort()
    {
        return proxyPort;
    }

    /**
     * Sets the <code>port</code> property. Default is 80.
     * 
     * @param p The port number.
     */
    public void setProxyPort(int p)
    {
        if (p > 0)
        {
            proxyPort = p;
        }
    }

    /**
     * Returns the <code>username</code> property.
     * 
     * @return the <code>username</code>
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the <code>username</code> property. 
     * 
     * @param username The user name for logging into the proxy server.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Returns the <code>password</code> property.
     * 
     * @return the <code>password</code>
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the <code>password</code> property.
     * 
     * @param password The password for loggin into the proxy server.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Returns the <code>nt-domain</code> property.
     * 
     * @return a string containing the <code>nt-domain</code>
     */
    public String getNTDomain()
    {
        return ntDomain;
    }

    /**
     * Sets the <code>nt-domain</code> property.
     * 
     * @param ntDomain The NT domain for the proxy server.
     */
    public void setNTDomain(String ntDomain)
    {
        this.ntDomain = ntDomain;
    }

    /**
     * @exclude
     */
    public static boolean isDotNet()
    {
        return System.getProperty("flex.platform.CLR") != null;
    }
}
