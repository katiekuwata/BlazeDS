/*******************************************************************************
 *
 * Copyright 2009 Adobe Systems Incorporated. All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Adobe Systems Incorporated. The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems incorporated and may be covered by
 * U.S. and Foreign Patents, patents in process, and are protected by trade
 * secret or copyright law. Dissemination of this information or reproduction of
 * this material is strictly forbidden unless prior written permission is
 * obtained from Adobe Systems Incorporated.
 ******************************************************************************/
package flex.messaging.services;

import flex.messaging.FlexSession;
import flex.messaging.client.FlexClient;
import java.security.Principal;
import java.util.EventObject;

/**
 * An event that indicates a user has either logged in or logged out successfully.
 * The event object provides access to the <tt>AuthenticationService</tt> that handled the
 * login or logout, which is the event source, as well as the <tt>Principal</tt>, <tt>FlexSession</tt>,
 * and <tt>FlexClient</tt> for the user. Following a logout, these objects may have been invalidated
 * so exercise caution with accessing and using them.
 */
public class AuthenticationEvent extends EventObject
{
    private static final long serialVersionUID = 6002063582698638736L;

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs an <tt>AuthenticationEvent</tt>.
     * 
     * @param source The <tt>AuthenticationService</tt> dispatching this event.
     * @param username The username used to authenticate
     * @param credentials The password or secret used to authenticate.
     * @param principal The user's <tt>Principal</tt>.
     * @param flexSession The user's <tt>FlexSession</tt>.
     * @param flexClient The user's <tt>FlexClient</tt>.
     */
    public AuthenticationEvent(final AuthenticationService source, final String username, final Object credentials, final Principal principal, final FlexSession flexSession, final FlexClient flexClient)
    {
        super(source);
        this.username = username;
        this.credentials = credentials;
        this.principal = principal;
        this.flexSession = flexSession;
        this.flexClient = flexClient;
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  credentials
    //----------------------------------    
    
    private final Object credentials;
    
    /**
     * Returns the credentials used for authentication, <code>null</code> for logout events.
     * 
     * @return The credentials used for authentication, <code>null</code> for logout events.
     */
    public Object getCredentials()
    {
        return credentials;
    }
    
    //----------------------------------
    //  flexClient
    //----------------------------------

    private final FlexClient flexClient;
    
    /**
     * Returns the <tt>FlexClient</tt> associated with this event.
     * 
     * @return The <tt>FlexClient</tt> associated with this event.
     */
    public FlexClient getFlexClient()
    {
        return flexClient;
    }

    //----------------------------------
    //  flexSession
    //----------------------------------

    private final FlexSession flexSession;
    
    /**
     * Returns the <tt>FlexSession</tt> associated with this event.
     * 
     * @return The <tt>FlexSession</tt> associated with this event.
     */
    public FlexSession getFlexSession()
    {
        return flexSession;
    }    
    
    //----------------------------------
    //  principal
    //----------------------------------

    private final Principal principal;
    
    /**
     * Returns the <tt>Principal</tt> associated with this event.
     * 
     * @return The <tt>Principal</tt> associated with this event.
     */
    public Principal getPrincipal()
    {
        return principal;
    }
    
    //----------------------------------
    //  source
    //----------------------------------
    
    public AuthenticationService getSource()
    {
        return AuthenticationService.class.cast(super.getSource());
    }
    
    //----------------------------------
    //  username
    //----------------------------------
    
    private String username;
    
    /**
     * Returns the username for authentication, <code>null</code> for logout events.
     * 
     * @return The username for authentication, <code>null</code> for logout events.
     */
    public String getUsername()
    {
        return username;
    }
}
