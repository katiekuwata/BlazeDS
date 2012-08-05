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
package flex.messaging.client;

import flex.messaging.ConnectionAwareSession;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import flex.messaging.FlexSessionListener;
import flex.messaging.MessageClient;
import flex.messaging.MessageClientListener;
import flex.messaging.MessageException;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import flex.messaging.services.MessageService;
import flex.messaging.util.StringUtils;
import flex.messaging.util.TimeoutAbstractObject;
import flex.messaging.util.UUIDUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a Flex client application instance on the server.
 */
public class FlexClient extends TimeoutAbstractObject implements FlexSessionListener, MessageClientListener
{
    //--------------------------------------------------------------------------
    //
    // Public Static Constants
    //
    //--------------------------------------------------------------------------

    /**
     * Log category for FlexClient related messages.
     */
    public static final String FLEX_CLIENT_LOG_CATEGORY = LogCategories.CLIENT_FLEXCLIENT;

    /**
     * This value is passed to the server in an initial client connect to
     * indicate that the client needs a server-assigned FlexClient Id.
     * @exclude
     */
    public static final String NULL_FLEXCLIENT_ID = "nil";

    //--------------------------------------------------------------------------
    //
    // Private Static Constants
    //
    //--------------------------------------------------------------------------

    /**
     * Error string constants.
     */
    private static final int FLEX_CLIENT_INVALIDATED = 10027;
    private static final int ENDPOINT_PUSH_HANDLER_ALREADY_REGISTERED = 10033;

    private static final String POLL_WAIT_THREAD_NAME_EXTENSION = "-in-poll-wait";

    //--------------------------------------------------------------------------
    //
    // Private Static Variables
    //
    //--------------------------------------------------------------------------

    /**
     * List of registered FlexClient created listeners.
     */
    private static final CopyOnWriteArrayList<FlexClientListener> createdListeners = new CopyOnWriteArrayList<FlexClientListener>();

    //--------------------------------------------------------------------------
    //
    // Public Static Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Adds a create listener that will be notified when new FlexClients
     * are created.
     *
     * @see flex.messaging.client.FlexClientListener
     *
     * @param listener The listener to add.
     */
    public static void addClientCreatedListener(FlexClientListener listener)
    {
        if (listener != null)
            createdListeners.addIfAbsent(listener);
    }

    /**
     * Removes a FlexClient created listener.
     *
     * @see flex.messaging.client.FlexClientListener
     *
     * @param listener The listener to remove.
     */
    public static void removeClientCreatedListener(FlexClientListener listener)
    {
        if (listener != null)
            createdListeners.remove(listener);
    }

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * @exclude
     * Constructs a new FlexClient instance.
     *
     * @param manager The FlexClientManager managing this instance.
     */
    public FlexClient(FlexClientManager manager)
    {
        this(manager, UUIDUtils.createUUID());
    }

    /**
     * @exclude
     * Constructs a new FlexClient instance having the specified Id.
     *
     * @param manager The FlexClientManager managing this instance.
     * @param id The Id for this instance.
     */
    public FlexClient(FlexClientManager manager, String id)
    {
        this.id = id;
        flexClientManager = manager;
        updateLastUse();
        valid = true;

        if (Log.isDebug())
            Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).debug("FlexClient created with id '" + this.id + "'.");
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Storage for custom attributes.
     */
    private volatile Map<String, Object> attributes;

    /**
     * List of registered FlexClient attribute listeners.
     */
    private volatile CopyOnWriteArrayList<FlexClientAttributeListener> attributeListeners;

    /**
     * List of registered FlexClient destroyed listeners.
     */
    private volatile CopyOnWriteArrayList<FlexClientListener> destroyedListeners;

    /**
     * The manager for the FlexClient.
     */
    final FlexClientManager flexClientManager;

    /**
     * The unique Id for the instance.
     */
    private final String id;

    /**
     * Flag used to break cycles during invalidation.
     */
    /* package visibility for FlexClientManager */ volatile boolean invalidating;

    /**
     * Instance level lock to sync for state changes.
     */
    final Object lock = new Object();

    /**
     * MessageClient subscriptions for this MessageClient.
     */
    private volatile CopyOnWriteArrayList<MessageClient> messageClients;

    /**
     * Queues of outbound messages to push to the client keyed by endpoint id.
     * Map(String endpointId, EndpointQueue queue).
     */
    private final Map<String, EndpointQueue> outboundQueues = new ConcurrentHashMap<String, EndpointQueue>(1);

    /**
     * EndpointPushHandlers keyed by endpointId that the FlexClient
     * can use to push messages to remote clients.
     * NOTE: these can't be added to the EndpointQueue data type because the existence of queues depends
     * upon client subscription state whereas endpoints that support push will generally set up their push
     * handling before any subscriptions have been created.
     */
    private Map<String, EndpointPushHandler> endpointPushHandlers;

    /**
     * Associated FlexSessions that represent the connections the FlexClient makes to the server.
     */
    private final CopyOnWriteArrayList<FlexSession> sessions = new CopyOnWriteArrayList<FlexSession>(); // We always have at least one session.

    /**
     * Flag indicating whether the instance is valid; once invalidated this flag is
     * set to false.
     */
    boolean valid;

    /**
     * The principal associated with this client.  Only used when perClientAuthentication
     * is being used.
     */
    private Principal userPrincipal;

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Adds a FlexClient attribute listener that will be notified when an
     * attribute is added, removed or changed. If the attribute implements
     * FlexClientBindingListener, it will be notified before any
     * FlexClientAttributeListeners are notified.
     *
     * @param listener The listener to add.
     */
    public void addClientAttributeListener(FlexClientAttributeListener listener)
    {
        if (listener != null)
        {
            checkValid();

            if (attributeListeners == null)
            {
                synchronized (lock)
                {
                    if (attributeListeners == null)
                        attributeListeners = new CopyOnWriteArrayList<FlexClientAttributeListener>();
                }
            }

            attributeListeners.addIfAbsent(listener);
        }
    }

    /**
     * Adds a destroy listener that will be notified when the FlexClient
     * is destroyed. Listeners are notified after all attributes
     * have been unbound from the FlexClient and any FlexClientBindingListeners
     * and FlexClientAttributeListeners have been notified.
     *
     * @see flex.messaging.client.FlexClientListener
     *
     * @param listener The listener to add.
     */
    public void addClientDestroyedListener(FlexClientListener listener)
    {
        if (listener != null)
        {
            checkValid();

            if (destroyedListeners == null)
            {
                synchronized (lock)
                {
                    if (destroyedListeners == null)
                        destroyedListeners = new CopyOnWriteArrayList<FlexClientListener>();
                }
            }

            destroyedListeners.addIfAbsent(listener);
        }
    }

    /**
     * Returns the attribute bound to the specified name for the FlexClient, or null
     * if no attribute is bound under the name.
     *
     * @param name The name the attribute is bound to.
     * @return The attribute bound to the specified name.
     */
    public Object getAttribute(String name)
    {
        synchronized (lock)
        {
            checkValid();

            updateLastUse();

            return (attributes == null) ? null : attributes.get(name);
        }
    }

    /**
     * Returns a snapshot of the names of all attributes bound to the FlexClient.
     *
     * @return A snapshot of the names of all attributes bound to the FlexClient.
     */
    public Enumeration<String> getAttributeNames()
    {
        synchronized (lock)
        {
            checkValid();

            updateLastUse();

            if (attributes == null)
                return Collections.enumeration(Collections.<String>emptyList());

            // Return a copy so we do not run into concurrent modification problems if
            // someone adds to the attributes while iterating through the returned enumeration.
            return Collections.enumeration(new ArrayList<String>(attributes.keySet()));
        }
    }

    /**
     * @exclude
     * Returns the push handler registered with the FlexClient with the supplied
     * endpoint id, or null if no push handler was registered with the FlexClient
     * for that endpoint.
     *
     * @return The push handler registered with the FlexClient with the supplied
     * endpoint id, or null if no push handler was registered with the FlexClient
     * for that endpoint.
     */
    public EndpointPushHandler getEndpointPushHandler(String endpointId)
    {
        synchronized (lock)
        {
            if (endpointPushHandlers != null && endpointPushHandlers.containsKey(endpointId))
                return endpointPushHandlers.get(endpointId);
            return null;
        }
    }

    /**
     * @exclude
     * Returns the queue processor registered with the FlexClient with the supplied
     * endpoint id, or null if no queue processor was registered with the FlexClient
     * for that endpoint.
     *
     * @param endpointId The endpoint id.
     * @return The queue processor registered with the FlexClient.
     */
    public FlexClientOutboundQueueProcessor getOutboundQueueProcessor(String endpointId)
    {
        EndpointQueue queue = outboundQueues.get(endpointId);
        return (queue != null)? queue.processor : null;
    }

    /**
     * Override {@link flex.messaging.util.TimeoutAbstractObject#getLastUse()} to make timeout
     * dependent upon FlexClient inactivity but also upon the presence of an active push-enabled session,
     * async or waited poll, or registered endpoint push handler (all of which indicate that a client has
     * an active, open connection to the server).
     *
     * @return The 'last use' timestamp for the FlexClient, which may be the current system time if the FlexClient
     *         has been idle but an open connection from the client to the server exists.
     */
    @Override
    public long getLastUse()
    {
        synchronized (lock)
        {
            long currentLastUse = super.getLastUse();
            long idleTime = System.currentTimeMillis() - currentLastUse;
            if (idleTime < flexClientManager.getFlexClientTimeoutMillis())
                return currentLastUse; // Not timed out; this will trigger the timeout to be rescheduled.

            // Check for async long-polls or endpoint streaming connections, if found, keep alive.
            if (!outboundQueues.isEmpty())
            {
                for (EndpointQueue queue : outboundQueues.values())
                {
                    if (queue.asyncPoll != null)
                        return System.currentTimeMillis();

                    if (endpointPushHandlers != null && endpointPushHandlers.containsKey(queue.endpointId))
                        return System.currentTimeMillis();
                }
            }

            // Check for connected sessions, or a session holding a (non-async) long poll and if found, keep alive.
            for (FlexSession session : sessions)
            {
                if (session instanceof ConnectionAwareSession)
                {
                    if (((ConnectionAwareSession)session).isConnected())
                        return System.currentTimeMillis();
                }
                // Otherwise, check for a long-poll.
                if (session.waitMonitor != null)
                {
                    for (EndpointQueue queue : session.waitMonitor.values())
                    {
                        if (queue.flexClient.equals(this))
                            return System.currentTimeMillis();
                    }
                }
            }
            return currentLastUse; // Allow the FlexClient to timeout.
        }
    }

    /**
     * @exclude
     *
     * Returns the principal associated with this client.  If the client has not
     * authenticated the principal will be null.  Should only be called from FlexContext
     * and only if perClientAuthentication is used.  Not available to users.
     *
     * @return The principal associated with the session.
     */
    public Principal getUserPrincipal()
    {
        synchronized (lock)
        {
            checkValid();
            return userPrincipal;
        }
    }

    /**
     * @exclude
     *
     * Should only be called from FlexContext and only if perClientAuthentication is used.
     * Not available to users.
     *
     * @param userPrincipal The principal to associate with the session.
     */
    public void setUserPrincipal(Principal userPrincipal)
    {
        synchronized (lock)
        {
            checkValid();
            this.userPrincipal = userPrincipal;
        }
    }

    /**
     * Invalidates the FlexClient.
     */
    public void invalidate()
    {
        synchronized (lock)
        {
            if (!valid || invalidating)
                return; // Already shutting down.

            invalidating = true; // This thread gets to shut the FlexClient down.
            flexClientManager.removeFlexClient(this);
            cancelTimeout();
        }

        // Unregister from all FlexSessions.
        if (!sessions.isEmpty())
        {
            for (FlexSession session : sessions)
                unregisterFlexSession(session);
        }

        // Invalidate associated MessageClient subscriptions.
        if (messageClients != null && !messageClients.isEmpty())
        {
            for (MessageClient messageClient : messageClients)
            {
                messageClient.removeMessageClientDestroyedListener(this);
                messageClient.invalidate();
            }
            messageClients.clear();
        }

        // Notify destroy listeners that we're shutting the FlexClient down.
        if (destroyedListeners != null && !destroyedListeners.isEmpty())
        {
            for (FlexClientListener destroyListener : destroyedListeners)
            {
                destroyListener.clientDestroyed(this);
            }
            destroyedListeners.clear();
        }

        // Unbind all attributes.
        if (attributes != null && !attributes.isEmpty())
        {
            Set<String> keySet = attributes.keySet();
            String[] keys = keySet.toArray(new String[keySet.size()]);
            for (String key : keys)
                removeAttribute(key);
        }

        // Close any registered push handlers.
        if (endpointPushHandlers != null && !endpointPushHandlers.isEmpty())
        {
            for (EndpointPushHandler handler : endpointPushHandlers.values())
            {
                handler.close(true /* notify Channel of disconnect */);
            }
            endpointPushHandlers = null;
        }

        synchronized (lock)
        {
            valid = false;
            invalidating = false;
        }

        if (Log.isDebug())
            Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).debug("FlexClient with id '" + this.id + "' has been invalidated.");
    }

    /**
     * Returns true if the FlexClient is valid; false if it has been invalidated.
     *
     * @return true if the FlexClient is valid; otherwise false.
     */
    public boolean isValid()
    {
        synchronized (lock)
        {
            return valid;
        }
    }

    /**
     * Returns a snapshot of the FlexSessions associated with the FlexClient
     * when this method is invoked.
     * This list is not guaranteed to remain consistent with the actual list
     * of active FlexSessions associated with the FlexClient over time.
     *
     * @return A snapshot of the current list of FlexSessions associated with the FlexClient.
     */
    public List<FlexSession> getFlexSessions()
    {
        List<FlexSession> currentSessions;
        synchronized (lock)
        {
            checkValid();

            updateLastUse();

            currentSessions = new ArrayList<FlexSession>(sessions); // Make a copy of the current list to return.
        }
        return currentSessions;
    }

    /**
     * @return The number of sessions associated with this FlexClient.
     */
    public int getSessionCount()
    {
        int sessionCount;
        synchronized (lock)
        {
            sessionCount = (sessions != null) ? sessions.size() : 0; // Make a copy of the current list to return.
        }
        return sessionCount;
    }

    /**
     * @return The number of subscriptions associated with this FlexClient.
     */
    public int getSubscriptionCount()
    {
        int count = 0;
        synchronized (lock)
        {

            if (messageClients != null && !messageClients.isEmpty())
            {
                for (MessageClient messageClient : messageClients)
                    count += messageClient.getSubscriptionCount();
            }

        }
        return count;
    }

    /**
     * Returns the message client registered with the FlexClient with the supplied
     * client id, or null if no message client was registered with the FlexClient
     * with that client id.
     *
     * @param clientId The client id.
     * @return The message client registered with the FlexClient.
     */
    public MessageClient getMessageClient(String clientId)
    {
        synchronized (lock)
        {
            if (messageClients != null && !messageClients.isEmpty())
            {
                for (MessageClient messageClient : messageClients)
                {
                    if (messageClient.getClientId().equals(clientId))
                        return messageClient;
                }
            }
        }
        return null;
    }

    /**
     * Returns a snapshot of the MessageClients (subscriptions) associated with the FlexClient
     * when this method is invoked.
     * This list is not guaranteed to remain consistent with the actual list
     * of active MessageClients associated with the FlexClient over time.
     *
     * @return A snapshot of the current list of MessageClients associated with the FlexClient.
     */
    public List<MessageClient> getMessageClients()
    {
        List<MessageClient> currentMessageClients;
        synchronized (lock)
        {
            checkValid();

            updateLastUse();

            currentMessageClients = (messageClients != null) ? new ArrayList<MessageClient>(messageClients) // Make a copy of the current list to return.
                                                             : Collections.<MessageClient>emptyList(); // Return an empty list.
        }
        return currentMessageClients;
    }

    /**
     * Returns the unique Id for the FlexClient.
     *
     * @return The unique Id for the FlexClient.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @exclude
     * Implements TimeoutCapable.
     * Determine the time, in milliseconds, that this object is allowed to idle
     * before having its timeout method invoked.
     */
    @Override
    public long getTimeoutPeriod()
    {
        return flexClientManager.getFlexClientTimeoutMillis();
    }

    /**
     * @exclude
     * Implements MessageClientListener.
     * Handling created events is a no-op.
     *
     * @param messageClient The new MessageClient.
     */
    public void messageClientCreated(MessageClient messageClient) {}

    /**
     * @exclude
     * Implements MessageClientListener.
     * Notification that an associated FlexSession was destroyed.
     *
     * @param messageClient The MessageClient that was destroyed.
     */
    public void messageClientDestroyed(MessageClient messageClient)
    {
        unregisterMessageClient(messageClient);
    }

    /**
     * @exclude
     * Poll for outbound messages for the FlexClient.
     * This method is only invoked by internal code while processing a client poll request; it
     * is not intended for general public use.
     * Poll requests that trigger this method come from client-side polling channels and the request
     * is not specific to a single Consumer/MessageClient instance so process any queued messages for
     * the specified endpoint across all subscriptions.
     *
     * @param endpointId The Id of the endpoint that received the poll request.
     * @return The flush result including messages to return in the poll response and
     *         an optional wait time for the next poll/flush.
     */
    public FlushResult poll(String endpointId)
    {
        synchronized (lock)
        {
            checkValid();

            EndpointQueue queue = outboundQueues.get(endpointId);

            if (queue != null)
                return internalPoll(queue);

            // Otherwise, the client is not subscribed.
            throwNotSubscribedException(endpointId);
        }
        return null;
    }

    /**
     * @exclude
     * Poll for outbound messages for the FlexClient and if no messages are available
     * immediately, store a reference to the passed async handler and call back when messages arrive.
     *
     * @param endpointId The Id of the endpoint that received the poll request.
     * @param handler The handler to callback when messages arrive.
     * @param waitIntervalMillis The wait interval in milliseconds for the poll to wait for data to arrive
     *        before returning an empty poll response.
     *
     * @return A <tt>TimeoutAbstractObject</tt> representing the asynchronous poll, or <code>null</code>
     *         if the poll request was handled immediately because data was available to return.
     */
    public TimeoutAbstractObject pollAsync(String endpointId, AsyncPollHandler handler, long waitIntervalMillis)
    {
        EndpointQueue queue;
        TimeoutAbstractObject asyncPollTask = null;
        synchronized (lock)
        {
            checkValid();

            queue = outboundQueues.get(endpointId);

            // If the queue exists and is not empty, flush immediately.
            if (queue != null)
            {
                if (!queue.messages.isEmpty())
                {
                    handler.asyncPollComplete(internalFlush(queue));
                }
                else // Set up an async long-poll.
                {
                    // Avoid monopolizing user agent connections.
                    FlexSession session = FlexContext.getFlexSession();
                    synchronized (session)
                    {
                        if (session.asyncPollMap != null)
                        {
                            AsyncPollWithTimeout parkedPoll = session.asyncPollMap.get(endpointId);
                            if (parkedPoll != null)
                            {
                                // If the poll is from the same client for this endpoint, treat it as a no-op.
                                if (parkedPoll.getFlexClient().equals(this))
                                {
                                    PollFlushResult result = new PollFlushResult();
                                    result.setClientProcessingSuppressed(true);
                                    handler.asyncPollComplete(result);
                                }
                                else // If the poll is for a different client on the same session, swap their waits.
                                {
                                    PollFlushResult result = new PollFlushResult();
                                    result.setAvoidBusyPolling(true);
                                    completeAsyncPoll(parkedPoll, result);
                                }
                            }
                        }
                        AsyncPollWithTimeout asyncPoll = new AsyncPollWithTimeout(this, session, queue, handler, waitIntervalMillis, endpointId);
                        synchronized (session)
                        {
                            if (session.asyncPollMap == null)
                                session.asyncPollMap = new HashMap<String, AsyncPollWithTimeout>();
                            session.asyncPollMap.put(endpointId, asyncPoll);
                        }
                        queue.asyncPoll = asyncPoll;
                        asyncPollTask = asyncPoll;
                    }
                }
            }
            else
            {
                // The queue was null; let the client know that there are no active subscriptions.
                throwNotSubscribedException(endpointId);
            }
        }
        return asyncPollTask;
    }

    /**
     * @exclude
     * Poll for outbound messages for the FlexClient and if no messages are available
     * immediately, put processing into a wait state until messages arrive.
     * This method is only invoked by internal code while processing a client poll request; it
     * is not intended for general public use.
     * Poll requests that trigger this method come from client-side polling channels and the request
     * is not specific to a single Consumer/MessageClient instance so process any queued messages for
     * the specified endpoint across all subscriptions.
     *
     * @param endpointId The Id of the endpoint that received the poll request.
     * @param session The FlexSession associated with this waitable poll request.
     * @param listener The listener to notify before a wait begins and as soon as one completes.
     * @param waitIntervalMillis The maximum amount of time to wait for messages in milliseconds.
     * @return The flush result including messages to return in the poll response and
     *         an optional wait time for the next poll/flush.
     */
    public FlushResult pollWithWait(String endpointId, FlexSession session, PollWaitListener listener, long waitIntervalMillis)
    {
        EndpointQueue queue;
        synchronized (lock)
        {
            checkValid();

            queue = outboundQueues.get(endpointId);

            // If the queue exists and is not empty there's no reason to wait; flush immediately.
            if (queue != null)
            {
                FlushResult flushResult = internalPoll(queue);
                if (flushResult != null)
                    return flushResult;
            }
        }

        // The queue exists but it was empty; we can try to wait for messages.
        if (queue != null)
        {
            synchronized (session)
            {
                // Set up the waitMonitor on the session; this is a reference to the queue that the
                // current poll request targets and we use it as a wait/notify monitor.
                // This also lets us prevent busy polling cycles from a single client. If we already have a waited
                // poll request a subsequent poll request is treated as a no-op.
                if (session.waitMonitor != null)
                {
                    final EndpointQueue waitingQueue = session.waitMonitor.get(endpointId);
                    // If the poll is from the same client swf, and the same endpoint, treat it as a no-op poll.
                    if (waitingQueue != null && waitingQueue.flexClient.equals(this))
                    {
                        PollFlushResult result = new PollFlushResult();
                        result.setClientProcessingSuppressed(true);
                        return result;
                    }
                }
                else
                {
                    session.waitMonitor = new HashMap<String, EndpointQueue>();
                }

                // Set the waitMonitor for the session to the queue
                // for this poll request before releasing the lock.
                session.waitMonitor.put(endpointId, queue);
            }

            // Now that the session references the wait monitor this thread will use to wait we can enter
            // the wait state.
            // -1 wait-interval actually means wait until notified.
            waitIntervalMillis = (waitIntervalMillis == -1) ? 0 : waitIntervalMillis;
            String threadName = Thread.currentThread().getName();
            try
            {
                boolean didWait = false;
                boolean avoidBusyPolling = false;
                synchronized (queue)
                {
                    // If the message queue is still empty, wait for a message to be added before invoking flush.
                    if (queue.messages.isEmpty())
                    {
                        if (Log.isDebug())
                            Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).debug("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id +
                                "' is waiting for new messages to arrive.");

                        didWait = true;

                        // Tag thread name during the wait.
                        Thread currentThread = Thread.currentThread();
                        currentThread.setName(threadName + POLL_WAIT_THREAD_NAME_EXTENSION);

                        if (listener != null)
                            listener.waitStart(queue);
 
                        queue.waitPoll = true; // Mark the queue as waiting.

                        queue.wait(waitIntervalMillis);
                        
                        queue.waitPoll = false; // Unmark the queue as waiting.

                        // Reset thread name now that the wait is over.
                        currentThread.setName(threadName);

                        if (listener != null)
                            listener.waitEnd(queue);

                        if (queue.avoidBusyPolling)
                        {
                            avoidBusyPolling = true;
                            queue.avoidBusyPolling = false;
                        }
                    }
                }

                synchronized (session)
                {
                    if (session.waitMonitor != null)
                    {
                        session.waitMonitor.remove(endpointId);
                    }
                }

                if (Log.isDebug())
                {
                    if (didWait)
                        Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).debug("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id +
                            "' is done waiting for new messages to arrive and is flushing the outbound queue.");
                    else
                        Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).debug("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id +
                            "' didn't need to wait and is flushing the outbound queue.");
                }

                // We need to hold the FlexClient lock to invoke flush.
                FlushResult result;
                synchronized (lock)
                {
                    result = internalFlush(queue);
                }
                if (avoidBusyPolling)
                {
                    PollFlushResult swappedPollResult = new PollFlushResult();
                    if (result != null)
                    {
                        swappedPollResult.setMessages(result.getMessages());
                        swappedPollResult.setNextFlushWaitTimeMillis(result.getNextFlushWaitTimeMillis());
                    }
                    swappedPollResult.setAvoidBusyPolling(true);
                    result = swappedPollResult;
                }
                return result;
            }
            catch (InterruptedException e)
            {
                if (Log.isWarn())
                    Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).warn("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id +
                            "' could not finish waiting for new messages to arrive " +
                            "because it was interrupted: " + e.toString());
            }
        }
        else
        {
            // The queue was null; let the client know that there are no active subscriptions.
            throwNotSubscribedException(endpointId);
        }
        return null;
    }

    /**
     * @exclude
     * Poll for outbound messages for a specific MessageClient/Consumer.
     * This overload of poll() is only invoked when handling a Consumer.receive() request.
     *
     * @param client The specific MessageClient instance to poll for messages for.
     * @return The flush result including messages to return in the poll response.
     *         The nextFlushWaitTimeMillis value is always forced to a value of 0 because
     *         Consumer.receive() calls are driven by client code and this setting has no meaning.
     */
    public FlushResult poll(MessageClient client)
    {
        FlushResult flushResult = null;
        synchronized (lock)
        {
            checkValid();

            String endpointId = client.getEndpointId();
            EndpointQueue queue = outboundQueues.get(endpointId);
            if (queue != null)
            {
                try
                {
                    flushResult = internalFlush(queue, client);
                }
                catch (RuntimeException e)
                {
                    if (Log.isError())
                        Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).error("Failed to flush an outbound queue for MessageClient '" + client.getClientId() + "' for FlexClient '" + getId() + "'.", e);
                    throw e;
                }
                if (flushResult != null)
                    flushResult.setNextFlushWaitTimeMillis(0); // Force to 0.
            }
            else
            {
                throwNotSubscribedException(endpointId);
            }
        }
        return flushResult;
    }

    /**
     * @exclude
     * Push a message to the FlexClient.
     * The message is added to the outbound queue of messages for the client and
     * will be pushed if possible or retrieved via a client poll request.
     *
     * @param message The Message to push.
     * @param messageClient The MessageClient subscription that this message targets.
     */
    public void push(Message message, MessageClient messageClient)
    {
        synchronized (lock)
        {
            // If the FlexClient is not valid, skip further processing.
            if (!valid)
                return;

            updateLastUse();

            // Route this message to the proper per-endpoint outbound queue.
            EndpointQueue queue = outboundQueues.get(messageClient.getEndpointId());

            // This queue may be null if all corresponding subscriptions have been invalidated.
            // If the queue has been shut down, we don't need to try to deliver the message.
            boolean empty;
            if (queue != null)
            {
                synchronized (queue) // To protect the list during the add and allow for notification.
                {
                    // Let the processor add the message to the queue.
                    try
                    {
                        queue.processor.add(queue.messages, message);
                        empty = queue.messages.isEmpty();

                        if (Log.isDebug())
                            Log.getLogger(LogCategories.MESSAGE_GENERAL).debug(
                               "Queuing message: " + message.getMessageId() +
                               StringUtils.NEWLINE +
                               "  to send to MessageClient: " + messageClient.getClientId() +
                               StringUtils.NEWLINE +
                               "  for FlexClient: " + messageClient.getFlexClient().getId() +
                               StringUtils.NEWLINE +
                               "  via endpoint: " + queue.endpointId +
                               StringUtils.NEWLINE +
                               "  client outbound queue size: " + queue.messages.size());
                    }
                    catch (RuntimeException e)
                    {
                        if (Log.isError())
                            Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).error("Failed to add a message to an outbound queue for FlexClient '" + getId() + "'.", e);
                        throw e;
                    }
                    // And notify any threads that may be in a poll wait state.
                    if (!empty && queue.waitPoll)
                        queue.notifyAll();
                }

                if (!empty)
                {
                    if (queue.asyncPoll != null)
                    {
                        completeAsyncPoll(queue.asyncPoll, internalFlush(queue));
                    }
                    else if (!empty && queue.flushTask == null &&
                            (queue.pushSession != null || (endpointPushHandlers != null && endpointPushHandlers.containsKey(queue.endpointId))))
                    {
                        // If a delayed flush is not scheduled and we have a push-enabled session associated with the queue
                        // or a push-enabled endpoint, try a direct push to the client.
                        directFlush(queue);
                    }
                }
            }
        }
    }

    /**
     * @exclude
     * Registers an <tt>EndpointPushHandler</tt> for the specified endpoint to handle pushing messages
     * to remote clients.
     *
     * @param handler The <tt>EndpointPushHandler</tt> to register.
     * @param endpointId The endpoint to register for.
     */
    public void registerEndpointPushHandler(EndpointPushHandler handler, String endpointId)
    {
        synchronized (lock)
        {
            if (endpointPushHandlers == null)
                endpointPushHandlers = new HashMap<String, EndpointPushHandler>(1);

            if (endpointPushHandlers.containsKey(endpointId))
            {
                MessageException me = new MessageException();
                me.setMessage(ENDPOINT_PUSH_HANDLER_ALREADY_REGISTERED, new Object[] {getId(), endpointId});
                throw me;
            }

            endpointPushHandlers.put(endpointId, handler);
        }
    }

    /**
     * @exclude
     * Used internally to associate a FlexSession with this FlexClient.
     *
     * @param session The FlexSession to associate with this FlexClient.
     */
    public void registerFlexSession(FlexSession session)
    {
        if (sessions.addIfAbsent(session))
        {
            session.addSessionDestroyedListener(this);
            session.registerFlexClient(this);
        }
    }

    /**
     * @exclude
     * Used internally to associate a MessageClient with this FlexClient.
     *
     * @param messageClient The MessageClient to associate with this FlexClient.
     */
    public void registerMessageClient(MessageClient messageClient)
    {
        if (messageClients == null)
        {
            synchronized (lock)
            {
                if (messageClients == null)
                    messageClients = new CopyOnWriteArrayList<MessageClient>();
            }
        }

        if (messageClients.addIfAbsent(messageClient))
        {
            messageClient.addMessageClientDestroyedListener(this);
            String endpointId = messageClient.getEndpointId();
            // Manage the outbound queue this MessageClient's subscription(s) will use
            // and associate the MessageClient with an EndpointPushHandler if one exists for the
            // endpoint the subscription was made over; this allows the shut-down of a
            // push connection to invalidate any subscriptions that are using it.
            synchronized (lock)
            {
                getOrCreateEndpointQueueAndRegisterSubscription(messageClient, endpointId);
                if (endpointPushHandlers != null)
                {
                    EndpointPushHandler handler = endpointPushHandlers.get(endpointId);
                    if (handler != null)
                        handler.registerMessageClient(messageClient);
                }
            }
        }
    }

    /**
     * Removes the attribute bound to the specified name for the FlexClient.
     *
     * @param name The name of the attribute to remove.
     */
    public void removeAttribute(String name)
    {
        Object value; // Used for event dispatch after the attribute is removed.

        synchronized (lock)
        {
            checkValid();

            updateLastUse();

            value = (attributes != null) ? attributes.remove(name) : null;
        }

        // If no value was bound under this name it's a no-op.
        if (value == null)
            return;

        notifyAttributeUnbound(name, value);
        notifyAttributeRemoved(name, value);
    }

    /**
     * Removes a FlexClient attribute listener.
     *
     * @param listener The listener to remove.
     */
    public void removeClientAttributeListener(FlexClientAttributeListener listener)
    {
        // No need to check validity; removing a listener is always ok.
        if (listener != null && attributeListeners != null)
            attributeListeners.remove(listener);
    }

    /**
     * Removes a FlexClient destroyed listener.
     *
     * @see flex.messaging.client.FlexClientListener
     *
     * @param listener The listener to remove.
     */
    public void removeClientDestroyedListener(FlexClientListener listener)
    {
        // No need to check validity; removing a listener is always ok.
        if (listener != null && destroyedListeners != null)
            destroyedListeners.remove(listener);
    }

    /**
     * @exclude
     * Implements FlexSessionListener interface.
     * Notification that a FlexSession was created.
     * This is a no-op because the FlexClient is never added as a static FlexSession created listener
     * but this method is required by the interface. We only listen for the destroyed event from
     * associated FlexSessions.
     *
     * @param session The FlexSession that was created.
     */
    public void sessionCreated(FlexSession session) {}

    /**
     * @exclude
     * Implements FlexSessionListener interface.
     * Notification that an associated FlexSession was destroyed.
     *
     * @param session The FlexSession that was destroyed.
     */
    public void sessionDestroyed(FlexSession session)
    {
        unregisterFlexSession(session);
    }

    /**
     * Binds an attribute value for the FlexClient under the specified name.
     *
     * @param name The name to bind the attribute under.
     * @param value The value of the attribute.
     */
    public void setAttribute(String name, Object value)
    {
        // Null value set is the same as removeAttribute().
        if (value == null)
        {
            removeAttribute(name);
            return;
        }

        Object oldValue; // Used to determine which events to dispatch after the set is performed.

        // Only synchronize for the attribute mutation; event dispatch doesn't require it.
        synchronized (lock)
        {
            checkValid();

            updateLastUse();

            if (attributes == null)
                attributes = new HashMap<String, Object>();

            oldValue = attributes.put(name, value);
        }

        if (oldValue == null)
        {
            notifyAttributeBound(name, value);
            notifyAttributeAdded(name, value);
        }
        else
        {
            notifyAttributeUnbound(name, oldValue);
            notifyAttributeReplaced(name, oldValue);
            notifyAttributeBound(name, value);
        }
    }

    /**
     * @exclude
     * Implements TimeoutCapable.
     * Inform the object that it has timed out.
     */
    public void timeout()
    {
        invalidate();
    }

    /**
     * @exclude
     * Unregisters an <tt>EndpointPushHandler</tt> from the specified endpoint.
     *
     * @param handler The <tt>EndpointPushHandler</tt> to unregister.
     * @param endpointId The endpoint to unregister from.
     */
    public void unregisterEndpointPushHandler(EndpointPushHandler handler, String endpointId)
    {
        synchronized (lock)
        {
            if (endpointPushHandlers == null)
                return; // No-op.

            if (endpointPushHandlers.get(endpointId).equals(handler))
                endpointPushHandlers.remove(endpointId);
        }
    }

    /**
     * @exclude
     * Used internally to disassociate a FlexSession from this FlexClient.
     *
     * @param session The FlexSession to disassociate from this FlexClient.
     */
    public void unregisterFlexSession(FlexSession session)
    {
        if (sessions.remove(session))
        {
            session.removeSessionDestroyedListener(this);
            session.unregisterFlexClient(this);
            // Once all client sessions/connections terminate; shut down.
            if (sessions.isEmpty())
                invalidate();
        }
    }

    /**
     * @exclude
     * Used internally to disassociate a MessageClient (subscription) from a FlexClient.
     *
     * @param messageClient The MessageClient to disassociate from the FlexClient.
     */
    public void unregisterMessageClient(MessageClient messageClient)
    {
        if (messageClients != null && messageClients.remove(messageClient))
        {
            messageClient.removeMessageClientDestroyedListener(this);
            String endpointId = messageClient.getEndpointId();
            // Manage the outbound queue that this subscription uses.
            synchronized (lock)
            {
                EndpointQueue queue = outboundQueues.get(endpointId);
                if (queue != null)
                {
                    // Decrement the ref count of MessageClients using this queue.
                    queue.messageClientRefCount--;

                    // Unregister the message client from the outbound throttle
                    // manager (if one exists).
                    OutboundQueueThrottleManager tm = queue.processor.getOutboundQueueThrottleManager();
                    if (tm != null)
                        tm.unregisterAllSubscriptions(messageClient.getDestinationId());

                    // If we're not attempting to notify the remote client that this MessageClient has
                    // been invalidated, remove any associated messages from the queue.
                    if (!messageClient.isAttemptingInvalidationClientNotification())
                    {
                        Object messageClientId = messageClient.getClientId();
                        for (Iterator<Message> iter = queue.messages.iterator(); iter.hasNext(); )
                        {
                            Message message = iter.next();
                            if (message.getClientId().equals(messageClientId))
                                iter.remove();
                        }
                    }

                    // If no active subscriptions require the queue, clean it up if possible.
                    if (queue.messageClientRefCount == 0)
                    {
                        if (queue.messages.isEmpty() || messageClient.isClientChannelDisconnected())
                        {
                            if (queue.asyncPoll != null) // Close out async long-poll if one is registered.
                            {
                                FlushResult flushResult = internalFlush(queue);
                                // If the MessageClient isn't attempting client notification, override
                                // and do so in this case to suppress the next poll request from the remote client
                                // which will fail triggering an unnecessary channel disconnect on the client.
                                if (!messageClient.isAttemptingInvalidationClientNotification())
                                {
                                    CommandMessage msg = new CommandMessage();
                                    msg.setClientId(messageClient.getClientId());
                                    msg.setOperation(CommandMessage.SUBSCRIPTION_INVALIDATE_OPERATION);
                                    List<Message> messages = flushResult.getMessages();
                                    if (messages == null)
                                        messages = new ArrayList<Message>(1);
                                    messages.add(msg);
                                }
                                completeAsyncPoll(queue.asyncPoll, flushResult);
                            }

                            // Remove the empty, unused queue.
                            outboundQueues.remove(endpointId);
                        }
                        // Otherwise, the queue is being used by a polling client or contains messages
                        // that will be written by a delayed flush.
                        // Leave it in place. Once the next poll request or delayed flush occurs the
                        // queue will be cleaned up at that point. See internalFlush() and shutdownQueue().
                    }

                    // Make sure to notify any threads waiting on this queue that may be associated
                    // with the subscription that's gone away.
                    synchronized (queue)
                    {
                        queue.notifyAll();
                    }
                }
                // And if this subscription was associated with an endpoint push handler, unregister it.
                if (endpointPushHandlers != null)
                {
                    EndpointPushHandler handler = endpointPushHandlers.get(endpointId);
                    if (handler != null)
                        handler.unregisterMessageClient(messageClient);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Utility method that tests validity and throws an exception if the instance
     * has been invalidated.
     */
    protected void checkValid()
    {
        synchronized (lock)
        {
            if (!valid)
            {
                MessageException e = new MessageException();
                e.setMessage(FLEX_CLIENT_INVALIDATED);
                throw e;
            }
        }
    }
    
    /**
     * Invoked to clean up a timed out or closed async poll.
     *
     * @param asyncPoll The async poll to complete.
     * @param result The FlushResult for the poll response.
     */
    protected void completeAsyncPoll(AsyncPollWithTimeout asyncPoll, FlushResult result)
    {
        synchronized (lock)
        {
            asyncPoll.cancelTimeout();
            EndpointQueue queue = asyncPoll.getEndpointQueue();
            if (queue.asyncPoll.equals(asyncPoll))
                queue.asyncPoll = null;
            FlexSession session = asyncPoll.getFlexSession();
            synchronized (session)
            {
                if (session.asyncPollMap != null)
                    session.asyncPollMap.remove(asyncPoll.getEndpointId());
            }
            asyncPoll.getHandler().asyncPollComplete(result);
        }
    }

    /**
     * Invoked to flush queued outbound messages to a client directly using a session
     * that supports real-time push.
     * Called by push() or delayed flush tasks for push-enabled sessions/connections.
     */
    protected void directFlush(EndpointQueue queue)
    {
        synchronized (lock)
        {
            // No need to invoke flush if the FlexClient has been invalidated.
            if (!valid)
                return;

            // If this invocation is a callback from a flush task, null out the task ref on
            // the queue to allow a subsequent delayed flush to be scheduled.
            if (queue.flushTask != null)
                queue.flushTask = null;

            FlushResult flushResult = internalFlush(queue);
            if (flushResult == null) // If there's no flush result, return.
                return;

            // Pass any messages that are ready to flush off to the network layer.
            List<Message> messages = flushResult.getMessages();
            if (messages != null && !messages.isEmpty())
            {
                // Update last use because we're writing back to the client.
                updateLastUse();

                if (queue.pushSession != null)
                {
                    for (Message msg : messages)
                        queue.pushSession.push(msg);
                }
                else if (endpointPushHandlers != null)
                {
                    EndpointPushHandler handler = endpointPushHandlers.get(queue.endpointId);
                    handler.pushMessages(messages);
                }
            }

            // Schedule a delayed flush if necessary.
            int flushWaitTime = flushResult.getNextFlushWaitTimeMillis();
            if (flushWaitTime > 0) // Set up and schedule the delayed flush task.
                queue.flushTask = new FlexClientScheduledFlushForPush(queue, flushWaitTime);
        }
    }
    
    /**
     * Utility method to initialize an EndpointQueue (if necessary) and associate a subscription (MessageClient) with it.
     */
    protected EndpointQueue getOrCreateEndpointQueueAndRegisterSubscription(MessageClient messageClient, String endpointId)
    {
        EndpointQueue newQueue;
        if (!outboundQueues.containsKey(endpointId))
        {
            newQueue = new EndpointQueue();
            newQueue.flexClient = this;
            newQueue.endpointId = endpointId;
            newQueue.endpoint = flexClientManager.getMessageBroker().getEndpoint(endpointId);
            newQueue.messages = new ArrayList<Message>(); /* Default size of 10 is fine */
            FlexSession session = messageClient.getFlexSession();
            if (session.isPushSupported())
                newQueue.pushSession = session;
            newQueue.processor = flexClientManager.createOutboundQueueProcessor(this, endpointId);
            newQueue.messageClientRefCount = 1;

            outboundQueues.put(endpointId, newQueue);
        }
        else
        {
            newQueue = outboundQueues.get(endpointId);
            newQueue.messageClientRefCount++;
            // Resubscribes as a result of network connectivity issues may arrive over the same
            // endpoint but use a new session.
            FlexSession session = messageClient.getFlexSession();
            if (session.isPushSupported())
                newQueue.pushSession = session;
        }
        return newQueue;
    }    

    /**
     * Utility method to flush the outbound queue and log any problems.
     * Any exceptions are logged and then rethrown.
     */
    protected FlushResult internalFlush(EndpointQueue queue)
    {
        return internalFlush(queue, null);
    }
    
    /**
     * Utility method to flush the outbound queue and log any problems.
     * If a specific client is passed, we need to invoke a client-specific flush.
     * If the passed client is null, we do a general flush of the queue.
     * Any exceptions are logged and then rethrown.
     */
    protected FlushResult internalFlush(EndpointQueue queue, MessageClient client)
    {
        FlushResult flushResult;
        try
        {
            flushResult = (client == null) ? queue.processor.flush(queue.messages)
                                           : queue.processor.flush(client, queue.messages);
            shutdownQueue(queue);

            // Update the last-use timestamp if we're writing messages back to the client.
            List<Message> messages = (flushResult != null) ? flushResult.getMessages() : null;
            if (messages != null && !messages.isEmpty())
                updateLastUse();            
        }
        catch (RuntimeException e)
        {
            if (Log.isError())
                Log.getLogger(FLEX_CLIENT_LOG_CATEGORY).error("Failed to flush an outbound queue for FlexClient '" + getId() + "'.", e);
            throw e;
        }
        return flushResult;
    }    

    /**
     * Utility method to flush messages in response to a poll request with 
     * regular and wait poll.
     * 
     * @param queue The endpoint queue to flush messages for.
     * @return The flush result with messages, or null if there are no messages.
     */
    protected FlushResult internalPoll(EndpointQueue queue)
    {
        List<Message> allMessages = new ArrayList<Message>();

        // First, add the previously flushed messages.
        if (queue.flushedMessagesBetweenPolls != null && queue.flushedMessagesBetweenPolls.size() > 0)
        {
            allMessages.addAll(queue.flushedMessagesBetweenPolls);
            queue.flushedMessagesBetweenPolls.clear();
        }

        // Then, check for regularly queued messages. We call internalFlush
        // even if the queue is empty so the queue processor could know
        // about the incoming poll request regardless.
        FlushResult internalFlushResult = internalFlush(queue);
        List<Message> flushedMessages = internalFlushResult.getMessages();
        if (flushedMessages != null && !flushedMessages.isEmpty())
            allMessages.addAll(flushedMessages);

        // Schedule a delayed flush, if necessary.
        int flushWaitTime = internalFlushResult.getNextFlushWaitTimeMillis();
        if (flushWaitTime > 0)
            queue.flushTask = new FlexClientScheduledFlushForPoll(queue, flushWaitTime);

        if (allMessages.size() > 0) // Flush, if there are messages.
        {
            FlushResult flushResult = new FlushResult();
            flushResult.setMessages(allMessages);
            return flushResult;
        }
        return null;
    }

    /**
     * Notify attribute listeners that an attribute has been added.
     *
     * @param name The name of the attribute.
     *
     * @param value The new value of the attribute.
     */
    protected void notifyAttributeAdded(String name, Object value)
    {
        if (attributeListeners != null && !attributeListeners.isEmpty())
        {
            FlexClientBindingEvent event = new FlexClientBindingEvent(this, name, value);
            // CopyOnWriteArrayList is iteration-safe from ConcurrentModificationExceptions.
            for (FlexClientAttributeListener attribListener : attributeListeners)
                attribListener.attributeAdded(event);
        }
    }

    /**
     * Notify binding listener that it has been bound to the FlexClient.
     *
     * @param name The attribute name.
     *
     * @param value The attribute that has been bound.
     */
    protected void notifyAttributeBound(String name, Object value)
    {
        if ((value != null) && (value instanceof FlexClientBindingListener))
        {
            FlexClientBindingEvent bindingEvent = new FlexClientBindingEvent(this, name);
            ((FlexClientBindingListener)value).valueBound(bindingEvent);
        }
    }

    /**
     * Notify attribute listeners that an attribute has been removed.
     *
     * @param name The name of the attribute.
     *
     * @param value The previous value of the attribute.
     */
    protected void notifyAttributeRemoved(String name, Object value)
    {
        if (attributeListeners != null && !attributeListeners.isEmpty())
        {
            FlexClientBindingEvent event = new FlexClientBindingEvent(this, name, value);
            // CopyOnWriteArrayList is iteration-safe from ConcurrentModificationExceptions.
            for (FlexClientAttributeListener attribListener : attributeListeners)
                attribListener.attributeRemoved(event);
        }
    }

    /**
     * Notify attribute listeners that an attribute has been replaced.
     *
     * @param name The name of the attribute.
     *
     * @param value The previous value of the attribute.
     */
    protected void notifyAttributeReplaced(String name, Object value)
    {
        if (attributeListeners != null && !attributeListeners.isEmpty())
        {
            FlexClientBindingEvent event = new FlexClientBindingEvent(this, name, value);
            // CopyOnWriteArrayList is iteration-safe from ConcurrentModificationExceptions.
            for (FlexClientAttributeListener attribListener : attributeListeners)
                attribListener.attributeReplaced(event);
        }
    }

    /**
     * Notify binding listener that it has been unbound from the FlexClient.
     *
     * @param name The attribute name.
     *
     * @param value The attribute that has been unbound.
     */
    protected void notifyAttributeUnbound(String name, Object value)
    {
        if ((value != null) && (value instanceof FlexClientBindingListener))
        {
            FlexClientBindingEvent bindingEvent = new FlexClientBindingEvent(this, name);
            ((FlexClientBindingListener)value).valueUnbound(bindingEvent);
        }
    }    
    
    /**
     * Invoked by FlexClientManager after this new FlexClient has been constructed and
     * is fully configured.
     */
    protected void notifyCreated()
    {
        if (!createdListeners.isEmpty())
        {
            // CopyOnWriteArrayList is iteration-safe from ConcurrentModificationExceptions.
            for (FlexClientListener createListener : createdListeners)
                createListener.clientCreated(this);
        }
    }
    
    /**
     * Utility method used to shutdown endpoint queues accessed via polling channels
     * that have no more active subscriptions and no more pending outbound messages.
     *
     * @param queue The queue to potentially shutdown.
     * @return true if the queue was cleaned up/removed; otherwise false.
     */
    protected boolean shutdownQueue(EndpointQueue queue)
    {
        // If no more subscriptions are using the queue and it is empty, shut it down.
        if (queue.messageClientRefCount == 0 && queue.messages.isEmpty())
        {
            outboundQueues.remove(queue.endpointId);
            // Notify any threads waiting on this queue.
            synchronized (queue)
            {
                queue.notifyAll();
            }
            return true;
        }
        return false;
    }
    
    /**
     * Utility method to throw a not subscribed exception back to the client
     * if they issue a poll request to an endpoint that they haven't subscribed over.
     *
     * @param endpointId The endpoint Id.
     */
    protected void throwNotSubscribedException(String endpointId)
    {
        // Pre-3.1 versions of the client library did not handle URL session tokens properly
        // and may incorrectly issue a poll, after subscribing, that does not contain the proper
        // FlexClient id.
        // This scenario looks like a poll from a client that is not subscribed, but it is not,
        // and deserves a more useful error message.
        // We determine this by checking for an (orphaned) FlexClient instance associated with the
        // current session that has a subscription established through the target endpoint.
        List<FlexClient> flexClients = FlexContext.getFlexSession().getFlexClients();
        for (FlexClient otherClient : flexClients)
        {
            if (!otherClient.equals(this))
            {
                List<MessageClient> otherSubs = otherClient.getMessageClients();
                for (MessageClient otherSub : otherSubs)
                {
                    if (otherSub.getEndpointId().equals(endpointId))
                    {
                        // Throw not-subscribed exception with extra guidance.
                        FlexClientNotSubscribedException e = new FlexClientNotSubscribedException();
                        e.setMessage(10036, new Object[]{endpointId});
                        e.setCode(MessageService.NOT_SUBSCRIBED_CODE);
                        throw e;
                    }
                }
            }
        }

        // Throw general not-subscribed exception.
        FlexClientNotSubscribedException e = new FlexClientNotSubscribedException();
        e.setMessage(10028, new Object[]{endpointId});
        e.setCode(MessageService.NOT_SUBSCRIBED_CODE);
        throw e;
    }

    //--------------------------------------------------------------------------
    //
    // Inner Classes
    //
    //--------------------------------------------------------------------------

    /**
     * Helper class for handling async poll requests. This class allows the response for an async poll
     * to be delayed until data arrives to return to the client or the specified wait interval elapses.
     * Wait timeouts are monitored by the <tt>FlexClientManager</tt> which contains a <tt>TimeoutManager</tt>
     * instance that is started and stopped during application bootstrap and shutdown. Managing timeouts
     * locally or statically isn't a good option because they lack a useful shutdown hook that's necessary
     * in order to close down the timeout manager cleanly.
     */
    public class AsyncPollWithTimeout extends TimeoutAbstractObject
    {
        public AsyncPollWithTimeout(FlexClient flexClient, FlexSession session, EndpointQueue queue, AsyncPollHandler handler, long waitIntervalMillis, String endpointId)
        {
            this.flexClient = flexClient;
            this.session = session;
            this.queue = queue;
            this.handler = handler;
            setTimeoutPeriod(waitIntervalMillis);
            flexClientManager.monitorAsyncPollTimeout(this);
            this.endpointId = endpointId;
        }

        private final FlexClient flexClient;

        public FlexClient getFlexClient()
        {
            return flexClient;
        }

        private final FlexSession session;

        public FlexSession getFlexSession()
        {
            return session;
        }

        private final EndpointQueue queue;

        public EndpointQueue getEndpointQueue()
        {
            return queue;
        }

        private final AsyncPollHandler handler;

        public AsyncPollHandler getHandler()
        {
            return handler;
        }

        private final String endpointId;

        public String getEndpointId()
        {
            return endpointId;
        }

        public void timeout()
        {
            completeAsyncPoll(this, null /* nothing to return */);
        }
    }

    /**
     * Helper class to flush a FlexClient's outbound queue after a specified delay.
     * Delayed flushes are handled by the <tt>FlexClientManager</tt>
     * using <tt>TimeoutManager</tt>.
     */
    abstract class FlexClientScheduledFlush extends TimeoutAbstractObject
    {
        final EndpointQueue queue;

        public FlexClientScheduledFlush(EndpointQueue queue, long waitIntervalMillis)
        {
            this.queue = queue;
            setTimeoutPeriod(waitIntervalMillis);
            flexClientManager.monitorScheduledFlush(this);
        }

        abstract void performFlushTask();

        public void timeout()
        {
            FlexContext.setThreadLocalFlexClient(FlexClient.this);
            performFlushTask();
            FlexContext.setThreadLocalFlexClient(null);
        }
    }

    /**
     * Helper class for push channels to directly flush a FlexClient's outbound
     * queue after a specified delay.
     */
    class FlexClientScheduledFlushForPush extends FlexClientScheduledFlush
    {
        public FlexClientScheduledFlushForPush(EndpointQueue queue, long waitIntervalMillis)
        {
            super(queue, waitIntervalMillis);
        }

        @Override
        void performFlushTask()
        {
            directFlush(queue);
        }

    }

    /**
     * Helper class for polling channels to flush a FlexClient's outbound
     * queue to flushedMessagesBetweenPolls queue after a specified delay.
     * When the next poll happens, the flushedMessagesBetweenPolls will be
     * drained first.
     */
    class FlexClientScheduledFlushForPoll extends FlexClientScheduledFlush
    {
        public FlexClientScheduledFlushForPoll(EndpointQueue queue, long waitIntervalMillis)
        {
            super(queue, waitIntervalMillis);
        }

        @Override
        void performFlushTask()
        {
            synchronized (lock)
            {
                // No need to invoke flush if the FlexClient has been invalidated.
                if (!valid)
                    return;

                // If this invocation is a callback from a flush task, null out the task ref on
                // the queue to allow a subsequent delayed flush to be scheduled.
                if (queue.flushTask != null)
                    queue.flushTask = null;

                FlushResult flushResult = internalFlush(queue);
                if (flushResult == null)
                    return;

                List<Message> messages = flushResult.getMessages();
                if (messages != null && messages.size() > 0)
                {
                    if (queue.asyncPoll != null)
                    {
                        completeAsyncPoll(queue.asyncPoll, flushResult);
                    }
                    else
                    {
                        if (queue.flushedMessagesBetweenPolls == null)
                            queue.flushedMessagesBetweenPolls = new ArrayList<Message>();
                        queue.flushedMessagesBetweenPolls.addAll(messages);
                    }
                }

                // Schedule a delayed flush, if necessary.
                int flushWaitTime = flushResult.getNextFlushWaitTimeMillis();
                if (flushWaitTime > 0)
                    queue.flushTask = new FlexClientScheduledFlushForPoll(queue, flushWaitTime);
            }
        }
    }

    /**
     * @exclude
     * Helper class that stores per-endpoint outbound queue state including:
     * <ul>
     *   <li>flexClient - The <tt>FlexClient</tt> the queue is used by.</li>
     *   <li>messages - The outbound queue of messages for the endpoint.</li>
     *   <li>flushedMessagesBetweenPolls - Keeps track of flushed (more precisely
     *       drained buffered) messages between polls. A seperate list is needed
     *       from messages list to avoid regular flush handling.</li>
     *   <li>flushedMessagesBetweenPolls - Keeps track of flushed messages between polls.</li>
     *   <li>processor - The processor that handles adding messages to the queue as well as flushing
     *       them to the network.</li>
     *   <li>asyncPoll - The async poll to timeout or callback when messages arrive
     *       (null if the endpoint or session supports direct push).</li>
     *   <li>pushSession - A reference to a pushSession to use for direct writes to the
     *       client (null if the endpoint uses polling or handles push directly).</li>
     *  
     *   <li>flushTask - A reference to a pending flush task that will perform a delayed flush of the queue;
     *       null if no delayed flush has been scheduled.</li>
     *   <li>messageClientRefCount - A reference count of MessageClients subcribed over this endpoint.
     *       Once all MessageClients unsubscribe this queue can be shut down.</li>
     *   <li>avoidBusyPolling - Used to signal poll result generation for the queue to avoid busy polling.</li>
     * </ul>
     */
    public static class EndpointQueue
    {
        public FlexClient flexClient;
        public String endpointId;
        public Endpoint endpoint;
        public List<Message> messages;
        public List<Message> flushedMessagesBetweenPolls;
        public FlexClientOutboundQueueProcessor processor;
        public AsyncPollWithTimeout asyncPoll;
        public boolean waitPoll;
        public FlexSession pushSession;
        public TimeoutAbstractObject flushTask;
        public int messageClientRefCount;
        public boolean avoidBusyPolling;
    }
}
