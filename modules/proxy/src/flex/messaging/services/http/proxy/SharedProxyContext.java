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
 * Store all the information needed for a proxy request that's used in flex.server.common.proxy.
 *
 * @author Brian Deitte
 */
public class SharedProxyContext
{
    // POST, GET, HEAD etc
    private String method;

    // often-used variables describing the type of request
    private boolean isSoapRequest;
    private boolean isHttpRequest;
    private boolean isClientHttps;

    // whether request has custom auth or Authorization header
    private boolean hasAuthorization;
    // whether endpoint is the same domain as proxy
    private boolean localDomain;
    // whether the endpoint has the same port as the proxy (always false if localDomain is false)
    private boolean localPort;
    // whether request needs browser caching disabled
    private boolean disableCaching;
    // whether target URL came from the client
    private boolean clientTarget;

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public boolean isSoapRequest()
    {
        return isSoapRequest;
    }

    public void setSoapRequest(boolean s)
    {
        isSoapRequest = s;
    }

    public boolean isHttpRequest()
    {
        return isHttpRequest;
    }

    public void setHttpRequest(boolean h)
    {
        isHttpRequest = h;
    }

    public boolean isClientHttps()
    {
        return isClientHttps;
    }

    public void setClientHttps(boolean h)
    {
        isClientHttps = h;
    }

    public boolean hasAuthorization()
    {
        return hasAuthorization;
    }

    public void setAuthorization(boolean hasAuthorization)
    {
        this.hasAuthorization = hasAuthorization;
    }

    public boolean isLocalDomain()
    {
        return localDomain;
    }

    public void setLocalDomain(boolean localDomain)
    {
        this.localDomain = localDomain;
    }
    
    public boolean isLocalPort()
    {
        return localPort;
    }
  
    public void setLocalPort(boolean localPort)
    {
        this.localPort = localPort;
    }
    
    public boolean isLocalDomainAndPort()
    {
        return localDomain && localPort;
    }

    public boolean disableCaching()
    {
        return disableCaching;
    }

    public void setDisableCaching(boolean disableCaching)
    {
        this.disableCaching = disableCaching;
    }

    public boolean isClientTarget()
    {
        return clientTarget;
    }

    public void setClientTarget(boolean clientTarget)
    {
        this.clientTarget = clientTarget;
    }
}
