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
 * Sessions that directly track their connection state support notifying interested
 * listeners of connectivity changes.
 */
public interface ConnectionAwareSession
{
    //----------------------------------
    //  connected
    //----------------------------------

    /**
     * Returns true if the session is connected; otherwise false.
     * 
     * @return true if the session is connected; otherwise false.
     */
    boolean isConnected();
    
    /**
     * Sets the connected state for the session.
     * 
     * @param value true if the session is connected; false if disconnected.
     */
    void setConnected(boolean value);
    
    //----------------------------------
    //  connectivityListeners
    //----------------------------------
    
    /**
     * Registers a session connectivity listener with the session.
     * This listener will be notified when the session acquires or looses connectivity
     * to the remote host.
     * 
     * @param listener The <tt>FlexSessionConnectivityListener</tt> to register with the session.
     */
    void addConnectivityListener(FlexSessionConnectivityListener listener);
    
    /**
     * Unregisters a session connectivity listener from the session.
     * The unregistered listener will no longer be notified of session connectivity changes.
     * 
     * @param listener The <tt>FlexSessionConnectivityListener</tt> to unregister from the session.
     */
    void removeConnectivityListener(FlexSessionConnectivityListener listener);
}
