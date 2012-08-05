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

/**
 * The listener interface for receiving FlexSession connectivity events.
 */

public interface FlexSessionConnectivityListener
{
    /**
     * Invoked when the session has connected to the remote host.
     * 
     * @param event The <tt>FlexSessionConnectivityEvent</tt> for the connect event.
     */
    void sessionConnected(FlexSessionConnectivityEvent event);
    
    /**
     * Invoked when the session has disconnected from or lost connectivity to the remote host.
     * 
     * @param event The <tt>FlexSessionConnectivityEvent</tt> for the disconnect event.
     */
    void sessionDisconnected(FlexSessionConnectivityEvent event);
}
