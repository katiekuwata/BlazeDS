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

import java.util.EventObject;

/**
 * An event dispatched when the connection state for a session changes.
 */
public class FlexSessionConnectivityEvent extends EventObject
{
    /**
     * @exclude
     */
    private static final long serialVersionUID = 8622680412552475829L;

    /**
     * Constructs a new <tt>FlexSessionConnectivityEvent</tt> using the supplied source <tt>ConnectionAwareSession</tt>.
     * 
     * @param session The session whose connection state has changed.
     */
    public FlexSessionConnectivityEvent(ConnectionAwareSession session)
    {
        super(session);
    }
    
    /**
     * Returns the session whose connection state has changed.
     * 
     * @return The session whose connection state has changed.
     */
    public ConnectionAwareSession getFlexSession()
    {
        return (ConnectionAwareSession)getSource();
    }
}
