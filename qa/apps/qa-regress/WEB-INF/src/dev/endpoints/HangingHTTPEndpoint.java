/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

package dev.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.messaging.endpoints.HTTPEndpoint;

/**
 *  HTTPEndpoint varient for connect timeout and request timeout testing. 
 *  This endpoint sleeps for 3 seconds before servicing each inbound message.
 */
public class HangingHTTPEndpoint extends HTTPEndpoint
{
    public HangingHTTPEndpoint()
    {
        super();
    }
    
    public HangingHTTPEndpoint(boolean enableManagement)
    {
        super(enableManagement);
    }
    
    public void service(HttpServletRequest req, HttpServletResponse res)
    {
        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException ie)
        {
            // Ignore.
        }
        super.service(req, res);
    }
}
