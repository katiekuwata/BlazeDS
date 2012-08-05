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

import flex.messaging.io.MessageIOConstants;

/**
 * A SOAP specific subclass of HTTPMessage. By default we
 * assume the content-type as &quot;text/xml; charset=utf-8&quot;
 * and the HTTP method will be POST.
 *
 * @author Peter Farland
 * @exclude
 */
public class SOAPMessage extends HTTPMessage
{
    /**
     * This number was generated using the 'serialver' command line tool.
     * This number should remain consistent with the version used by
     * ColdFusion to communicate with the message broker over RMI.
     */
    private static final long serialVersionUID = 3706466843618325314L;

    public SOAPMessage()
    {
        contentType = MessageIOConstants.CONTENT_TYPE_XML;
        method = MessageIOConstants.METHOD_POST;
    }

    public String getAction()
    {
        Object action = httpHeaders.get(MessageIOConstants.HEADER_SOAP_ACTION);
        return action == null ? null : action.toString();
    }

    public void setAction(String action)
    {
        httpHeaders.put(MessageIOConstants.HEADER_SOAP_ACTION, action);
    }
}
