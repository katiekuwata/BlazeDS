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
package flex.messaging.cluster;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.lang.reflect.InvocationTargetException;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.JChannelFactory;
import org.jgroups.Message;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;

import flex.messaging.FlexContext;
import flex.messaging.config.ConfigMap;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.log.Log;
import flex.messaging.services.Service;
import flex.messaging.util.StringUtils;
import flex.messaging.util.ExceptionUtil;

/**
 * @exclude
 * A collection of ClusterNode instances scoped to a specific destination.
 */
public class JGroupsCluster extends Cluster implements RequestHandler
{
    /**
     * ConfigMap property names for initialize(String, ConfigMap).
     */
    public static final String PROPERTY_CHANNEL_BLOCK = "channel-block";
    public static final String PROPERTY_CHANNEL_AUTO_GETSTATE = "channel-auto-getstate";
    public static final String PROPERTY_CHANNEL_AUTO_RECONNECT = "channel-auto-reconnect";
    public static final String PROPERTY_CHANNEL_LOCAL = "channel-local";

    /**
     * The JGroups MessageDispatcher that distributes messages for this
     * cluster.
     */
    private MessageDispatcher broadcastDispatcher;

    /**
     * The BroadcastHandlers registered with this cluster to handle
     * specific internal cluster events.
     */
    private final List<BroadcastHandler> broadcastHandlers;

    /**
     * The id of the cluster.
     */
    private String clusterId;

    /**
     * The JGroups channel for this distributed cluster.
     */
    private JChannel clusterChannel;

    /**
     * The cluster manager for the application.
     */
    private final ClusterManager clusterManager;

    /**
     * The listener for cluster for cluster membership. The listener
     * receives JGroups cluster membership events (join and abandon)
     * and handles then appropriately.
     */
    private final ClusterMembershipListener clusterMembershipListener;

    /**
     * The nodes of the cluster.
     */
    private final Map<Address,ClusterNode> clusterNodes;

    /**
     * JGroups implementation of the cluster.
     *
     * @param clusterManager A link to the cluster manager.
     */
    public JGroupsCluster(ClusterManager clusterManager)
    {
        this.clusterManager = clusterManager;
        broadcastHandlers = new ArrayList<BroadcastHandler>();
        clusterMembershipListener = new ClusterMembershipListener(this);
        clusterNodes = new HashMap<Address,ClusterNode>();
        configureBroadcastHandlers();
    }

    /**
     * Initializes the Cluster with the id and the map of properties.
     * 
     * @param id The cluster id.
     * @param properties The map of properties.
     */
    @Override
    public void initialize(String id, ConfigMap properties)
    {
        clusterId = id;
        if (Log.isDebug())
            Log.getLogger(LOG_CATEGORY).debug("Joining cluster with id: " + clusterId);

        boolean channelAutoGetState = false;
        boolean channelAutoReconnect = false;
        boolean channelBlock = false;
        boolean channelLocal = false; // Disable the delivery of messages to ourselves.

        if (properties != null && !properties.isEmpty())
        {
            channelAutoGetState = properties.getPropertyAsBoolean(PROPERTY_CHANNEL_AUTO_GETSTATE, false);
            channelAutoReconnect = properties.getPropertyAsBoolean(PROPERTY_CHANNEL_AUTO_RECONNECT, false);
            channelBlock = properties.getPropertyAsBoolean(PROPERTY_CHANNEL_BLOCK, false);
            channelLocal = properties.getPropertyAsBoolean(PROPERTY_CHANNEL_LOCAL, false);
        }

        try
        {
            JChannelFactory channelFactory = new JChannelFactory(clusterPropertiesFile);
            clusterChannel = (JChannel) channelFactory.createChannel();
            clusterChannel.setOpt(Channel.AUTO_GETSTATE, channelAutoGetState);
            clusterChannel.setOpt(Channel.AUTO_RECONNECT, channelAutoReconnect);
            clusterChannel.setOpt(Channel.BLOCK, channelBlock);
            clusterChannel.setOpt(Channel.LOCAL, channelLocal);
            broadcastDispatcher = new MessageDispatcher(clusterChannel, null, clusterMembershipListener, this);
            clusterChannel.connect(clusterId);
        }
        catch (ChannelException cex)
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10200, new Object[] { clusterId, clusterPropertiesFile });
            cx.setRootCause(cex);
            throw cx;
        }
    }

    /**
     * Retrieve a List of Maps, where each Map contains channel id keys
     * mapped to endpoint URLs. There is exactly one endpoint URL for each
     * channel id. This List represents all of the known endpoint URLs
     * for all of the channels in the Cluster.
     *
     * @param return A list of endpoint URLs for all the nodes in the cluster.
     * @param serviceType Name of the service for this destination.
     * @param destName Name of the destination.
     */
    @Override
    public List<Map<String,String>> getAllEndpoints(String serviceType, String destName)
    {
        List<Map<String,String>> channelToEndpointMaps = new ArrayList<Map<String,String>>();

        synchronized (clusterNodes)
        {
            for (Entry<Address,ClusterNode> clusterNodeEntry : clusterNodes.entrySet())
            {
                Address addr = clusterNodeEntry.getKey();
                if (clusterMembershipListener.isZombie(addr))
                    continue;

                ClusterNode node = clusterNodes.get(addr);
                Map<String,String> nodeEndpoints = node.getEndpoints(serviceType, destName);

                if (nodeEndpoints.isEmpty())
                    continue;

                // Remove duplicate channel endpoints.
                for (Map<String,String> nodeEndpoints2 : channelToEndpointMaps)
                {
                    for (String endpointUrl : nodeEndpoints2.values())
                    {
                        if (nodeEndpoints.containsValue(endpointUrl)) // Remove the duplicate
                        {
                            for (Iterator<String> iter3 = nodeEndpoints.values().iterator(); iter3.hasNext();)
                            {
                                String endpointUrl2 = iter3.next();
                                if (endpointUrl2.equals(endpointUrl))
                                    iter3.remove();
                            }
                        }
                    }
                }

                if (nodeEndpoints.size() > 0)
                    channelToEndpointMaps.add(nodeEndpoints);
            }
        }
        return channelToEndpointMaps;
    }

    /**
     * Shutdown the cluster.
     */
    @Override
    public void destroy()
    {
        try
        {
            clusterChannel.close();
        }
        catch (Exception e)
        {}
    }

    /**
     * Clusters broadcast messages across their physical nodes, and when they
     * receive those messages they locate a BroadcastHandler capable of handling
     * the broadcast. This method configures those handlers.
     */
    void configureBroadcastHandlers()
    {
        // Add handlers in order, with handlers that service a larger number of common
        // messages ahead of those that service fewer.
        broadcastHandlers.add(new ServiceOperationHandler(this));
        broadcastHandlers.add(new RemoteEndpointHandler(this));
    }

    /**
     * This method is called in response to a ClusterMembershipListener's
     * notification that an Address has joined the Cluster.
     *
     * @param address Address of the node that was added to the cluster.
     */
    void addClusterNode(Address address)
    {
        if (Log.isDebug())
            Log.getLogger(LOG_CATEGORY).debug("Cluster node from address " + address +
                    " joined the cluster for " + clusterId);

        ClusterNode remoteNode = null;
        synchronized (clusterNodes)
        {
            remoteNode = clusterNodes.get(address);
        }

        if (remoteNode == null)
            broadcastMyEndpoints(address);
    }

    /**
     * In response to a ClusterMembershipListener's notification that an
     * Address has abandoned the Cluster, remove the node and avoid using
     * its channel and endpoint information in the future.
     *
     * @address Address of the node that was removed from the cluster.
     */
    void removeClusterNode(Address address)
    {
        synchronized (clusterNodes)
        {
            clusterNodes.remove(address);
        }
            
        sendRemoveNodeListener(address);

        if (Log.isDebug())
            Log.getLogger(LOG_CATEGORY).debug("Cluster node from address " + address +
                    " abandoned the cluster for " + clusterId);
    }

    /**
     * Return the physical address of the local ClusterNode.
     * This method is specific to the JGroups implementation.
     *
     * @return Address of the local cluster node.
     */
    Address getJGroupsLocalAddress()
    {
        return clusterChannel.getLocalAddress();
    }

    /**
     * Returns the local cluster node.
     */
    @Override
    public Object getLocalAddress()
    {
        return getJGroupsLocalAddress();
    }

    /**
     * Add a local endpoint URL for a local channel. After doing so, broadcast the information to
     * peers so that they will be aware of the URL.
     *
     * @param serviceType The service for this destination.
     * @param destName The destination name.
     * @param channelId The data services channel for this destination.
     * @param endpointUrl The endpoint url to be added to this cluster.
     * @param endpointPort The endpoint port for this cluster.
     */
    @Override
    public void addLocalEndpointForChannel(String serviceType, String destName,
                                    String channelId, String endpointUrl, int endpointPort)
    {
        if (Log.isDebug())
            Log.getLogger(LOG_CATEGORY).debug("Adding local clustered destination endpoint and broadcasting to peers. cluster-id=" + clusterId +
                                              " destination=" + destName + " channelId=" + channelId +
                                              " endpoint url=" + endpointUrl + " endpointPort=" + endpointPort);

        Address myAddr = getJGroupsLocalAddress();
        ClusterNode myNode = getNodeForAddress(myAddr);
        endpointUrl = canonicalizeUrl(channelId, endpointUrl, endpointPort, myNode);
        myNode.addEndpoint(serviceType, destName, channelId, endpointUrl);
        broadcastClusterOperation("addEndpointForChannel", serviceType, destName,
                                  channelId, endpointUrl, null);
    }

    /**
     * This method is invoked by the RemoteEndpointEndpointHandler when it receives a message from a remote peer.
     * The remote peer has broadcast some channel id and endpoint URL information, which we add to our local
     * list of such information, and we also tell the sender about our own channel and endpoint information.
     *
     * @param address The remote peer which broadcast the original message.
     * @param serviceType The service for this destination.
     * @param destName The destination name.
     * @param channelId The data services channel for this destination.
     * @param endpointUrl The endpoint url to be added to this cluster.
     */
    void addEndpointForChannel(Address address, String serviceType, String destName, String channelId, String endpointUrl)
    {
        ClusterNode node = getNodeForAddress(address);
        if (!node.containsEndpoint(serviceType, destName, channelId, endpointUrl))
        {
            if (Log.isDebug())
                Log.getLogger(LOG_CATEGORY).debug("Adding remote clustered destination endpoint from address " + address + ". cluster-id=" + clusterId +
                                                  " destination=" + destName + " channelId=" + channelId +
                                                  " endpoint url=" + endpointUrl);
            
            node.addEndpoint(serviceType, destName, channelId, endpointUrl);
            broadcastMyEndpoints(address);
        }
    }

    /**
     * Tell a specific remote peer about our local channel and endpoint URL information.
     *
     * @address The remote peer which broadcast the original message.
     */
    void broadcastMyEndpoints(Address address)
    {
        // tell the new node about my channel id -> endpoint urls
        Vector<Address> destination = new Vector<Address>();
        destination.add(address);
        ClusterNode myNode = getNodeForAddress(clusterChannel.getLocalAddress());
        Map<String,Map<String,String>> destKeyToChannelMap = myNode.getDestKeyToChannelMap();
        synchronized (destKeyToChannelMap)
        {
            for (Entry<String,Map<String,String>> entry : destKeyToChannelMap.entrySet())
            {
                String destKey = entry.getKey();
                int ix = destKey.indexOf(":");
                String serviceType = destKey.substring(0, ix);
                String destName = destKey.substring(ix+1);
                Map<String,String> channelEndpoints = myNode.getEndpoints(serviceType, destName);
                for (Entry<String,String> channelEndpointEntry : channelEndpoints.entrySet())
                {
                    broadcastClusterOperation("addEndpointForChannel", serviceType, destName, channelEndpointEntry.getKey(), channelEndpointEntry.getValue(), destination);
                }
            }
        }
    }

    /**
     * Broadcast a cluster-related operation, such as channel and endpoint information,
     * out to the remote peers.
     *
     * @param clusterOperation The cluster operation that should be invoked.
     * @param serviceType The service that the operation should be invoked on.
     * @param destName The destination that the operation should be invoked on.
     * @param channelId The data service channel id used by this destination.
     * @param endpointUrl The endpoint url initiating this operation.
     * @param destinations Addresses to which this operation should be broadcast. Null indicates
     *    that the message should be broadcast to all nodes in the cluster.
     *
     * @see RemoteEndpointHandler The handler on the peer node that responds to cluster operations.
     * @see RemoteOperationHandler#isSupportedOperation List of service operations handled by the cluster.
     */
    void broadcastClusterOperation(String clusterOperation, String serviceType, String destName,
                                   String channelId, String endpointUrl, Vector<Address> destinations)
    {
        List<Object> operationInfo = new ArrayList<Object>();
        operationInfo.add(serviceType);
        operationInfo.add(destName);
        operationInfo.add(channelId);
        operationInfo.add(endpointUrl);
        broadcastOperation(clusterOperation, operationInfo, destinations);
    }

    /**
     * Broadcast a service-related operation, which usually includes a Message as a method parameter. This method
     * allows a local service to process a Message and then send the Message to the services on all peer nodes
     * so that they may perform the same processing.
     *
     * @param serviceOperation The service operation to be passed to cluster nodes.
     * @param params Parameters that are needed to perform the operation.
     *
     * @see ServiceOperationHandler The handler on the peer node that responds to service operations.
     * @see ServiceOperationHandler#supportedOperations List of service operations handled by the cluster.
     */
    @Override
    public void broadcastServiceOperation(String serviceOperation, Object[] params)
    {
        ArrayList<Object> operationInfo = new ArrayList<Object>();
        operationInfo.addAll(Arrays.asList(params));
        broadcastOperation(serviceOperation, operationInfo, null);
    }

    /**
     * Send a service-related operation in point-to-point fashion to one and only one member of the cluster.
     * This is similar to the broadcastServiceOperation except that this invocation is sent to the node
     * identified by the targetAddresss or when the targetAddress is null to first
     * node among the cluster members that does not have the local node's address.
     *
     * @param serviceOperation The service operation to be passed to the specified cluster node.
     * @param params Parameters that are need to perform the operation.
     * @param targetAddress The node that the operation should be passed to. If null, the first non-local
     *    address in the cluster is used as the targetAddress.
     *
     * @see ServiceOperationHandler#supportedOperations List of service operations handled by the cluster.
     */
    @Override
    public void sendPointToPointServiceOperation(String serviceOperation, Object[] params, Object targetAddress)
    {
        ArrayList<Object> operationInfo = new ArrayList<Object>();
        operationInfo.addAll(Arrays.asList(params));
        // for point to point invocations, add the sender's address as a param
        operationInfo.add(getJGroupsLocalAddress());
        Vector<Address> targetDestination = new Vector<Address>();
        if (targetAddress != null)
        {
            targetDestination.add((Address)targetAddress);
        }
        else
        {
            for (int i=0; i<clusterChannel.getView().getMembers().size(); i++)
            {
                Address a = (Address) clusterChannel.getView().getMembers().get(i);
                if (!a.equals(getJGroupsLocalAddress()))
                {
                    targetDestination.add(a);
                    break;
                }
            }
        }
        broadcastOperation(serviceOperation, operationInfo, targetDestination);
    }

    /**
     * Returns the Address instances for each of the servers in the cluster.
     *
     * @return A list of address for the nodes in the cluster.
     */
    @Override
    public List<Address> getMemberAddresses()
    {
        return clusterChannel.getView().getMembers();
    }

    /**
     * This is the core broadcast operation.
     *
     * @param handlerClass The name of the handler class for this operation.
     * @param operationName The name of the operation to be performed.
     * @param operationParams Parameters that are need to perform the operation.
     * @param destinations Addresses to which this operation should be broadcast. Null indicates
     *    that the message should be broadcast to all nodes in the cluster.
     */
    private void broadcastOperation(String operationName, List<Object> operationParams, Vector<Address> destinations)
    {
        try
        {
            operationParams.add(0, operationName);
            Message operationMessage = new Message(null, getJGroupsLocalAddress(), (Serializable) operationParams);
            // null destinations implies a broadcast to all members (but ourself cause local is off)
            broadcastDispatcher.castMessage(destinations, operationMessage, GroupRequest.GET_NONE, 0);
        }
        catch (IllegalArgumentException iae)
        {
            String message = iae.getMessage();
            String notSerializableType = null;
            if ((message != null) && message.startsWith("java.io.NotSerializableException"))
                notSerializableType = message.substring(message.indexOf(": ") + 2);

            if (notSerializableType != null)
            {
                ClusterException cx = new ClusterException();
                cx.setMessage(10212, new Object[] { clusterId, notSerializableType });
                cx.setRootCause(iae);
                throw cx;
            }
            else
            {
                ClusterException cx = new ClusterException();
                cx.setMessage(10204, new Object[] { clusterId });
                cx.setRootCause(iae);
                throw cx;
            }
        }
        catch (Exception e)
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10204, new Object[] { clusterId });
            cx.setRootCause(e);
            throw cx;
        }
    }

    /**
     * Receive a message broadcast by another peer. This method is responsible for routing
     * the message to the appropriate broadcast handler.
     *
     * @param msg The message passed from a cluster peer.
     * @return In this implementation, always null.
     */
    public Object handle(Message msg)
    {
        // Only route messages that did not originate on this node
        if (msg.getSrc() != getJGroupsLocalAddress()) // TODO - shouldn't this be checked with equals?
        {
            @SuppressWarnings("unchecked")
            List<Object> operationInfo = (List<Object>)msg.getObject();
            String operationName = (String) operationInfo.get(0);
            try
            {
                // We only have the message broker, but make that available to
                // the handler thread.
                FlexContext.setThreadLocalObjects(null, null, clusterManager.getMessageBroker(),
                                                  null, null, null);
                boolean handled = false;
                for (BroadcastHandler handler : broadcastHandlers)
                {
                    if (handler.isSupportedOperation(operationName))
                    {
                        handler.handleBroadcast(msg.getSrc(), operationInfo);
                        handled = true;
                        break;
                    }
                }
                if (!handled && Log.isWarn())
                    Log.getLogger(LOG_CATEGORY).warn("Cluster message was not handled by any registered handler. Unhandled message info: " + operationInfo);                    
            }
            finally
            {
                FlexContext.clearThreadLocalObjects();
            }
        }
        return null;
    }

    /**
     * Locate the ClusterNode mapped to the provided physical address. If no ClusterNode
     * exists for the address, create one.
     *
     * @param addr Address of the node to locate.
     * @return  ClusterNode for the given address.
     */
    private ClusterNode getNodeForAddress(Address addr)
    {
        synchronized (clusterNodes)
        {
            ClusterNode node = (ClusterNode) clusterNodes.get(addr);
            if (node == null)
            {
                node = new ClusterNode(addr);
                clusterNodes.put(addr, node);
            }
            return node;
        }
    }

    /**
     * The endpoint in the configuration may may not be fully-qualified, but it must be before it
     * is added to a ClusterNode.
     *
     * @param channelId The channel id for this endpoint.
     * @param endpointUrl The endpoint url.
     * @param endpointPort The endpoint port for the url.
     * @param myNode The current node for the cluster.
     * @return The fully-qualified url for the endpoint.
     */
    private String canonicalizeUrl(String channelId, String endpointUrl, int endpointPort, ClusterNode myNode)
    {
        if (endpointUrl.startsWith("/"))
        {
            ClusterException cx = new ClusterException();
            cx.setMessage(10203, new Object[] { channelId});
            throw cx;
        }

        if (endpointUrl.indexOf(":///") != -1)
            endpointUrl = StringUtils.substitute(endpointUrl, ":///", "://" + myNode.getHost() + "/");

        if (endpointPort != 0 && endpointUrl.indexOf("" + endpointPort) == -1)
        {
            StringBuffer sb = new StringBuffer(endpointUrl);
            sb.insert(endpointUrl.indexOf("/", endpointUrl.indexOf("://") + 3), ":" + endpointPort);
            endpointUrl = sb.toString();
        }

        return endpointUrl;
    }

    /**
     * This BroadcastHandler implementation handles cluster operations
     * broadcast by a Cluster.
     */
    static class RemoteEndpointHandler implements BroadcastHandler
    {
        static final HashMap<String,Boolean> supportedOperations = new HashMap<String,Boolean>();
        static // Populate lookup table with op names. 
        {
            supportedOperations.put("addEndpointForChannel", Boolean.TRUE);
            supportedOperations.put("sendEndpointUrl", Boolean.TRUE);
            supportedOperations.put("receiveEndpointUrl", Boolean.TRUE);            
        }
        
        public RemoteEndpointHandler(final JGroupsCluster cluster)
        {
            this.cluster = cluster;
        }
        
        private final JGroupsCluster cluster;
        
        /**
         * Handle the broadcast message.  This handler supports the single operation
         * "addEndpointForChannel".
         *
         * @param sender Sender of the original message.
         * @param params Any parameters need to handle the message.
         */
        public void handleBroadcast(Object sender, List<Object> params)
        {
            String opName = (String)params.get(0);
            // Special case handling for the "addEndpointForChannel" op.
            if (opName.equals("addEndpointForChannel"))
            {
                // note: the operation name is at index 0, and we do not need it in this handler
                // the serviceType, destName, channel id and endpoint url are expected to be at indexes 1, 2, 3, and 4 respectively
                cluster.addEndpointForChannel((Address)sender, (String) params.get(1), (String) params.get(2), (String) params.get(3), (String) params.get(4));
                return;
            }
            
            // All other ops are routed to an endoint based on id, specified in the 2nd param.
            // Arguments are optional, but if passed will show up as param 3-N. These will be passed into a
            // method on the endpoint having a name that matches the op name (param 0).
            String endpointId = (String)params.get(1);
            Endpoint endpoint = cluster.clusterManager.getMessageBroker().getEndpoint(endpointId);
            try
            {                
                if (endpoint != null)
                {
                    Object[] paramValues = params.subList(3, params.size()).toArray();
                    Method[] endpointMethods = endpoint.getClass().getMethods();
                    // note: in order to avoid requiring endpoints to have specific formal
                    // types on methods (superclasses aren't honored by reflection) we just
                    // grab the first method we see with the correct name
                    // -- intended for internal use only
                    for (int i=0; i<endpointMethods.length; i++)
                    {
                        if (endpointMethods[i].getName().equals(opName))
                        {
                            endpointMethods[i].invoke(endpoint, paramValues);
                            break;
                        }
                    }
                }
                else if (Log.isWarn())
                {
                    Log.getLogger(LOG_CATEGORY).warn("Cluster message targeting endpoint '" + endpointId + "' will be ignored because no endpoint is registered under that id.");
                }
            }
            catch (InvocationTargetException ite)
            {
                Throwable th = ite.getCause();
                if (Log.isError())
                {
                    Log.getLogger(LOG_CATEGORY).error("Error handling cluster message targetting endpoint '" + endpointId + "' and operation '" + opName + "'.", th);
                }
                ClusterException cx = new ClusterException();
                cx.setMessage(10219, new Object[] { cluster.clusterId});
                cx.setRootCause(th);
                throw cx;
            }
            catch (Exception e)
            {
                if (Log.isError())
                {
                    Log.getLogger(LOG_CATEGORY).error("Error handling cluster message targetting endpoint '" + endpointId + "' and operation '" + opName + "'.", e);
                }
                ClusterException cx = new ClusterException();
                cx.setMessage(10219, new Object[] { cluster.clusterId });
                cx.setRootCause(e);
                throw cx;
            }
        }

        /**
         * Determine whether this Handler supports a particular operation by name.
         * This handler supports the "addEndpointForChannel" operation.
         *
         * @return Whether or not this handler supports the named operation.
         * @param name Name of the operation.
         */
        public boolean isSupportedOperation(String name)
        {
            return supportedOperations.containsKey(name); 
        }
    }

    /**
     * This BroadcastHandler implementation handles service operations
     * broadcast by a Cluster.
     * It is static to support the static lookup table of supported operations to avoid
     * needing to iterate a list for every received message from a peer node in the cluster.
     */
    static class ServiceOperationHandler implements BroadcastHandler
    {
        static final HashMap<String,Boolean> supportedOperations = new HashMap<String,Boolean>();
        static // Populate lookup table with op names. 
        {
            supportedOperations.put("pushMessageFromPeer", Boolean.TRUE);
            supportedOperations.put("peerSyncAndPush", Boolean.TRUE);
            supportedOperations.put("requestAdapterState", Boolean.TRUE);
            supportedOperations.put("receiveAdapterState", Boolean.TRUE);
            supportedOperations.put("sendSubscriptions", Boolean.TRUE);
            supportedOperations.put("receiveSubscriptions", Boolean.TRUE);
            supportedOperations.put("subscribeFromPeer", Boolean.TRUE);
            supportedOperations.put("pushMessageFromPeerToPeer", Boolean.TRUE);
            supportedOperations.put("peerSyncAndPushOneToPeer", Boolean.TRUE);            
        }
        
        public ServiceOperationHandler(final JGroupsCluster cluster)
        {
            this.cluster = cluster;
        }
        
        private final JGroupsCluster cluster;

        /**
         * Handle the broadcast message.  This method uses introspection
         * to invoke the method defined by the supportedOperation on the
         * service class.
         *
         * @param sender Sender of the original message.
         * @param params Any parameters need to handle the message.
         */
        public void handleBroadcast(Object sender, List<Object> params)
        {
            try
            {
                // param 0 is the method name
                String serviceType = (String) params.get(1);
                // In this case, the destName is not used because the dest is in the
                // message.  It is here just to be consistent with the other methods and
                // in case we need to send something to a destination without a message.
                // String destName = (String) params.get(2);
                Service svc = cluster.clusterManager.getMessageBroker().getServiceByType(serviceType);
                if (svc != null)
                {
                    String methodName = (String) params.get(0);
                    Object[] paramValues = params.subList(3, params.size()).toArray();
                    Method[] svcMethods = svc.getClass().getMethods();
                    // note: in order to avoid requiring services to have specific formal
                    // types on methods (superclasses aren't honored by reflection) we just
                    // grab the first method we see with the correct name
                    // -- intended for internal use only
                    for (int i=0; i<svcMethods.length; i++)
                    {
                        if (svcMethods[i].getName().equals(methodName))
                        {
                            svcMethods[i].invoke(svc, paramValues);
                            break;
                        }
                    }
                }
            }
            catch (InvocationTargetException ite)
            {
                Throwable th = ite.getCause();
                if (Log.isError())
                {
                    Log.getLogger(LOG_CATEGORY).error("Error handling message pushed from cluster: " + th + StringUtils.NEWLINE + 
                                                      "Exception=" + ExceptionUtil.toString(th));
                }
                ClusterException cx = new ClusterException();
                cx.setMessage(10205, new Object[] { cluster.clusterId});
                cx.setRootCause(th);
                throw cx;
            }
            catch (Exception e)
            {
                if (Log.isError())
                {
                    Log.getLogger(LOG_CATEGORY).error("Error handling message pushed from cluster: " + e + StringUtils.NEWLINE + 
                                                      "Exception=" + ExceptionUtil.toString(e));
                }
                ClusterException cx = new ClusterException();
                cx.setMessage(10205, new Object[] { cluster.clusterId });
                cx.setRootCause(e);
                throw cx;
            }
        }

        /**
         * Determine whether this Handler supports a particular operation by name.
         *
         * @return Whether or not this handler supports the named operation.
         * @param name Name of the operation.
         * @see #supportedOperations
         */
        public boolean isSupportedOperation(String name)
        {
            return supportedOperations.containsKey(name); 
        }
    }
}
