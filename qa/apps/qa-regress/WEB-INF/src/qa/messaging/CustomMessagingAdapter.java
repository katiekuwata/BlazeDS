/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
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

package qa.messaging;

import flex.messaging.messages.Message;
import flex.messaging.services.MessageService;
import flex.messaging.services.ServiceAdapter;
/**
 * The <code>CustomMessagingAdapter</code> class is a custom message adapter used to verify that messages
 * can be sent to and received from a Destination that uses a custom message adapter. A custom message adapter 
 * extends <code>flex.messaging.services.ServiceAdapter</code> and overrides the invoke method.
 */
public class CustomMessagingAdapter extends ServiceAdapter {
	/**
     * Constructs an unmanaged <code>CustomMessagingAdapter</code> instance.
     */
	public CustomMessagingAdapter() {
	}
	/**
     * Constructs a <code>CustomMessagingAdapter</code> instance.
     *
     * @param enableManagement <code>true</code> if the <code>ServiceAdapter</code> has a
     * corresponding MBean control for management; otherwise <code>false</code>.
     */
	public CustomMessagingAdapter(boolean enableManagement) {
		super(enableManagement);
	}
	/**
     * Handle a data message intended for this adapter.  In this case we just send the 
     * message by first getting the <code>MessageService</code> from the destination 
     * and then calling <code>pushMessageToClients</code> and <code>sendPushMessageFromPeer</code>
     * on the <code>MessageService</code>. To send a message to clients, you call 
     * the MessageService.pushMessageToClients() method in your adapter's invoke() method. This method 
     * takes a message object as its first parameter. Its second parameter is a Boolean value that indicates whether to 
     * evaluate message selector statements. You can call the MessageService.sendPushMessageFromPeer() method in your adapter's 
     * invoke() method to broadcast messages to peer server nodes in a clustered environment. 
     * 
     * @param message the message as sent by the client intended for this adapter
     * @return the body of the acknowledge message (or null if there is no body)
     *
     * @see flex.messaging.messages.Message
     * @see flex.messaging.messages.AsyncMessage
     */
	public Object invoke(Message message) {
		MessageService msgService = (MessageService)getDestination().getService();
        msgService.pushMessageToClients(message, true);
        msgService.sendPushMessageFromPeer(message, true);
        System.out.println("Operation invoke called in Custom Messaging Adapter.");
        return null;
	}

}
