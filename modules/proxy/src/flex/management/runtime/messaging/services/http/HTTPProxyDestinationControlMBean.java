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
package flex.management.runtime.messaging.services.http;

import java.io.IOException;
import java.util.Date;

import flex.management.runtime.messaging.DestinationControlMBean;

/**
 * @exclude
 * Defines the runtime monitoring and management interface for managed
 * <code>HTTPProxyDestination</code>s.
 * 
 * @author shodgson
 */
public interface HTTPProxyDestinationControlMBean extends
        DestinationControlMBean
{
    /**
     * Returns the number of SOAP invocations the HTTP proxy service has processed.
     * 
     * @return The number of SOAP invocations the HTTP proxy service has processed.
     * @throws IOException Throws IOException.
     */
    Integer getInvokeSOAPCount() throws IOException;
    
    /**
     * Resets the count of SOAP invocations.
     *
     * @throws IOException Throws IOException.
     */
    void resetInvokeSOAPCount() throws IOException;
    
    /**
     * Returns the timestamp of the most recent SOAP invocation processed by the
     * HTTP proxy service.
     * 
     * @return The timestamp for the most recent SOAP invocation.
     * @throws IOException Throws IOException.
     */
    Date getLastInvokeSOAPTimestamp() throws IOException;
    
    /**
     * Returns the number of SOAP invocations per minute.
     *
     * @return The number of SOAP invocations per minute.
     * @throws IOException Throws IOException.
     */
    Double getInvokeSOAPFrequency() throws IOException;
    
    /**
     * Returns the number of HTTP invocations the HTTP proxy service has processed.
     * 
     * @return The number of HTTP invocations the HTTP proxy service has processed.
     * @throws IOException Throws IOException.
     */
    Integer getInvokeHTTPCount() throws IOException;
    
    /**
     * Resets the count of HTTP invocations.
     *
     * @throws IOException Throws IOException.
     */
    void resetInvokeHTTPCount() throws IOException;
    
    /**
     * Returns the timestamp of the most recent HTTP invocation processed by the
     * HTTP proxy service.
     * 
     * @return The timestamp for the most recent HTTP invocation.
     * @throws IOException Throws IOException.
     */
    Date getLastInvokeHTTPTimestamp() throws IOException;
    
    /**
     * Returns the number of HTTP invocations per minute.
     *
     * @return The number of HTTP invocations per minute.
     * @throws IOException Throws IOException.
     */
    Double getInvokeHTTPFrequency() throws IOException;
}
