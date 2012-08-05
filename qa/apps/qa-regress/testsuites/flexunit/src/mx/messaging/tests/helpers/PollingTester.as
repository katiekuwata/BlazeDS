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

// this class is used to confirm that multiple messages sent will all be recieved
// in a polling operation
public class PollingTester extends EventDispatcher
{
    public function PollingTester(cons:Consumer, pro:Producer, messageList:Array)
    {
        super();
        _consumer = cons;
        _consumer.addEventListener(MessageAckEvent.ACKNOWLEDGE, ackHandler);
        _consumer.addEventListener(MessageEvent.MESSAGE, messageHandler);
        _consumer.subscribe();
        _producer = pro;
        _messages = messageList;
        _messageTestedCount = 0;
    }

    private function ackHandler(event:MessageAckEvent):void
    {
        if ((event.correlation is CommandMessage) && (CommandMessage(event.correlation).operation == CommandMessage.SUBSCRIBE_OPERATION))
        {
            for (var i:int=0; i<_messages.length; i++)
                _producer.send(AsyncMessage(_messages[i]));
        }
        else // Unsubscribe ack. The test is done.
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
        // see if we can find the specified message in the list
        var found:Boolean = false;
        for (var i:int=0; i<_messages.length && !found; i++)
        {
            if (_messages[i].body.text == event.message.body.text)
            {
                found = true;
            }
        }
        if (found)
            _messageTestedCount++;

        if (_messageTestedCount == _messages.length)
        {            
            _consumer.unsubscribe();
        }
    }

    private var _messages:Array;
    private var _messageTestedCount:uint;
    private var _consumer:Consumer;
    private var _producer:Producer;
}

}