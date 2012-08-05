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

public class CheckUnsubHelper extends EventDispatcher
{
    public function CheckUnsubHelper(cons:Consumer)
    {
        super();
        cons.addEventListener(MessageAckEvent.ACKNOWLEDGE, gotAck);
        consumer = cons;
    }

    private function gotAck(event:MessageAckEvent):void
    {
        var message:CommandMessage = CommandMessage(event.correlation);
        switch (message.operation)
        {
            case CommandMessage.SUBSCRIBE_OPERATION:
                consumer.unsubscribe();
            break;
            case CommandMessage.UNSUBSCRIBE_OPERATION:
                var event2:TestEvent = new TestEvent(TestEvent.COMPLETE);
                event2.passed = true;
                dispatchEvent(event2);
            break;
       }
    }

    private var consumer:Consumer;
}

}