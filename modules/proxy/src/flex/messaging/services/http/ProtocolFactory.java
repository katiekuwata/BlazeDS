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

import org.apache.commons.httpclient.protocol.Protocol;

import flex.messaging.FlexConfigurable;

/**
 * Implementations of the ProtocolFactory interface allow the developer to
 * customize how the HTTP Proxy Service communicates with a 3rd party endpoint.
 * ProtocolFactory extends FlexConfigurable to allow for properties to be
 * provided directly in the services configuration.
 * <p>
 * An example of a custom protocol might be to provide client certificates
 * for two-way SSL authentication for a specific destination.
 * </p>
 * <p>
 * Implementations of this interface must provide a default, no-args
 * constructor.
 * </p>
 * 
 * @author Peter Farland
 */
public interface ProtocolFactory extends FlexConfigurable
{
    /**
     * Returns a custom implementation of Apache Commons
     * HTTPClient's Protocol interface.
     * 
     * @return An implementation of org.apache.commons.httpclient.protocol.Protocol.
     */
    Protocol getProtocol();
}
