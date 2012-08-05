/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging;

import flex.messaging.client.FlexClient;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.io.TypeMarshallingContext;
import flex.messaging.security.LoginManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * The <tt>FlexContext</tt> is a utility class that exposes the current execution context.
 * It provides access to <tt>FlexSession</tt> and <tt>FlexClient</tt> instances associated
 * with the current message being processed, as well as global context via the <tt>MessageBroker</tt>,
 * <tt>ServletContext</tt> and <tt>ServletConfig</tt> for the application.
 * Any, or all, of the properties exposed by this class may be <code>null</code> depending upon
 * the current execution context so test for that before attempting to interact with them.
 */
public class FlexContext
{
    private static ThreadLocal<FlexClient> flexClients = new ThreadLocal<FlexClient>();
    private static ThreadLocal<FlexSession> sessions = new ThreadLocal<FlexSession>();
    private static ThreadLocal<MessageBroker> messageBrokers = new ThreadLocal<MessageBroker>();
    private static ThreadLocal<Endpoint> endpoints = new ThreadLocal<Endpoint>();
    private static ThreadLocal<HttpServletResponse> responses = new ThreadLocal<HttpServletResponse>();
    private static ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();
    private static ThreadLocal<HttpServletRequest> tunnelRequests = new ThreadLocal<HttpServletRequest>();
    private static ThreadLocal<ServletConfig> servletConfigs = new ThreadLocal<ServletConfig>();
    private static ThreadLocal<Boolean> messageFromPeer = new ThreadLocal<Boolean>();
    private static ThreadLocal<MessageRoutedNotifier> messageRoutedNotifiers = new ThreadLocal<MessageRoutedNotifier>();

    private FlexContext()
    {
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalObjects(FlexClient flexClient,
                                             FlexSession session,
                                             MessageBroker broker,
                                             HttpServletRequest request,
                                             HttpServletResponse response,
                                             ServletConfig servletConfig)
    {
        if (flexClients == null) // In case releaseThreadLocalObjects has been called.
            return;

        flexClients.set(flexClient);
        sessions.set(session);
        messageBrokers.set(broker);
        requests.set(request);
        responses.set(response);
        servletConfigs.set(servletConfig);
        messageFromPeer.set(Boolean.FALSE);
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalObjects(FlexClient flexClient, FlexSession session, MessageBroker broker)
    {
        setThreadLocalObjects(flexClient, session, broker, null, null, null);
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void clearThreadLocalObjects()
    {
        if (flexClients == null) // In case releaseThreadLocalObjects has been called.
            return;

        // first clear thread locals on the broker
        MessageBroker mb = messageBrokers.get();
        if (mb != null)
            mb.clearSystemSettingsThreadLocal();

        flexClients.remove();
        sessions.remove();
        messageBrokers.remove();
        endpoints.remove();
        requests.remove();
        responses.remove();
        tunnelRequests.remove();
        servletConfigs.remove();
        messageFromPeer.remove();
        messageRoutedNotifiers.remove();

        TypeMarshallingContext.clearThreadLocalObjects();
    }

    /**
     * The HttpServletResponse for the current request if the request is via HTTP.
     * Returns null if the client is using a non-HTTP channel.
     * Available for users.
     */
    public static HttpServletRequest getHttpRequest()
    {
        return requests != null? requests.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalHttpRequest(HttpServletRequest value)
    {
        if (requests == null)
            return;

        if (value == null)
            requests.remove();
        else
            requests.set(value);
    }

    /**
     * The HttpServletResponse for the current request if the request is via HTTP.
     * Returns null if the using an non-HTTP channel.
     * Available for users.
     */
    public static HttpServletResponse getHttpResponse()
    {
        return responses != null? responses.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalHttpResponse(HttpServletResponse value)
    {
        if (responses == null)
            return;

        if (value == null)
            responses.remove();
        else
            responses.set(value);
    }

    /**
     * The HttpServletRequest for the current request if it is transporting a tunneled protocol.
     * Returns null if the current request protocol it not tunneled.
     * Available for users.
     */
    public static HttpServletRequest getTunnelHttpRequest()
    {
        return tunnelRequests != null? tunnelRequests.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalTunnelHttpRequest(HttpServletRequest value)
    {
        if (tunnelRequests == null)
            return;

        if (value == null)
            tunnelRequests.remove();
        else
            tunnelRequests.set(value);
    }

    /**
     * The ServletConfig for the current request, uses the last known ServletConfig
     * when the request is not via HTTP.  Available for users.
     */
    public static ServletConfig getServletConfig()
    {
        return servletConfigs != null? servletConfigs.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalServletConfig(ServletConfig value)
    {
        if (servletConfigs == null)
            return;

        if (value == null)
            servletConfigs.remove();
        else
            servletConfigs.set(value);
    }

    /**
     * The ServletContext for the current web application.
     */
    public static ServletContext getServletContext()
    {
        return getServletConfig() != null? getServletConfig().getServletContext() : null;
    }

    /**
     * The FlexClient for the current request. Available for users.
     */
    public static FlexClient getFlexClient()
    {
        return flexClients != null? flexClients.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalFlexClient(FlexClient flexClient)
    {
        if (flexClients == null)
            return;

        if (flexClient == null)
            flexClients.remove();
        else
            flexClients.set(flexClient);
    }

    /**
     * The FlexSession for the current request.  Available for users.
     */
    public static FlexSession getFlexSession()
    {
        return sessions != null? sessions.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalSession(FlexSession session)
    {
        if (sessions == null)
            return;

        if (session == null)
            sessions.remove();
        else
            sessions.set(session);
    }

    /**
     * The MessageBroker for the current request.  Not available for users.
     *
     * @exclude
     */
    public static MessageBroker getMessageBroker()
    {
        return messageBrokers != null? messageBrokers.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalMessageBroker(MessageBroker value)
    {
        // This is a special case because MessageBroker is sometimes accessed by
        // services, destinations, adapters during shutdown so it needs to be set
        // on the context even if a previous MessageBrokerServlet#destroy called
        // releaseThreadLocalObjects.
        if (messageBrokers == null)
            messageBrokers = new ThreadLocal<MessageBroker>();

        if (value == null)
            messageBrokers.remove();
        else
            messageBrokers.set(value);
    }

    /**
     * The Endpoint for the current message. Not available for users.
     * @exclude
     */
    public static Endpoint getEndpoint()
    {
        return endpoints != null? endpoints.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setThreadLocalEndpoint(Endpoint value)
    {
        if (endpoints == null)
            return;

        if (value == null)
            endpoints.remove();
        else
            endpoints.set(value);
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static MessageRoutedNotifier getMessageRoutedNotifier()
    {
        return messageRoutedNotifiers != null? messageRoutedNotifiers.get() : null;
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static void setMessageRoutedNotifier(MessageRoutedNotifier value)
    {
        if (messageRoutedNotifiers == null)
            return;

        if (value == null)
            messageRoutedNotifiers.remove();
        else
            messageRoutedNotifiers.set(value);
    }

    /**
     * Indicates whether the current message being processed came from a server peer
     * in a cluster.
     */
    public static boolean isMessageFromPeer()
    {
        return messageFromPeer != null? messageFromPeer.get() : null;
    }

    /**
     * Sets a thread local indicating whether the message being processed came from
     * a server peer in a cluster.
     *
     * @param value True if the message came from a peer; otherwise false.
     *
     * @exclude
     */
    public static void setMessageFromPeer(boolean value)
    {
        if (messageFromPeer == null)
            return;

        messageFromPeer.set(value);
    }

    /**
     * Users should not call this.
     * @exclude
     */
    public static boolean isPerClientAuthentication()
    {
        MessageBroker messageBroker = getMessageBroker();
        if (messageBroker == null)
            return false;

        LoginManager loginManager = messageBroker.getLoginManager();
        return loginManager == null? false : loginManager.isPerClientAuthentication();
    }

    /**
     * Returns the principal associated with the session or client depending on whether
     * perClientauthentication is being used.  If the client has not
     * authenticated the principal will be null.
     *
     * @return The principal associated with the session.
     */
    public static Principal getUserPrincipal()
    {
        if (isPerClientAuthentication())
        {
            FlexClient client = getFlexClient();
            if (client != null)
                return client.getUserPrincipal();
        }
        else
        {
            FlexSession session = getFlexSession();
            if (session != null)
                return session.getUserPrincipal();
        }
        return null;
    }

    /**
     * Sets the Principal on either the current FlexClient or FlexSession depending upon whether
     * perClientAuthentication is in use.
     *
     * @param userPrincipal The principal to associate with the FlexClient or FlexSession
     * depending upon whether perClientAuthentication is in use.
     */
    public static void setUserPrincipal(Principal userPrincipal)
    {
        if (isPerClientAuthentication())
            getFlexClient().setUserPrincipal(userPrincipal);
        else
            getFlexSession().setUserPrincipal(userPrincipal);
    }

    /**
     * @exclude
     * Create the static thread local storage.
     */
    public static void createThreadLocalObjects()
    {
        if (flexClients == null) // Allocate if needed.
        {
            flexClients = new ThreadLocal<FlexClient>();
            sessions = new ThreadLocal<FlexSession>();
            messageBrokers = new ThreadLocal<MessageBroker>();
            endpoints = new ThreadLocal<Endpoint>();
            responses = new ThreadLocal<HttpServletResponse>();
            requests = new ThreadLocal<HttpServletRequest>();
            tunnelRequests = new ThreadLocal<HttpServletRequest>();
            servletConfigs = new ThreadLocal<ServletConfig>();
            messageFromPeer = new ThreadLocal<Boolean>();
            messageRoutedNotifiers = new ThreadLocal<MessageRoutedNotifier>();
        }
    }

    /**
     * @exclude
     * Destroy the static thread local storage.
     * Call ONLY on shutdown
     */
    public static void releaseThreadLocalObjects()
    {
        flexClients = null;
        sessions = null;
        messageBrokers = null;
        endpoints = null;
        responses = null;
        requests = null;
        tunnelRequests = null;
        servletConfigs = null;
        messageFromPeer = null;
        messageRoutedNotifiers = null;
    }
}
