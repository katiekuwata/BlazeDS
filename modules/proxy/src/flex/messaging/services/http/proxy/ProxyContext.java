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

import flex.messaging.services.http.ExternalProxySettings;
import flex.messaging.services.http.HTTPProxyAdapter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.protocol.Protocol;

import java.util.Map;

/**
 * @exclude
 * Store all the information needed for a proxy request.
 *
 * @author Brian Deitte
 * @author Peter Farland
 */
public class ProxyContext extends SharedProxyContext
{
    private HttpMethodBase httpMethod;
    private HttpClient httpClient;

    private String contentType;
    private String url;
    private Target target;
    private Object body;
    private Map headers;

    private ExternalProxySettings externalProxySettings;
    private int cookieLimit = HTTPProxyAdapter.DEFAULT_COOKIE_LIMIT;
    private boolean allowLaxSSL;
    private boolean contentChunked;

    private String credentialsHeader;
    
    // set up by ProxtContextFilter
    private UsernamePasswordCredentials proxyCredentials;
    private HttpConnectionManager connectionManager;
    private Protocol protocol;

    // the status code from the response
    private int statusCode = 200;

    // TODO: Decide whether responses will always be Strings
    private boolean streamResponseToClient;
    private boolean recordHeaders;
    private Map requestHeaders;
    private Map responseHeaders;
    private Object response;


    /*          PROXY COMMUNICATION           */

    public HttpConnectionManager getConnectionManager()
    {
        return connectionManager;
    }

    public void setConnectionManager(HttpConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    public HttpMethodBase getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethodBase httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    public HttpClient getHttpClient()
    {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }


    /*          INPUT           */

    public Map getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map headers)
    {
        this.headers = headers;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String type)
    {
        contentType = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String s)
    {
        url = s;
    }

    public Target getTarget()
    {
        return target;
    }

    public void setTarget(Target target)
    {
        this.target = target;
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    public void setProtocol(Protocol protocol)
    {
        this.protocol = protocol;
    }

    public Object getBody()
    {
        return body;
    }

    public void setBody(Object body)
    {
        this.body = body;
    }

    public String getCredentialsHeader()
    {
        return credentialsHeader;
    }

    public void setCredentialsHeader(String credentialsHeader)
    {
        this.credentialsHeader = credentialsHeader;
    }

    public UsernamePasswordCredentials getProxyCredentials()
    {
        return proxyCredentials;
    }

    public void setProxyCredentials(UsernamePasswordCredentials proxyCredentials)
    {
        this.proxyCredentials = proxyCredentials;
    }

    public int getCookieLimit()
    {
        return cookieLimit;
    }

    public void setCookieLimit(int cookieLimit)
    {
        this.cookieLimit = cookieLimit;
    }

    public boolean allowLaxSSL()
    {
        return allowLaxSSL;
    }

    public void setAllowLaxSSL(boolean allowLaxSSL)
    {
        this.allowLaxSSL = allowLaxSSL;
    }

    public ExternalProxySettings getExternalProxySettings()
    {
        return externalProxySettings;
    }

    public void setExternalProxySettings(ExternalProxySettings externalProxySettings)
    {
        this.externalProxySettings = externalProxySettings;
    }

    public boolean getRecordHeaders()
    {
        return recordHeaders;
    }

    public void setRecordHeaders(boolean recordHeaders)
    {
        this.recordHeaders = recordHeaders;
    }

    public boolean getContentChunked()
    {
        return contentChunked;
    }
    
    public void setContentChunked(boolean value)
    {
        contentChunked = value;
    }


    /*          OUTPUT           */

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int originalStatusCode)
    {
        this.statusCode = originalStatusCode;
    }

    public Object getResponse()
    {
        return response;
    }

    public void setResponse(Object r)
    {
        response = r;
    }

    public boolean streamResponseToClient()
    {
        return streamResponseToClient;
    }

    public void setStreamResponseToClient(boolean s)
    {
        this.streamResponseToClient = s;
    }
    
    public Map getRequestHeaders()
    {
        return requestHeaders;
    }

    public void setRequestHeaders(Map requestHeaders)
    {
        this.requestHeaders = requestHeaders;
    }

    public Map getResponseHeaders()
    {
        return responseHeaders;
    }

    public void setResponseHeaders(Map responseHeaders)
    {
        this.responseHeaders = responseHeaders;
    }
}
