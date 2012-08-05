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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.messaging.FlexContext;
import flex.messaging.util.PropertyStringResourceLoader;

/**
 * A Tomcat specific implementation of LoginCommand.
 *
 * @author Brian Deitte
 * @author Matt Chotin
 */
public class TomcatLoginCommand extends AppServerLoginCommand
{
    private static final int NO_VALVE = 20000;

    /** {@inheritDoc} */
    public Principal doAuthentication(String username, Object credentials)
        throws SecurityException
    {
        TomcatLogin login = TomcatLoginHolder.getLogin();
        if (login == null)
        {
            SecurityException se =
                new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
            se.setMessage(NO_VALVE);
            throw se;
        }

        String password = extractPassword(credentials);
        if (password != null)
        {
            HttpServletRequest request = (HttpServletRequest)FlexContext.getHttpRequest();
            return login.login(username, password, request);
        }

        return null;
    }

    /** {@inheritDoc} */
    public boolean doAuthorization(Principal principal, List roles)
        throws SecurityException
    {
        boolean authorized = false;

        HttpServletRequest request = FlexContext.getHttpRequest();
        HttpServletResponse response = FlexContext.getHttpResponse(); // Response is null for NIO endpoints.
        
        if (response != null && request != null &&
            principal != null && principal.equals(request.getUserPrincipal()))
        {
            authorized = doAuthorization(principal, roles, request);
        }
        else
        {
            TomcatLogin login = TomcatLoginHolder.getLogin();
            if (login == null)
            {
                SecurityException se =
                    new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
                se.setMessage(NO_VALVE);
                throw se;
            }
            authorized = login.authorize(principal, roles);
        }

        return authorized;
    }

    /** {@inheritDoc} */
    public boolean logout(Principal principal) throws SecurityException
    {
        HttpServletRequest request = FlexContext.getHttpRequest();
        HttpServletResponse response = FlexContext.getHttpResponse(); // Response is null for NIO endpoints.
        if (response != null && request != null)
        {
            TomcatLogin login = TomcatLoginHolder.getLogin();
            if (login != null)
            {
                return login.logout(request);
            }
            else
            {
                //TODO should we do this?
                //request.getSession(false).invalidate();
            }
        }
        return true;
    }

}
