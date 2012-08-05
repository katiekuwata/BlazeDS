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
package flex.messaging.services.remoting.adapters;

import flex.messaging.MessageException;

/**
 * @exclude
 */
public class NoSuchMethodException extends MessageException
{
    static final long serialVersionUID = -3383405805642648507L;
        
    /**
     * Constructor.
     * 
     * @param message message describing this exception
     */
    public NoSuchMethodException(String s)
    {
        setMessage(s);
        setCode("Server.ResourceUnavailable"); //MessagingException.SERVER_RESOURCE_UNAVAILABLE
    }
}
