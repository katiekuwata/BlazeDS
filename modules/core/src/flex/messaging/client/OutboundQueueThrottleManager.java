/*************************************************************************
  *
  * ADOBE CONFIDENTIAL
  * __________________
  *
  *  Copyright 2008 Adobe Systems Incorporated
  *  All Rights Reserved.
  *
  * NOTICE:  All information contained herein is, and remains
  * the property of Adobe Systems Incorporated and its suppliers,
  * if any.  The intellectual and technical concepts contained
  * herein are proprietary to Adobe Systems Incorporated and its
  * suppliers and may be covered by U.S. and Foreign Patents,
  * patents in process, and are protected by trade secret or copyright law.
  * Dissemination of this information or reproduction of this material
  * is strictly forbidden unless prior written permission is obtained
  * from Adobe Systems Incorporated.
  **************************************************************************/
package flex.messaging.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flex.messaging.MessageClient.SubscriptionInfo;
import flex.messaging.config.ThrottleSettings.Policy;
import flex.messaging.log.Log;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import flex.messaging.services.messaging.MessageFrequency;
import flex.messaging.services.messaging.ThrottleManager;
import flex.messaging.services.messaging.ThrottleManager.ThrottleResult;
import flex.messaging.util.StringUtils;


/**
 * Used to keep track of and limit outbound message rates of a single FlexClient queue.
 * An outbound FlexClient queue can contain messages from multiple MessageClients
 * across multiple destinations. It can also contain messages for multiple
 * subscriptions (for each subtopic/selector) across the same destination for
 * the same MessageClient.
 */
public class OutboundQueueThrottleManager
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs a default outbound queue throttle manager.
     *
     * @param processor The outbound queue processor that is using this throttle manager.
     */
    public OutboundQueueThrottleManager(FlexClientOutboundQueueProcessor processor)
    {
        destinationFrequencies = new HashMap<String, DestinationFrequency>();
        this.processor = processor;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Map of destination id and destination message frequencies.
     */
    protected final Map<String, DestinationFrequency> destinationFrequencies;

    /**
     * The parent queue processor of the throttle manager.
     */
    protected final FlexClientOutboundQueueProcessor processor;

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Determines whether the destination has been registered or not.
     *
     * @param destinationId The destination id.
     * @return True if the destination with the specified id has been registered.
     */
    public boolean isDestinationRegistered(String destinationId)
    {
        return destinationFrequencies.containsKey(destinationId);
    }

    /**
     * Registers the destination with the outbound throttle manager.
     *
     * @param destinationId The id of the destination.
     * @param outboundMaxClientFrequency The outbound max-client-frequency specified
     * at the destination.
     * @param outboundPolicy The outbound throttle policy specified at the destination.
     */
    public void registerDestination(String destinationId, int outboundMaxClientFrequency, Policy outboundPolicy)
    {
        DestinationFrequency frequency = destinationFrequencies.get(destinationId);
        if (frequency == null)
        {
            frequency = new DestinationFrequency(outboundMaxClientFrequency, outboundPolicy);
            destinationFrequencies.put(destinationId, frequency);
        }
    }

    /**
     * Unregisters the destination from the outbound throttle manager.
     *
     * @param destinationId The id of the destination.
     */
    public void unregisterDestination(String destinationId)
    {
        if (isDestinationRegistered(destinationId))
            destinationFrequencies.remove(destinationId);
    }

    /**
     * Determines whether the subscription of a certain client talking to a certain
     * destination has been registered or not.
     *
     * @param destinationId The destination id.
     * @param si The subscription information.
     *
     * @return True if the subscription has been registered.
     */
    public boolean isSubscriptionRegistered(String destinationId, SubscriptionInfo si)
    {
        if (isDestinationRegistered(destinationId))
        {
            DestinationFrequency frequency = destinationFrequencies.get(destinationId);
            return frequency != null && frequency.isSubscriptionRegistered(si);
        }
        return false;
    }

    /**
     * Registers the subscription of a client talking to a destination with the
     * specified subscription info.
     *
     * @param destinationId The destination id.
     * @param si The subscription information.
     */
    public void registerSubscription(String destinationId, SubscriptionInfo si)
    {
        DestinationFrequency frequency = destinationFrequencies.get(destinationId);
        frequency.registerSubscription(si);
    }

    /**
     * Unregisters the subscription.
     *
     * @param destinationId The destination id.
     * @param si The subscription information.
     */
    public void unregisterSubscription(String destinationId, SubscriptionInfo si)
    {
        if (isSubscriptionRegistered(destinationId, si))
        {
            DestinationFrequency frequency = destinationFrequencies.get(destinationId);
            frequency.unregisterSubscription(si);
            // If it was the last client, unregister the destination as well.
            if (frequency.subscriptionMessageFrequencies.size() == 0)
                unregisterDestination(destinationId);
        }
    }

    /**
     * Unregisters all subscriptions of the client under the specified destination.
     *
     * @param destinationId The destination id.
     */
    public void unregisterAllSubscriptions(String destinationId)
    {
        if (isDestinationRegistered(destinationId))
        {
            DestinationFrequency frequency = destinationFrequencies.get(destinationId);
            frequency.unregisterAllSubscriptions();
            // If it was the last subscription, unregister the client as well.
            if (frequency.subscriptionMessageFrequencies.size() == 0)
                unregisterDestination(destinationId);
        }
    }

    /**
     * Attempts to throttle the outgoing message.
     *
     * @param message The message to consider to throttle.
     * @return True if the message was throttled; otherwise false.
     */
    public ThrottleResult throttleOutgoingClientLevel(Message message)
    {
        String destinationId = message.getDestination();
        if (isDestinationRegistered(destinationId))
        {
            DestinationFrequency frequency = destinationFrequencies.get(message.getDestination());
            // Get the throttling limit to check against.
            int maxFrequency = frequency.getMaxFrequency(message);
            // Get the message rate of the client.)
            MessageFrequency messageFrequency = frequency.getMessageFrequency(message);
            if (messageFrequency != null)
            {
                ThrottleResult result = messageFrequency.checkLimit(maxFrequency, frequency.outboundPolicy);
                return result;
            }
        }
        // Otherwise, return OK result.
        return new ThrottleResult();
    }

    /**
     * Updates the outgoing client level message frequency of the message.
     *
     * @param message The message.
     */
    public void updateMessageFrequencyOutgoingClientLevel(Message message)
    {
        String destinationId = message.getDestination();
        if (isDestinationRegistered(destinationId))
        {
            DestinationFrequency frequency = destinationFrequencies.get(message.getDestination());
            MessageFrequency messageFrequency = frequency.getMessageFrequency(message);
            if (messageFrequency != null)
                messageFrequency.updateMessageFrequency();
        }
    }

    //--------------------------------------------------------------------------
    //
    // Inner Classes
    //
    //--------------------------------------------------------------------------

    /**
     * Used to keep track of max-client-frequency and outgoing throttle policy
     * specified at the destination. It also keeps track of outbound message
     * rates of all MessageClient subscriptions across the destination.
     */
    class DestinationFrequency
    {
        protected final int outboundMaxClientFrequency; // destination specified client limit.
        protected final Policy outboundPolicy; // destination specified policy.
        /**
         * List of message frequencies for each subscription.
         */
        protected final List<SubscriptionMessageFrequency> subscriptionMessageFrequencies;

        /**
         * Default constructor.
         *
         * @param outboundMaxClientFrequency The outbound throttling max-client-frequency of the destination.
         * @param outboundPolicy The outbound throttling policy of the destination.
         */
        DestinationFrequency(int outboundMaxClientFrequency, Policy outboundPolicy)
        {
            subscriptionMessageFrequencies = new ArrayList<SubscriptionMessageFrequency>();
            this.outboundMaxClientFrequency = outboundMaxClientFrequency;
            this.outboundPolicy = outboundPolicy;
        }

        /**
         * Returns the max-client-frequency for the subscription the message is
         * intended for which is simply the max-client-frequency specified at the destination.
         *
         * @param message The message.
         *
         * @return The max-frequency for the subscription.
         */
        int getMaxFrequency(Message message)
        {
            // Return max-frequency specified at the destination.
            return outboundMaxClientFrequency;
        }

        /**
         * Given a subscription the message is intended to, returns the message
         * rate frequency for that subscription.
         *
         * @param message The message.
         * @return The message frequency for the subscription, if it exists; otherwise null.
         */
        MessageFrequency getMessageFrequency(Message message)
        {
            String subtopic = (String)message.getHeader(AsyncMessage.SUBTOPIC_HEADER_NAME);
            String selector = (String)message.getHeader(CommandMessage.SELECTOR_HEADER);
            SubscriptionInfo si = new SubscriptionInfo(selector, subtopic);

            if (isSubscriptionRegistered(si))
            {
                for (SubscriptionMessageFrequency frequency : subscriptionMessageFrequencies)
                {
                    if (frequency.si.equals(si))
                        return frequency.mf;
                }
            }
            return null;
        }

        /**
         * Determines whether the subscription has been registered.
         *
         * @param si The subscription info.
         * @return True if the subscription has been registered.
         */
        boolean isSubscriptionRegistered(SubscriptionInfo si)
        {
            if (subscriptionMessageFrequencies != null)
            {
                for (SubscriptionMessageFrequency info : subscriptionMessageFrequencies)
                {
                    if (info.si.equals(si))
                        return true;
                }
            }
            return false;
        }

        /**
         * Registers the subscription.
         *
         * @param si The subscription info.
         */
        void registerSubscription(SubscriptionInfo si)
        {
            SubscriptionMessageFrequency frequency = new SubscriptionMessageFrequency(si, new MessageFrequency(outboundMaxClientFrequency));
            subscriptionMessageFrequencies.add(frequency);
            logMaxFrequencyDuringRegistration(outboundMaxClientFrequency, si);
        }

        /**
         * Utility function to log the maxFrequency being used during subscription.
         *
         * @param maxFrequency The maxFrequency to log.
         */
        void logMaxFrequencyDuringRegistration(int maxFrequency, SubscriptionInfo si)
        {
            if (Log.isDebug())
                Log.getLogger(ThrottleManager.LOG_CATEGORY).debug("Outbound queue throttle manager for FlexClient '"
                        + processor.getFlexClient().getId() + "' is using '" + maxFrequency
                        + "' as the throttling limit for its subscription: "
                        +  StringUtils.NEWLINE + si);
        }

        /**
         * Unregisters the subscription.
         *
         * @param si The subscription info.
         */
        void unregisterSubscription(SubscriptionInfo si)
        {
            if (isSubscriptionRegistered(si))
            {
                subscriptionMessageFrequencies.remove(si);
                // If it was the last subscription, remove the client as well.
                if (subscriptionMessageFrequencies.size() == 0)
                    unregisterAllSubscriptions();
            }
        }

        /**
         * Unregisters all subscriptions of the destination.
         *
         */
        void unregisterAllSubscriptions()
        {
            subscriptionMessageFrequencies.clear();
        }
    }

    /**
     * Combines SubscriptionInfo and MessageFrequency of a subscription.
     */
    static class SubscriptionMessageFrequency
    {
        protected final SubscriptionInfo si;
        protected final MessageFrequency mf;

        public SubscriptionMessageFrequency(SubscriptionInfo si, MessageFrequency mf)
        {
            this.si = si;
            this.mf = mf;
        }
    }
}
