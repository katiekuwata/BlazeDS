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

import org.apache.commons.httpclient.HostConfiguration;

import java.net.URL;

/**
 * @exclude
 * Encapsulates information about a proxy target.
 *
 * @author Brian Deitte
 */
public class Target
{
    // FIXME: this class turned out not to be as useful as originally thought.  Should move this information
    // directly into ProxyContext

    private URL url;
    private boolean useCustomAuthentication = true;
    private boolean isHTTPS;
    private String encodedPath;
    private String remoteUsername;
    private String remotePassword;
    private HostConfiguration hostConfig;

    public URL getUrl()
    {
        return url;
    }

    public void setUrl(URL url)
    {
        this.url = url;
    }

    public boolean isHTTPS()
    {
        return isHTTPS;
    }

    public void setHTTPS(boolean HTTPS)
    {
        isHTTPS = HTTPS;
    }

    public String getEncodedPath()
    {
        return encodedPath;
    }

    public void setEncodedPath(String encodedPath)
    {
        this.encodedPath = encodedPath;
    }

    public HostConfiguration getHostConfig()
    {
        return hostConfig;
    }

    public void setHostConfig(HostConfiguration hostConfig)
    {
        this.hostConfig = hostConfig;
    }

    public String getRemoteUsername()
    {
        return remoteUsername;
    }

    public void setRemoteUsername(String name)
    {
        remoteUsername = name;
    }

    public String getRemotePassword()
    {
        return remotePassword;
    }

    public void setRemotePassword(String pass)
    {
        remotePassword = pass;
    }

    public boolean useCustomAuthentication()
    {
        return useCustomAuthentication;
    }

    public void setUseCustomAuthentication(boolean b)
    {
        useCustomAuthentication = b;
    }
}
