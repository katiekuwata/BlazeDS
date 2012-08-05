/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2008] Adobe Systems Incorporated
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

package qa.messaging;

import java.util.List;
import flex.messaging.FlexContext;
import flex.messaging.MessageRoutedEvent;
import flex.messaging.MessageRoutedListener;
import flex.messaging.MessageRoutedNotifier;
import flex.messaging.VersionInfo;
import flex.messaging.client.FlexClientOutboundQueueProcessor;
import flex.messaging.config.ConfigMap;
import flex.messaging.messages.Message;

public class MessageRoutedListenerTest extends FlexClientOutboundQueueProcessor implements MessageRoutedListener
{
    
    public void initialize(ConfigMap properties) 
    {
        System.out.println("======== initialize custom FlexClientOutboundQueueProcessor " + VersionInfo.getBuildAsLong());
        super.initialize(properties);
    }
    
    // add event listener and route the message to the queue
    public void add(List outboundQueue, Message message)
    {
        MessageRoutedNotifier notifier = FlexContext.getMessageRoutedNotifier();
        
        if(notifier != null)
        {
            notifier.addMessageRoutedListener(this);
            System.out.println("======== MessageRoutedListener added");
            message.setBody("The original message has been removed");
        }
        
        super.add(outboundQueue, message);       
    }
    
    public void messageRouted(MessageRoutedEvent event)
    {
        Message msg = event.getMessage();
        System.out.println("========= MessageRouted: id=" + msg.getMessageId() + " destination=" + msg.getDestination() + " body=" + msg.getBody().toString());        
    }
    
}
