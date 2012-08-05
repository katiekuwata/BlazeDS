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

import flex.messaging.MessageException;

/**
 * @exclude
 * Simple exception used to get back to ErrorFilter from other filters.
 *
 * @author Brian Deitte
 */
public class ProxyException extends MessageException
{
    static final long serialVersionUID = -6516172702871227717L;

    public static final String CODE_SERVER_PROXY_REQUEST_FAILED = "Server.Proxy.Request.Failed";

    //--------------------------------------------------------------------------
    //
    // Constructors
    //
    //--------------------------------------------------------------------------

    /**
     * Default constructor.
     */
    public ProxyException()
    {
        super();
        super.setCode(CODE_SERVER_PROXY_REQUEST_FAILED);
    }

    /**
     * Constructor with a message.
     *
     * @param message The detailed message for the exception.
     */
    public ProxyException(int message)
    {
        this();
        setMessage(message);
    }
}