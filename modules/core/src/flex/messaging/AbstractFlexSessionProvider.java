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

import flex.messaging.config.ConfigMap;

/**
 * @exclude
 * Base for FlexSessionProvider implementations.
 * Providers are protocol-specific factories for concrete FlexSession implementations.
 * They are registered with a FlexSessionManager, which acts as the central point of control
 * for tracking all active FlexSessions and for dispatching creation events to FlexSessionListeners.
 */
public abstract class AbstractFlexSessionProvider implements FlexComponent
{
    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Instance lock.
     */
    protected final Object lock = new Object();
    
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  flexSessionManager
    //----------------------------------

    private volatile FlexSessionManager flexSessionManager;
    
    /**
     * Returns the <tt>FlexSessionManager</tt> this provider is currently registered to.
     * 
     * @return The <tt>FlexSessionManager</tt> this provider is currently registered to.
     */
    public FlexSessionManager getFlexSessionManager()
    {
        return flexSessionManager;
    }
    
    /**
     * Sets the <tt>FlexSessionManager</tt> this provider is registered to.
     * 
     * @param value The <tt>FlexSessionManager</tt> this provider is registered to.
     */
    public void setFlexSessionManager(final FlexSessionManager value)
    {
        flexSessionManager = value;
    }
    
    //----------------------------------
    //  logCategory
    //----------------------------------

    private boolean started;    
    
    /**
     * Indicates whether the component is started and running.
     * 
     * @return <code>true</code> if the component has started; 
     *         otherwise <code>false</code>.
     */
    public boolean isStarted()
    {
        synchronized (lock)
        {
            return started;
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Initializes the component with configuration information.
     *
     * @param id The id of the component.
     * @param configMap The properties for configuring component.
     */
    public void initialize(final String id, final ConfigMap configMap)
    {
        // No-op.
    }
    
    /**
     * Removes a <tt>FlexSession</tt> created by this provider.
     * This callback is invoked by <tt>FlexSession</tt>s when they are invalidated.
     *
     * @param session The <tt>FlexSession</tt> being invalidated.
     */
    public void removeFlexSession(final FlexSession session)
    {
        FlexSessionManager manager = getFlexSessionManager();
        if (manager != null)
            manager.unregisterFlexSession(session);
    }
    
    /**
     * Invoked to start the component.
     * This base implementation changes the components state such that {@link #isStarted()} returns true.
     */
    public void start()
    {
        synchronized (lock)
        {
            started = true;
        }
    }
    
    /**
     * Invoked to stop the component.
     * This base implementation changes the components state such that {@link #isStarted()} returns false.
     */
    public void stop()
    {
        synchronized (lock)
        {
            started = false;
        }
    }
}
