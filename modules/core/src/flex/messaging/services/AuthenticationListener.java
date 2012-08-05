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

import java.util.EventListener;

/**
 * The listener interface for receiving <tt>AuthenticationEvent</tt>s.
 * <tt>AuthenticationListener</tt>s are registered with the <tt>AuthenticationService</tt>
 * and allow for custom post-processing of successful user login and logout events.
 */
public interface AuthenticationListener extends EventListener
{
    /**
     * Invoked after a user has successfully logged in.
     */
    void loginSucceeded(AuthenticationEvent event);
    
    /**
     * Invoked after a user has successfully logged out.
     */
    void logoutSucceeded(AuthenticationEvent event);
}
