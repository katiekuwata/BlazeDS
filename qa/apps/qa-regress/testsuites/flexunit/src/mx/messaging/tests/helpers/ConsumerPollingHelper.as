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
package mx.messaging.tests.helpers
{

import flash.events.*;
import mx.messaging.*;
import mx.messaging.events.*;
import mx.messaging.messages.*;
import mx.messaging.channels.*;

public class ConsumerPollingHelper extends EventDispatcher
{
    public function ConsumerPollingHelper(cons:Consumer, pro:Producer)
    {
        super();
        _consumer = cons;
        _producer = pro;
        _consumer.addEventListener(MessageEvent.MESSAGE, messageHandler);
        _consumer.addEventListener(MessageAckEvent.ACKNOWLEDGE, ackHandler);
        _consumer.subscribe();
    }

    private function ackHandler(event:MessageAckEvent):void
    {
        if ((event.correlation is CommandMessage) && (CommandMessage(event.correlation).operation == CommandMessage.SUBSCRIBE_OPERATION))
        {
            var msg:AsyncMessage = new AsyncMessage();
            msg.body = "Polling Message 1";
            _producer.send(msg);
        }
        else // Unsubscribe ack.
        {
            var event2:TestEvent = new TestEvent(TestEvent.COMPLETE);
            event2.passed = true;
            dispatchEvent(event2);
            _consumer.disconnect();
            _producer.disconnect();
        }
    }

    private function messageHandler(event:MessageEvent):void
    {
        // Got message, go ahead and unsubscribe.        
        _consumer.unsubscribe();
    }

    private var _consumer:Consumer;
    private var _producer:Producer;
}

}