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

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.util.ExceptionUtil;

import java.io.IOException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.Valve;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 * A Tomcat valve for allowing programmatic login.  This valve saves the container, something not available
 * normally to a servlet, and allows login to the current realm.  The pieces interacting with Tomcat are taken from
 * org.apache.catalina.authenticator.AuthenticatorBase.  It would be nice if we could just extend that class or
 * call some of its methods, but things aren't set up in that class in such a way that this is possible
 *
 * FIXME: doesn't support Tomcat's SingleSignOn idea.  This is a way to write custom valves that associate
 * the principal to different web apps or locations.  See AuthenticatorBase for details
 *
 * JAR NOTE: this class is not in flex-messaging.jar but rather flex-tomcat-server.jar
 *
 * @author Brian Deitte
 * @author Matt Chotin
 * @exclude
 */
public class TomcatValve extends ValveBase implements Lifecycle
{

    private static String AMF_MATCH = "/amfgateway";
    private static String GATEWAY_MATCH = "/flashgateway";
    private static String MESSAGEBROKER_MATCH = "/messagebroker";
    private static String CUSTOM_MATCH = System.getProperty("flex.tomcatValveMatch");

    public void addLifecycleListener(LifecycleListener listener)
    {
        // ignore
    }

    public LifecycleListener[] findLifecycleListeners()
    {
        // ignore
        return null;
    }

    public void removeLifecycleListener(LifecycleListener listener)
    {
        // ignore
    }

    public void start() throws LifecycleException
    {
        //rtmp may not go through invoke so we need to put at least one
        //TomcatLoginImpl in the holder
        TomcatLoginHolder.setLogin(new TomcatLoginImpl(getContainer(), null));
    }

    public void stop() throws LifecycleException
    {
        // ignore
    }

    public void invoke(Request request, Response response)
            throws IOException, ServletException
    {
        ServletRequest servRequest = request.getRequest();
        if (servRequest instanceof HttpServletRequest)
        {
            // we only set the TomcatLoginImpl for gateway paths

            HttpServletRequest hrequest = ((HttpServletRequest)servRequest);
            String path = hrequest.getServletPath();
            boolean match = false;
            if (path == null)
            {
                // We need to use a slighly-weaker uri match for 4.1
                String uri = hrequest.getRequestURI();
                match = (uri != null &&
                    (uri.indexOf(MESSAGEBROKER_MATCH) != -1 ||
                    uri.indexOf(AMF_MATCH) != -1 ||
                    uri.indexOf(GATEWAY_MATCH) != -1 ||
                    (CUSTOM_MATCH != null && uri.indexOf(CUSTOM_MATCH) != -1)));
            }
            else
            {
                 match = (path.startsWith(MESSAGEBROKER_MATCH) ||
                         path.startsWith(AMF_MATCH) ||
                         path.startsWith(GATEWAY_MATCH) ||
                         (CUSTOM_MATCH != null && path.startsWith(CUSTOM_MATCH)));
            }

            if (match)
            {
                TomcatLoginHolder.setLogin(new TomcatLoginImpl(getContainer(), request));

                // copy over user princicpal and auth type values, just like in AuthenticatorBase.invoke()
                Principal principal = hrequest.getUserPrincipal();
                if (principal == null) 
                {
                    Session session = getSession(request, false);
                    if (session != null) 
                    {
                        principal = session.getPrincipal();
                        if (principal != null) 
                        {
                            request.setAuthType(session.getAuthType());
                            request.setUserPrincipal(principal);
                        }
                    }
                }
            }
        }
        Valve next = getNext();
        if (next != null)
            next.invoke(request, response);
    }

    // from AuthenticatorBase.getSession()
    static Session getSession(Request request, boolean create) 
    {

        HttpServletRequest hreq =
            (HttpServletRequest) request.getRequest();

        HttpSession hses = hreq.getSession(create);

        if (hses == null)
            return (null);
        Manager manager = request.getContext().getManager();

        if (manager == null)
            return (null);
        else 
        {
            try 
            {
                return (manager.findSession(hses.getId()));
            } catch (IOException e) 
            {
                Log.getLogger(LogCategories.SECURITY).error("Error in TomcatValve getting session id " + hses.getId() + " : " + ExceptionUtil.toString(e));
                return (null);
            }
        }
    }

    class TomcatLoginImpl implements TomcatLogin
    {
        private Container container;
        private Request request;

        TomcatLoginImpl(Container container, Request request)
        {
            this.container = container;
            this.request = request;
        }

        // authenticate the user and associate with the current session.  This is taken
        // from AuthenticatorBase.register()
        public Principal login(String username, String password, HttpServletRequest servletRequest)
        {
            Realm realm = container.getRealm();
            if (realm == null)
                return null;
            Principal principal = realm.authenticate(username, password);

            if (principal != null) 
            {

                if (this.request != null && this.request.getRequest() == servletRequest)
                {
                    request.setAuthType("flexmessaging"); //was "flashgateway"
                    request.setUserPrincipal(principal);

                    Session session = getSession(request, true);

                    // Cache the authentication information in our session, if any
                    if (session != null) 
                    {
                        session.setAuthType("flexmessaging"); //was "flashgateway"
                        session.setPrincipal(principal);
                        if (username != null)
                            session.setNote(Constants.SESS_USERNAME_NOTE, username);
                        else
                            session.removeNote(Constants.SESS_USERNAME_NOTE);
                        if (password != null)
                            session.setNote(Constants.SESS_PASSWORD_NOTE, password);
                        else
                            session.removeNote(Constants.SESS_PASSWORD_NOTE);
                    }
                }
            }

            return principal;
        }

        public boolean authorize(Principal principal, List roles)
        {

            Realm realm = container.getRealm();
            Iterator iter = roles.iterator();
            while (iter.hasNext())
            {
                String role = (String)iter.next();
                if (realm.hasRole(principal, role))
                    return true;
            }
            return false;
        }

        public boolean logout(HttpServletRequest request)
        {
            if (this.request != null && this.request.getRequest() == request)
            {
                Session session = getSession(this.request, false);
                if (session != null)
                {
                    session.setPrincipal(null);
                    session.setAuthType(null);
                    session.removeNote(Constants.SESS_USERNAME_NOTE);
                    session.removeNote(Constants.SESS_PASSWORD_NOTE);
                }
                return true;
            }
            return false;
        }
    }

}
