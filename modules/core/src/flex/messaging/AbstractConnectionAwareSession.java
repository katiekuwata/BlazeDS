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

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @exclude
 * Abstract base class for <tt>ConnectionAwareSession</tt> implementations.
 * Provides support for registering <tt>FlexSessionConnectivityListener</tt>s
 * along with protected methods to notify registered listeners of <tt>FlexSessionConnectivityEvent</tt>s.
 */
public abstract class AbstractConnectionAwareSession extends FlexSession implements ConnectionAwareSession
{
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    /**
     * @exclude
     * Constructs a new instance.
     *
     * @param sessionProvider The provider that instantiated this instance.
     */
    public AbstractConnectionAwareSession(AbstractFlexSessionProvider sessionProvider)
    {
        super(sessionProvider);
    }
    
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  connected
    //----------------------------------
    
    /**
     * Connected flag for the session.
     */
    private boolean connected;

    /**
     * Returns whether the session is connected.
     * 
     * @return true if the session is connected; otherwise false.
     */
    public boolean isConnected()
    {
        synchronized (lock)
        {
            return connected;
        }
    }
    
    /**
     * Sets the connected state for the session.
     * 
     * @param value true for a connected session; false for a disconnected session.
     */
    public void setConnected(boolean value)
    {
        boolean notify = false;
        synchronized (lock)
        {
            if (connected != value)
            {
                connected = value;
                notify = true;
            }
        }
        if (notify)
        {
            if (!value)
                notifySessionDisconnected();
            else
                notifySessionConnected();
        }
    }
    
    //----------------------------------
    //  connectivityListeners
    //----------------------------------
    
    /**
     * The list of connectivity listeners for the session.
     */
    private volatile CopyOnWriteArrayList<FlexSessionConnectivityListener> connectivityListeners;
    
    /**
     * @see flex.messaging.ConnectionAwareSession#addConnectivityListener(FlexSessionConnectivityListener)
     */
    public void addConnectivityListener(FlexSessionConnectivityListener listener)
    {
        if (connectivityListeners == null)
        {
            synchronized (lock)
            {
                if (connectivityListeners == null)
                    connectivityListeners = new CopyOnWriteArrayList<FlexSessionConnectivityListener>(); 
            }
        }
        if (connectivityListeners.addIfAbsent(listener) && isConnected())
        {
            // If the listener is added when the session has already connected, notify it at add time.
            FlexSessionConnectivityEvent event = new FlexSessionConnectivityEvent(this);
            listener.sessionConnected(event);
        }
    }

    /**
     * @see flex.messaging.ConnectionAwareSession#removeConnectivityListener(FlexSessionConnectivityListener)
     */
    public void removeConnectivityListener(FlexSessionConnectivityListener listener)
    {
        if (connectivityListeners == null) return;
        connectivityListeners.remove(listener);
    }
    
    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------    
    
    /**
     * Notifies registered <tt>FlexSessionConnectivityListener</tt>s that the session has connected.
     */
    protected void notifySessionConnected()
    {
        if (connectivityListeners != null)
        {
            FlexSessionConnectivityEvent event = new FlexSessionConnectivityEvent(this);
            for (FlexSessionConnectivityListener listener : connectivityListeners)
                listener.sessionDisconnected(event);
        }
    }
    
    /**
     * Notifies registered <tt>FlexSessionConnectivityListener</tt>s that the session has disconnected.
     */
    protected void notifySessionDisconnected()
    {
        if (connectivityListeners != null)
        {
            FlexSessionConnectivityEvent event = new FlexSessionConnectivityEvent(this);
            for (FlexSessionConnectivityListener listener : connectivityListeners)
                listener.sessionDisconnected(event);            
        }
    }    
}