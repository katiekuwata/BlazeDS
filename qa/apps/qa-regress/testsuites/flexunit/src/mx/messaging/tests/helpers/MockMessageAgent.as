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

package mx.messaging.tests.helpers {

import mx.logging.*;
import mx.messaging.*;
import mx.messaging.channels.*
import mx.messaging.config.*;
import mx.messaging.errors.*;
import mx.messaging.events.*;
import mx.messaging.messages.*;

/**
 *  Simple MockMessageAgent to test base functionality.
 */
public class MockMessageAgent extends MessageAgent
{
    public function MockMessageAgent()
    {
        super();
        _log = Log.getLogger("ChannelSetTest");
		_agentType = "stub-agent";
    }
    
    override public function acknowledge(ackMsg:AcknowledgeMessage, msg:IMessage):void
    {
        dispatchEvent(MessageAckEvent.createEvent(ackMsg, msg));
    }
    
    override public function fault(errMsg:ErrorMessage, msg:IMessage):void
    {
        dispatchEvent(MessageFaultEvent.createEvent(errMsg));
    }
    
    public function send(message:IMessage):void
    {
        internalSend(message);
    }
}

}