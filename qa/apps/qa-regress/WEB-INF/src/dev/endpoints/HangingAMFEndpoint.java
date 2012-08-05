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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.messaging.endpoints.AMFEndpoint;

/**
 *  AMFEndpoint varient for connect timeout and request timeout testing. 
 *  This endpoint sleeps for 3 seconds before servicing a messaging, and then
 *  rather than servicing the message returns a 404. This is used to test
 *  client side Channel connect timeout, and that subsequent connect faults
 *  after a client-side timeout do not generate additional spurious events.
 */
public class HangingAMFEndpoint extends AMFEndpoint
{
    public HangingAMFEndpoint()
    {
        super();
    }
    
    public HangingAMFEndpoint(boolean enableManagement)
    {
        super(enableManagement);        
    }    
    
    public void service(HttpServletRequest req, HttpServletResponse res)
    {
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException ie)
        {
            // Ignore.
        }
        
        // Switch behavior based on the query string.
        String queryString = req.getQueryString();
        if (queryString != null && queryString.indexOf("404") != -1)
        {
            try
            {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            catch (IOException ioe)
            {
                throw new RuntimeException("Failed to send 404.", ioe);
            }
        }
        else
            super.service(req, res);
    }    
}
