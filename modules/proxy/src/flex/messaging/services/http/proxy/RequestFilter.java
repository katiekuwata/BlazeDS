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

import flex.messaging.io.MessageIOConstants;
import flex.messaging.services.http.ExternalProxySettings;
import flex.messaging.services.http.httpclient.FlexGetMethod;
import flex.messaging.services.http.httpclient.FlexPostMethod;
import flex.messaging.services.HTTPProxyService;
import flex.messaging.util.StringUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;

import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;

import flex.messaging.util.URLEncoder;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import flex.messaging.log.Logger;
import flex.messaging.log.Log;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @exclude
 * Sends the request to the endpoint, including custom copying of headers and cookies.
 *
 * @author Brian Deitte
 */
public class RequestFilter extends ProxyFilter
{
    private static final int CAUGHT_ERROR = 10706;
    private static final int UNKNOWN_HOST = 10707;
    private static final int INVALID_METHOD = 10719;
    private static final String STRING_JSESSIONID = "jsessionid";

    public void invoke(ProxyContext context)
    {
        setupRequest(context);
        copyCookiesToEndpoint(context);
        copyHeadersToEndpoint(context);
        addCustomHeaders(context);
        recordRequestHeaders(context);
        sendRequest(context);

        if (next != null)
        {
            next.invoke(context);
        }
    }

    protected void setupRequest(ProxyContext context)
    {
        // set the proxy to send requests through
        ExternalProxySettings externalProxy = context.getExternalProxySettings();
        if (externalProxy != null)
        {
            String proxyServer = externalProxy.getProxyServer();

            if (proxyServer != null)
            {
                context.getTarget().getHostConfig().setProxy(proxyServer, externalProxy.getProxyPort());
                if (context.getProxyCredentials() != null)
                {
                    context.getHttpClient().getState().setProxyCredentials(ProxyUtil.getDefaultAuthScope(), context.getProxyCredentials());
                }
            }
        }

        String method = context.getMethod();
        String encodedPath = context.getTarget().getEncodedPath();
        if (MessageIOConstants.METHOD_POST.equals(method))
        {
            FlexPostMethod postMethod = new FlexPostMethod(encodedPath);
            context.setHttpMethod(postMethod);
            if (context.hasAuthorization())
            {
                postMethod.setConnectionForced(true);
            }
        }
        else if (ProxyConstants.METHOD_GET.equals(method))
        {
            FlexGetMethod getMethod = new FlexGetMethod(context.getTarget().getEncodedPath());
            context.setHttpMethod(getMethod);
            if (context.hasAuthorization())
            {
                getMethod.setConnectionForced(true);
            }
        }
        else if (ProxyConstants.METHOD_HEAD.equals(method))
        {
            HeadMethod headMethod = new HeadMethod(encodedPath);
            context.setHttpMethod(headMethod);
        }
        else if (ProxyConstants.METHOD_PUT.equals(method))
        {
            PutMethod putMethod = new PutMethod(encodedPath);
            context.setHttpMethod(putMethod);
        }
        else if (ProxyConstants.METHOD_OPTIONS.equals(method))
        {
            OptionsMethod optionsMethod = new OptionsMethod(encodedPath);
            context.setHttpMethod(optionsMethod);
        }
        else if (ProxyConstants.METHOD_DELETE.equals(method))
        {
            DeleteMethod deleteMethod = new DeleteMethod(encodedPath);
            context.setHttpMethod(deleteMethod);
        }
        else if (ProxyConstants.METHOD_TRACE.equals(method))
        {
            TraceMethod traceMethod = new TraceMethod(encodedPath);
            context.setHttpMethod(traceMethod);
        }
        else
        {
            ProxyException pe = new ProxyException(INVALID_METHOD);
            pe.setDetails(INVALID_METHOD, "1", new Object[] { method });
            throw pe;
        }

        HttpMethodBase httpMethod = context.getHttpMethod();
        if (httpMethod instanceof EntityEnclosingMethod)
        {
            ((EntityEnclosingMethod)httpMethod).setContentChunked(context.getContentChunked());
        }
    }

    // before calling the endpoint, set up the cookies found in the request
    public static void copyCookiesToEndpoint(ProxyContext context)
    {
        HttpServletRequest clientRequest = FlexContext.getHttpRequest();
        if (clientRequest != null)
        {
            javax.servlet.http.Cookie[] cookies = clientRequest.getCookies();
            HttpState initState = context.getHttpClient().getState();

            if (cookies != null)
            {
                // Gather up the cookies keyed on the length of the path.
                // This is done so that if we have two cookies with the same name,
                // we pass the cookie with the longest path first to the endpoint
                TreeMap cookieMap = new TreeMap();

                for (int i = 0; i < cookies.length; i++)
                {
                    javax.servlet.http.Cookie cookie = cookies[i];
                    CookieInfo origCookie = new CookieInfo(cookie.getName(), cookie.getDomain(), cookie.getName(),
                            cookie.getValue(), cookie.getPath(), cookie.getMaxAge(), null, cookie.getSecure());
                    CookieInfo newCookie = RequestUtil.createCookie(origCookie, context, context.getTarget().getUrl().getHost(),
                            context.getTarget().getUrl().getPath());

                    if (newCookie != null)
                    {
                        Integer pathInt = Integer.valueOf(0 - newCookie.path.length());
                        ArrayList list = (ArrayList)cookieMap.get(pathInt);
                        if (list == null)
                        {
                            list = new ArrayList();
                            cookieMap.put(pathInt, list);
                        }
                        list.add(newCookie);
                    }
                }

                // loop through (in order) the cookies we've gathered
                for (Iterator iterator = cookieMap.values().iterator(); iterator.hasNext();)
                {
                    ArrayList list = (ArrayList)iterator.next();
                    for (Iterator iterator2 = list.iterator(); iterator2.hasNext();)
                    {
                        CookieInfo cookieInfo = (CookieInfo)iterator2.next();
                        if (Log.isInfo())
                        {
                            String str = "-- Cookie in request: " + cookieInfo;
                            Log.getLogger(HTTPProxyService.LOG_CATEGORY).debug(str);
                        }

                        Cookie cookie = new Cookie(cookieInfo.domain, cookieInfo.name, cookieInfo.value, cookieInfo.path,
                                cookieInfo.maxAge, cookieInfo.secure);

                        // If this is a session cookie and we're dealing with local domain, make sure the session
                        // cookie has the latest session id. This check is needed when the session was invalidated
                        // and then recreated in this request; we shouldn't be sending the old session id to the endpoint.
                        if (context.isLocalDomain() && STRING_JSESSIONID.equalsIgnoreCase(cookieInfo.clientName))
                        {
                            FlexSession flexSession = FlexContext.getFlexSession();
                            if (flexSession != null && flexSession.isValid())
                            {
                                String sessionId = flexSession.getId();
                                String cookieValue = cookie.getValue();
                                if (!cookieValue.contains(sessionId))
                                {
                                    int colonIndex = cookieValue.indexOf(":");
                                    if (colonIndex != -1)
                                    {
                                        // Websphere changes jsession id to the following format:
                                        // 4 digit cacheId + jsessionId + ":" + cloneId.
                                        ServletContext servletContext = FlexContext.getServletContext();
                                        String serverInfo = servletContext != null? servletContext.getServerInfo() : null;
                                        boolean isWebSphere = serverInfo != null? serverInfo.contains("WebSphere") : false;
                                        if (isWebSphere)
                                        {
                                            String cacheId = cookieValue.substring(0, 4);
                                            String cloneId = cookieValue.substring(colonIndex);
                                            String wsSessionId = cacheId + sessionId + cloneId;
                                            cookie.setValue(wsSessionId);
                                        }
                                        else
                                        {
                                            cookie.setValue(sessionId);
                                        }
                                    }
                                    else
                                    {
                                        cookie.setValue(sessionId);
                                    }
                                }
                            }
                        }
                        // finally add the cookie to the current request
                        initState.addCookie(cookie);
                    }
                }
            }
        }
    }

    public static void copyHeadersToEndpoint(ProxyContext context)
    {
        HttpServletRequest clientRequest = FlexContext.getHttpRequest();
        if (clientRequest != null)
        {
            Enumeration headerNames = clientRequest.getHeaderNames();
            while (headerNames.hasMoreElements())
            {
                String headerName = (String)headerNames.nextElement();
                if (RequestUtil.ignoreHeader(headerName, context))
                {
                    continue;
                }

                Enumeration headers = clientRequest.getHeaders(headerName);
                while (headers.hasMoreElements())
                {
                    String value = (String)headers.nextElement();
                    context.getHttpMethod().addRequestHeader(headerName, value);

                    if (Log.isInfo())
                    {
                        Log.getLogger(HTTPProxyService.LOG_CATEGORY).debug("-- Header in request: " + headerName + " : " + value);
                    }
                }
            }
        }
    }

    protected void addCustomHeaders(ProxyContext context)
    {
        HttpMethodBase httpMethod = context.getHttpMethod();

        String contentType = context.getContentType();
        if (contentType != null)
        {
            httpMethod.setRequestHeader(ProxyConstants.HEADER_CONTENT_TYPE, contentType);
        }

        Map customHeaders = context.getHeaders();
        if (customHeaders != null)
        {
            Iterator it = customHeaders.keySet().iterator();
            while (it.hasNext())
            {
                String name = (String)it.next();
                Object value = customHeaders.get(name);
                if (value == null)
                {
                    httpMethod.setRequestHeader(name, "");
                }
                // Single value for the name.
                else if (value instanceof String)
                {
                    httpMethod.setRequestHeader(name, (String)value);
                }
                // Multiple values for the name.
                else if (value instanceof List)
                {
                    List valueList = (List)value;
                    for (Iterator iterator = valueList.iterator(); iterator.hasNext();)
                    {
                        Object currentValue = (Object)iterator.next();
                        if (currentValue == null)
                            httpMethod.addRequestHeader(name, "");
                        else
                            httpMethod.addRequestHeader(name, (String)currentValue);
                    }
                }
                else if (value.getClass().isArray())
                {
                    Object[] valueArray = (Object[])value;
                    for (int i = 0; i < valueArray.length; i++)
                    {
                        Object currentValue = valueArray[i];
                        if (currentValue == null)
                            httpMethod.addRequestHeader(name, "");
                        else
                            httpMethod.addRequestHeader(name, (String)currentValue);
                    }
                }
            }
        }

        if (context.isSoapRequest())
        {
            // add the appropriate headers
            context.getHttpMethod().setRequestHeader(ProxyConstants.HEADER_CONTENT_TYPE, MessageIOConstants.CONTENT_TYPE_XML);

            // get SOAPAction, and if it doesn't exist, create it
            String soapAction = null;

            Header header = context.getHttpMethod().getRequestHeader(MessageIOConstants.HEADER_SOAP_ACTION);
            if (header != null)
            {
                soapAction = header.getValue();
            }

            if (soapAction == null)
            {
                HttpServletRequest clientRequest = FlexContext.getHttpRequest();
                if (clientRequest != null)
                {
                    soapAction = clientRequest.getHeader(MessageIOConstants.HEADER_SOAP_ACTION);
                }

                // SOAPAction has to be quoted per the SOAP 1.1 spec.
                if (soapAction != null && !soapAction.startsWith("\"") && !soapAction.endsWith("\""))
                {
                    soapAction = "\"" + soapAction + "\"";
                }

                // If soapAction happens to still be null at this point, we'll end up not sending
                // one, which should generate a fault on the server side which we'll happily convey
                // back to the client.

                context.getHttpMethod().setRequestHeader(MessageIOConstants.HEADER_SOAP_ACTION, soapAction);
            }
        }
    }

    protected void recordRequestHeaders(ProxyContext context)
    {
        if (context.getRecordHeaders())
        {
            Header[] headers = context.getHttpMethod().getRequestHeaders();
            if (headers != null)
            {
                HashMap recordedHeaders = new HashMap();
                for (int i=0; i < headers.length; i++)
                {
                    Header header = headers[i];
                    String headerName = header.getName();
                    String headerValue = header.getValue();
                    Object existingHeaderValue = recordedHeaders.get(headerName);
                    // Value(s) already exist for the header.
                    if (existingHeaderValue != null)
                    {
                        ArrayList headerValues;
                        // Only a single value exists.
                        if (existingHeaderValue instanceof String)
                        {
                            headerValues = new ArrayList();
                            headerValues.add(existingHeaderValue);
                            headerValues.add(headerValue);
                            recordedHeaders.put(headerName, headerValues);
                        }
                        // Multiple values exist.
                        else if (existingHeaderValue instanceof ArrayList)
                        {
                            headerValues = (ArrayList)existingHeaderValue;
                            headerValues.add(headerValue);
                        }
                    }
                    else
                    {
                        recordedHeaders.put(headerName, headerValue);
                    }
                }
                context.setRequestHeaders(recordedHeaders);
            }
        }
    }

    protected void sendRequest(ProxyContext context)
    {
        Target target = context.getTarget();
        String method = context.getMethod();
        HttpMethod httpMethod = context.getHttpMethod();

        if (httpMethod instanceof EntityEnclosingMethod)
        {
            Object data = processBody(context);
            Class dataClass = data.getClass();
            if (data instanceof String)
            {
                String requestString = (String)data;
                if (Log.isInfo())
                {
                    Logger logger = Log.getLogger(HTTPProxyService.LOG_CATEGORY);
                    logger.debug("-- Begin " + method + " request --");
                    logger.debug(StringUtils.prettifyString(requestString));
                    logger.debug("-- End " + method + " request --");
                }

                try
                {
                    StringRequestEntity requestEntity = new StringRequestEntity(requestString, null, "UTF-8");
                    ((EntityEnclosingMethod)httpMethod).setRequestEntity(requestEntity);
                }
                catch (UnsupportedEncodingException ex)
                {
                    ProxyException pe = new ProxyException(CAUGHT_ERROR);
                    pe.setDetails(CAUGHT_ERROR, "1", new Object[] { ex });
                    throw pe;
                }
            }
            else if (dataClass.isArray() && Byte.TYPE.equals(dataClass.getComponentType()))
            {
                byte[] dataBytes = (byte[])data;
                ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(dataBytes, context.getContentType());
                ((EntityEnclosingMethod)httpMethod).setRequestEntity(requestEntity);
            }
            else if (data instanceof InputStream)
            {
                InputStreamRequestEntity requestEntity = new InputStreamRequestEntity((InputStream)data, context.getContentType());
                ((EntityEnclosingMethod)httpMethod).setRequestEntity(requestEntity);
            }
            else
            {
                //TODO: Support multipart post

                //FIXME: Throw exception if unhandled data type
            }
        }
        else if (httpMethod instanceof GetMethod)
        {
            Object req = processBody(context);

            if (req instanceof String)
            {
                String requestString = (String)req;
                if (Log.isInfo())
                {
                    Logger logger = Log.getLogger(HTTPProxyService.LOG_CATEGORY);
                    logger.debug("-- Begin " + method + " request --");
                    logger.debug(StringUtils.prettifyString(requestString));
                    logger.debug("-- End " + method + " request --");
                }

                if (!"".equals(requestString))
                {
                    String query = context.getHttpMethod().getQueryString();
                    if (query != null)
                    {
                        query += "&" + requestString;
                    }
                    else
                    {
                        query = requestString;
                    }
                    context.getHttpMethod().setQueryString(query);
                }
            }
        }

        context.getHttpClient().setHostConfiguration(target.getHostConfig());

        try
        {
            context.getHttpClient().executeMethod(context.getHttpMethod());
        }
        catch (UnknownHostException uhex)
        {
            ProxyException pe = new ProxyException();
            pe.setMessage(UNKNOWN_HOST, new Object[] { uhex.getMessage() });
            pe.setCode(ProxyException.CODE_SERVER_PROXY_REQUEST_FAILED);
            throw pe;
        }
        catch (Exception ex)
        {
            // FIXME: JRB - could be more specific by looking for timeout and sending 504 in that case.
            // rfc2616 10.5.5 504 - could get more specific if we parse the HttpException
            ProxyException pe = new ProxyException(CAUGHT_ERROR);
            pe.setDetails(CAUGHT_ERROR, "1", new Object[] { ex.getMessage() });
            pe.setCode(ProxyException.CODE_SERVER_PROXY_REQUEST_FAILED);
            throw pe;
        }
    }

    protected Object processBody(ProxyContext context)
    {
        //FIXME: Should we also send on URL params that were used to contact the message broker servlet?

        Object body = context.getBody();
        if (body == null)
        {
            return "";
        }
        else if (body instanceof String)
        {
            return (String)body;
        }
        else if (body instanceof Map)
        {
            Map params = (Map)body;

            StringBuffer postData = new StringBuffer();

            boolean formValues = false;
            Iterator iter = params.keySet().iterator();
            while (iter.hasNext())
            {
                String name = (String)iter.next();
                if (! formValues)
                {
                    formValues = true;
                }
                else
                {
                    postData.append("&");
                }

                Object vals = params.get(name);

                if (vals == null)
                {
                    encodeParam(postData, name, "");
                }
                if (vals instanceof String)
                {
                    String val = (String)vals;
                    encodeParam(postData, name, val);
                }
                else if (vals instanceof List)
                {
                    List valLists = (List)vals;

                    for (int i = 0; i < valLists.size(); i++)
                    {
                        Object o = valLists.get(i);
                        String val = "";
                        if (o != null)
                            val = o.toString();

                        if (i > 0)
                            postData.append("&");

                        encodeParam(postData, name, val);
                    }
                }
                else if (vals.getClass().isArray())
                {
                    for (int i = 0; i < Array.getLength(vals); i++)
                    {
                        Object o = Array.get(vals, i);
                        String val = "";
                        if (o != null)
                            val = o.toString();

                        if (i > 0)
                            postData.append("&");

                        encodeParam(postData, name, val);
                    }
                }
            }

            return postData.toString();
        }
        else if (body.getClass().isArray())
        {
            return body;
        }
        else if (body instanceof InputStream)
        {
            return body;
        }
        else
        {
            return body.toString();
        }
    }

    protected void encodeParam(StringBuffer buf, String name, String val)
    {
        name = URLEncoder.encode(name);
        val = URLEncoder.encode(val);

        buf.append(name);
        buf.append("=");
        buf.append(val);
    }
}
