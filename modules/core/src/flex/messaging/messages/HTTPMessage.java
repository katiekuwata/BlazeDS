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
package flex.messaging.messages;

import flex.messaging.util.StringUtils;
import java.util.Map;

/**
 * An HTTPMessage specifies a destination that
 * needs to be resolved into a String
 * representation of an HTTP or HTTPS URI
 * endpoint.
 * <p>
 * The method takes values such as GET, POST,
 * HEAD etc.
 * </p>
 *
 * @author Peter Farland
 * @exclude
 */
public class HTTPMessage extends RPCMessage
{
    public HTTPMessage()
    {
    }

    /**
     * This number was generated using the 'serialver' command line tool.
     * This number should remain consistent with the version used by
     * ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = 5954910346466323369L;

    protected String contentType;
    protected String method;
    protected String url;
    protected Map httpHeaders;
    protected boolean recordHeaders;

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String type)
    {
        contentType = type;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String m)
    {
        if (m != null)
        {
            method = m.trim().toUpperCase();
        }
        else
        {
            method = m;
        }
    }

    public Map getHttpHeaders()
    {
        return httpHeaders;
    }

    public void setHttpHeaders(Map h)
    {
        httpHeaders = h;
    }

    public void setUrl(String s)
    {
        url = s;
    }

    public String getUrl()
    {
        return url;
    }

    public boolean getRecordHeaders()
    {
        return recordHeaders;
    }

    public void setRecordHeaders(boolean recordHeaders)
    {
        this.recordHeaders = recordHeaders;
    }

    protected String toStringFields(int indentLevel)
    {
        String sep = getFieldSeparator(indentLevel);
        StringBuilder sb = new StringBuilder();
        sb.append(sep).append("method = ").append(getMethod()).
           append(sep).append("url = ").append(getUrl()).
           append(sep).append("headers = ").append(getHeaders());
        sb.append(super.toStringFields(indentLevel));
        return sb.toString();
    }

    protected String internalBodyToString(Object body, int indentLevel) 
    {
        return body instanceof String ?
            StringUtils.prettifyString((String) body) :
            super.internalBodyToString(body, indentLevel);
    }
}
