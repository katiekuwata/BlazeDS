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

import flex.messaging.services.http.httpclient.FlexGetMethod;
import flex.messaging.services.http.httpclient.FlexPostMethod;
import flex.messaging.util.Assert;
import flex.messaging.util.Trace;
import flex.messaging.MessageException;

/**
 * @exclude
 * Wraps filters with exception handling.
 */
public class ErrorFilter extends ProxyFilter
{
    /**
     * Invokes the filter with the context.
     * 
     * @param context The proxy context.
     */
    public void invoke(ProxyContext context)
    {
        try
        {
            if (next != null)
            {
                next.invoke(context);
            }
        }
        catch (MessageException ex)
        {
            throw ex;
        }
        catch (Throwable ex)
        {
            throw new MessageException(ex);
        }
        finally
        {
            try
            {
                if (context.getHttpMethod() != null)
                {

                    // we don't want to keep the connection open if authentication info was sent
                    if (context.hasAuthorization())
                    {
                        if (context.getHttpMethod() instanceof FlexGetMethod)
                        {
                            ((FlexGetMethod)context.getHttpMethod()).setConnectionForced(true);
                        }
                        else if (context.getHttpMethod() instanceof FlexPostMethod)
                        {
                            ((FlexPostMethod)context.getHttpMethod()).setConnectionForced(true);
                        }
                        else
                        {
                            Assert.testAssertion(false, "Should have custom Flex method: " + context.getHttpMethod().getClass());
                        }
                    }
                    context.getHttpMethod().releaseConnection();
                }
            }
            catch (Exception e)
            {
                if (Trace.error)
                    e.printStackTrace();
            }
        }
    }
}
