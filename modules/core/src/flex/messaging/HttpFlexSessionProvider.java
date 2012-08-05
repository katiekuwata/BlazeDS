/*******************************************************************************
 *
 * Copyright 2008 Adobe Systems Incorporated. All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Adobe Systems Incorporated. The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems incorporated and may be covered by
 * U.S. and Foreign Patents, patents in process, and are protected by trade
 * secret or copyright law. Dissemination of this information or reproduction of
 * this material is strictly forbidden unless prior written permission is
 * obtained from Adobe Systems Incorporated.
 ******************************************************************************/
package flex.messaging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import flex.messaging.log.Log;

/**
 * Provider implementation for <code>HttpFlexSession</code>s.
 * Not intended for public use.
 */
public class HttpFlexSessionProvider extends AbstractFlexSessionProvider
{
    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Factory method to get an existing <tt>HttpFlexSession</tt> for the current request,
     * or create and return a new <code>HttpFlexSession</code> if necessary.
     * The <code>HttpFlexSession</code> wraps the underlying J2EE <code>HttpSession</code>.
     * Not intended for public use.
     * 
     * @param request The current <tt>HttpServletRequest</tt>.
     * @return A <tt>HttpFlexSession</tt>.
     */
    public HttpFlexSession getOrCreateSession(HttpServletRequest request)
    {
        HttpFlexSession flexSession;
        HttpSession httpSession = request.getSession(true);

        if (!HttpFlexSession.isHttpSessionListener && !HttpFlexSession.warnedNoEventRedispatch)
        {
            HttpFlexSession.warnedNoEventRedispatch = true;
            if (Log.isWarn())
                Log.getLogger(HttpFlexSession.WARN_LOG_CATEGORY).warn("HttpFlexSession has not been registered as a listener in web.xml for this application so no events will be dispatched to FlexSessionAttributeListeners or FlexSessionBindingListeners. To correct this, register flex.messaging.HttpFlexSession as a listener in web.xml.");
        }

        boolean isNew = false;
        synchronized (httpSession)
        {
            flexSession = (HttpFlexSession)httpSession.getAttribute(HttpFlexSession.SESSION_ATTRIBUTE);
            if (flexSession == null)
            {
                flexSession = new HttpFlexSession(this);
                // Correlate this FlexSession to the HttpSession before triggering any listeners.
                FlexContext.setThreadLocalSession(flexSession);
                httpSession.setAttribute(HttpFlexSession.SESSION_ATTRIBUTE, flexSession);
                flexSession.setHttpSession(httpSession);
                isNew = true;
            }
            else
            {
                FlexContext.setThreadLocalSession(flexSession);
                if (flexSession.httpSession == null)
                {
                    // httpSession is null if the instance is new or is from
                    // serialization.
                    flexSession.setHttpSession(httpSession);
                    isNew = true;
                }
            }
        }

        if (isNew)
        {
            getFlexSessionManager().registerFlexSession(flexSession);
            flexSession.notifyCreated();

            if (Log.isDebug())
                Log.getLogger(HttpFlexSession.FLEX_SESSION_LOG_CATEGORY).debug("FlexSession created with id '" + flexSession.getId() + "' for an Http-based client connection.");
        }

        return flexSession;
    }
    }
