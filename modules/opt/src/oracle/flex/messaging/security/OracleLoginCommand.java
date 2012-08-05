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

import oracle.security.jazn.JAZNConfig;
import oracle.security.jazn.callback.JAZNCallbackHandler;

import java.security.Principal;
import java.util.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;

/**
 * A Oracle specific implementation of LoginCommand to manually authenticate
 * a user with the current web-app container.
 */
public class OracleLoginCommand extends AppServerLoginCommand
{
    /** {@inheritDoc} */
    public Principal doAuthentication(String username, Object credentials)
        throws SecurityException
    {
        OracleUser user;
        try
        {
            CallbackHandler callbackHandler = new JAZNCallbackHandler
                (JAZNConfig.getJAZNConfig(), null, 
                 username, extractPassword(credentials));
            LoginContext context = new LoginContext
                ("oracle.security.jazn.oc4j.JAZNUserManager", callbackHandler);
            user = new OracleUser(context);
        }
        catch (LoginException loginException)
        {
            throw wrapLoginException(loginException);
        }
        return user;
    }

    /** {@inheritDoc} */
    public boolean doAuthorization(Principal principal, List roles) 
        throws SecurityException
    {
        boolean result = false;
        if (principal instanceof OracleUser)
        {
            OracleUser user = (OracleUser) principal;
            result = user.isMemberOf(roles);
        }        
        return result;
    }

    /** {@inheritDoc} */
    public boolean logout(Principal principal) throws SecurityException
    {
        boolean result = false;
        if (principal instanceof OracleUser)
        {
            OracleUser user = (OracleUser) principal;
            try
            {
                user.logout();
                result = true;
            }
            catch (LoginException loginException)
            {
                throw wrapLoginException(loginException);
            }
        }
        return result;
    }

    private SecurityException wrapLoginException(LoginException exception)
    {
        SecurityException result = new SecurityException();
        result.setRootCause(exception);
        return result;
    }
}
