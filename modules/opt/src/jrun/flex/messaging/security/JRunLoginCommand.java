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

import flex.messaging.FlexContext;
import flex.messaging.util.PropertyStringResourceLoader;
import jrun.security.JRunSecurityException;
import jrun.servlet.WebApplication;
import jrun.servlet.security.AuthenticatedUser;
import jrun.servlet.security.ServletUsers;
import jrun.servlet.security.WebAppSecurityService;
import jrunx.kernel.JRun;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A JRun 4 specific implementation of LoginCommand to manually authenticate
 * a user with the current web-app container.
 * <p>
 * The JRun WebAppSecurityService is located for the current web-app's
 * ServletEngineService JMX MBean.
 * </p>
 * <p>If authenticated, the login command can also check if the user is authorized
 * in a given list of roles.</p>
 */
public class JRunLoginCommand extends AppServerLoginCommand
{
    private WebAppSecurityService appSecurity;
    private ServletUsers usersService;
    // Exception message key numbers.

    /**
     * JRun couldn't authenticate the user due to an internal error message number.
     */
    private static final int AUTH_FAILED = 10060;
    /**
     * JRun couldn't authenticate due to missing security manager message number.
     */
    private static final int SEC_MANAGER_REQ_FOR_AUTH = 10061;
    /**
     * Cannot locate JRun's security manager message number.
     */
    private static final int NO_SEC_MANAGER = 20050;
    /**
     * Cannot access JRun's security service or servlet users message number.
     */
    private static final int NO_SEC_SRVC_OR_USERS = 20051;

    /** {@inheritDoc} */
    public void start(ServletConfig servletConfig)
    {
        //
        // CHECK JRUN VERSION
        //
        try
        {
            Class.forName("jrun.servlet.security.AuthenticatedUser");
            Class.forName("jrun.servlet.security.WebAppSecurityService");
            Class.forName("jrun.servlet.JRunServletContext");
        }
        catch (Throwable t)
        {
            SecurityException se =
                new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
            se.setMessage(NO_SEC_MANAGER);
            throw se;
        }

        if (JRun.server != null)
        {
            try
            {
                ServletContext servletContext = servletConfig.getServletContext();
                String webAppName = ((jrun.servlet.JRunServletContext)servletContext).getWebApplication().getName();

                Set serviceMBeans = null;
                ObjectInstance mbean = null;

                //
                // LOCATE WEB APP SECURITY SERVICE
                //

                ObjectName serviceName = new ObjectName("ServletEngineService." + webAppName + ":service=WebAppSecurityService");
                serviceMBeans = JRun.server.queryMBeans(serviceName, null);

                if (serviceMBeans != null)
                {
                    // Just grab the first object instance
                    Iterator iterator = serviceMBeans.iterator();
                    if (iterator.hasNext())
                    {
                        mbean = (ObjectInstance)iterator.next();
                    }
                }

                if (mbean != null)
                {
                    Object securityService = JRun.server.invoke(mbean.getObjectName(), "getWebAppSecurity", null, null);

                    if (securityService instanceof WebAppSecurityService)
                    {
                        appSecurity = (WebAppSecurityService)securityService;
                    }
                }

                // LOCATE SERVLET USERS SERVICE
                serviceName = new ObjectName("ServletEngineService:service=ServletUsersService");
                serviceMBeans = JRun.server.queryMBeans(serviceName, null);

                if (serviceMBeans != null)
                {
                    // Just grab the first object instance
                    Iterator iterator = serviceMBeans.iterator();
                    if (iterator.hasNext())
                    {
                        mbean = (ObjectInstance)iterator.next();
                    }
                }

                if (mbean != null)
                {
                    Object usersService = JRun.server.invoke(mbean.getObjectName(), "getServletUsers", null, null);

                    if (usersService instanceof ServletUsers)
                    {
                        this.usersService = (ServletUsers)usersService;
                    }
                }
            }
            catch (MalformedObjectNameException ex)
            {
                SecurityException se =
                    new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
                se.setMessage(NO_SEC_SRVC_OR_USERS);
                se.setRootCause(ex);
                throw se;
            }
            catch (InstanceNotFoundException ex)
            {
                SecurityException se =
                    new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
                se.setMessage(NO_SEC_SRVC_OR_USERS);
                se.setRootCause(ex);
                throw se;
            }
            catch (MBeanException ex)
            {
                Exception e = ex.getTargetException();
                SecurityException se =
                    new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
                se.setMessage(NO_SEC_SRVC_OR_USERS);
                se.setRootCause(e);
                throw se;
            }
            catch (ReflectionException ex)
            {
                SecurityException se =
                    new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
                se.setMessage(NO_SEC_SRVC_OR_USERS);
                se.setRootCause(ex);
                throw se;
            }
            catch (NullPointerException npe)
            {
                SecurityException se =
                    new SecurityException(new PropertyStringResourceLoader(PropertyStringResourceLoader.VENDORS_BUNDLE));
                se.setMessage(NO_SEC_SRVC_OR_USERS);
                se.setRootCause(npe);
                throw se;
            }
        }
    }

    /** {@inheritDoc} */
    public Principal doAuthentication(String username, Object credentials) throws SecurityException
    {
        if (appSecurity == null)
        {
            start(FlexContext.getServletConfig());
        }

        try
        {
            AuthenticatedUser user = null;
            String password = extractPassword(credentials);

            if (password != null)
            {
                // Test for the presence of a response here (rather than request) because NIO 
                // endpoints require the alternate code path and they don't populate the response
                // in FlexContext.
                HttpServletResponse response = FlexContext.getHttpResponse();
                if (response != null)
                {
                    HttpServletRequest request = FlexContext.getHttpRequest();
                    user = appSecurity.authenticate(username, password, request.getSession(true));
                }
                else
                {
                    ServletContext servletContext = FlexContext.getServletConfig().getServletContext();
                    WebApplication webApp = ((jrun.servlet.JRunServletContext)servletContext).getWebApplication();
                    HttpSession tempSession = webApp.getSessionService().createSession("temp");
                    user = appSecurity.authenticate(username, password, tempSession);
                    tempSession.invalidate();
                }
                return user.getPrincipal();
            }
        }
        catch (Exception ex)
        {
            SecurityException se;

            if (ex instanceof SecurityException)
            {
                se = (SecurityException)ex;
            }
            else if (ex instanceof MBeanException)
            {
                ex = ((MBeanException)ex).getTargetException();
                se = new SecurityException();
                se.setMessage(AUTH_FAILED);
                se.setRootCause(ex);
            }
            else if (ex instanceof JRunSecurityException)
            {
                se = new SecurityException();
                se.setMessage(AUTH_FAILED);
                se.setRootCause(ex);
            }
            /* [Matt] we don't need this because RTMP always
             * re-authenticates so we shouldn't get the
             * TwoUsers problem anymore.  If we get rid of
             * the temp session invalidation above this may need
             * to come back
            else if (ex instanceof MissingResourceException)
            {
                //no idea why JRun can't find this resource to throw its
                //real error, but let's cover this case anyway
                se = new SecurityException();
                if (((MissingResourceException)ex).getKey().equals("ServletUsers.TwoUsers"))
                {
                    se.setMessage(CANNOT_REAUTH); //10054
                    se.setCode(SecurityException.CLIENT_AUTHENTICATION_CODE);
                }
                else
                {
                    se.setMessage(AUTH_FAILED);
                    se.setRootCause(ex);
                }
            }
            */
            else
            {
                se = new SecurityException();
                se.setMessage(SEC_MANAGER_REQ_FOR_AUTH);
            }

            se.setCode(SecurityException.CLIENT_AUTHENTICATION_CODE);
            throw se;
        }

        return null;
    }

    /** {@inheritDoc} */
    public boolean doAuthorization(Principal principal, List roles) throws SecurityException
    {
        if (appSecurity == null)
        {
            start(FlexContext.getServletConfig());
        }

        boolean authorized = false;
        
        // Test for the presence of a response here (rather than request) because NIO 
        // endpoints require the alternate code path and they don't populate the response
        // in FlexContext.
        HttpServletResponse response = FlexContext.getHttpResponse();
        if (response != null)
        {
            HttpServletRequest request = FlexContext.getHttpRequest();
            authorized = doAuthorization(principal, roles, request);
        }
        else
        {
            for (int i = 0; i < roles.size(); i++)
            {
                String role = (String)roles.get(i);
                if (appSecurity.isPrincipalInRole(principal, role))
                {
                    authorized = true;
                    break;
                }
            }
        }

        return authorized;
    }

    /** {@inheritDoc} */
    public boolean logout(Principal principal) throws SecurityException
    {
        HttpServletResponse response = FlexContext.getHttpResponse();
        if (response != null)
        {
            // Destroy the Principal maintained by the app server.
            try
            {
                ServletContext servletContext = FlexContext.getServletConfig().getServletContext();
                WebApplication webApp = ((jrun.servlet.JRunServletContext)servletContext).getWebApplication();
                HttpServletRequest request = FlexContext.getHttpRequest();
        
                HttpSession session = request.getSession();
                String sessionId = session.getId();
                AuthenticatedUser currentUser = usersService.getUser(sessionId, webApp);
                currentUser.logoff();
            }
            catch (NullPointerException npe)
            {
            }
        }
        // else, current non-servlet session will be automatically invalidated, destroying any active Principal.

        return true;
    }

    /** {@inheritDoc} */
    public void stop()
    {
        usersService = null;
        appSecurity = null;
    }

}
