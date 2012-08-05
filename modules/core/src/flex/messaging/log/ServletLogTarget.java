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
package flex.messaging.log;

import javax.servlet.ServletContext;

/**
 * This is a log target which uses the servlet context in order to log
 * messages.
 */
public class ServletLogTarget extends LineFormattedTarget
{
    static ServletContext context;

    /**
     * This method must be called during startup to give this target a reference
     * to the ServletContext.
     * @param ctx the servlet context
     * @exclude
     */
    public static void setServletContext(ServletContext ctx)
    {
        context = ctx;
    }

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Default constructor.
     */
    public ServletLogTarget()
    {
        super();
    }

    boolean warned = false;

    /**
     * Log a message via the servlet context.
     * @param message the message to log.
     */
    @Override
    protected void internalLog(String message)
    {
        if (context == null)
        {
            if (!warned)
            {
                System.out.println("**** No servlet context set in ServletLogTarget - logging disabled.");
                warned = true;
            }
        }
        else
        {
          context.log(message);
        }
    }
}