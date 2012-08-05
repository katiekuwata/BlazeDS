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
package flex.messaging.security;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import flex.messaging.FlexContext;
import flex.messaging.io.MessageIOConstants;

/**
 * This class implements LoginCommand and doAuthorization in way that should work by default if
 * authorization logged a user into the J2EE application server.  doAuthorization uses isUserInRole.
 *
 * @exclude
 */
public abstract class AppServerLoginCommand implements LoginCommand
{

    /**
     * The gateway calls this method to perform programmatic authorization.
     * <p>
     * This implementation will simply iterate over the supplied roles and
     * check that at least one of the roles returned true from a call to
     * HttpServletRequest.isUserInRole(String role).
     * </p>
     *
     * @param principal The principal being checked for authorization
     * @param roles    A List of role names to check, all members should be strings
     * @return true if the principal belongs to at least one of the roles
     * @throws SecurityException Throws SecurityException
     */
    public boolean doAuthorization(Principal principal, List roles) throws SecurityException
    {
        HttpServletRequest request = FlexContext.getHttpRequest();
        return (request != null) ? doAuthorization(principal, roles, request) : false;
    }

    protected boolean doAuthorization(Principal principal, List roles, HttpServletRequest request)
        throws SecurityException
    {
        for (Object role : roles)
        {
            if (request.isUserInRole((String)role))
                return true;
        }

        return false;
    }

    protected String extractPassword(Object credentials)
    {
        if (credentials instanceof String)
            return (String)credentials;
        else if (credentials instanceof Map)
            return (String)((Map)credentials).get(MessageIOConstants.SECURITY_CREDENTIALS);
        return null;
    }

    /**
     * Called to initialize a login command prior to authentication/authorization requests.
     * The default implementation is no-op but subclasses can override to provide
     * their own implementation.
     *
     * @param config The servlet configuration for MessageBrokerServlet.
     */
    public void start(ServletConfig config)
    {
        // No-op.
    }

    /**
     * Called to free up resources used by the login command. The default implementation
     * is no-op, subclasses can override to provide their own implementation.
     */
    public void stop()
    {
        // No-op.
    }
}
