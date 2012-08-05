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
import flash.net.*;
import flash.utils.*;

public class AMFTestResponder extends Responder implements IEventDispatcher
{
    public function AMFTestResponder()
    {
        super(result, status);
        eventDispatcher = new EventDispatcher(this);
    }

    public function result(r:Object):void
    {
        var event:TestResultEvent = new TestResultEvent(r);
        dispatchEvent(event);
    }

    public function status(s:Object):void
    {
        var event:TestStatusEvent = new TestStatusEvent(s);
        dispatchEvent(event);
    }

    public function addEventListener(type:String, listener:Function, useCapture:Boolean=false, priority:int=0, weakRef:Boolean=false):void
    {
        eventDispatcher.addEventListener(type, listener, useCapture, priority);
    }

    public function dispatchEvent(event:flash.events.Event):Boolean
    {
        return eventDispatcher.dispatchEvent(event);
    }

    public function hasEventListener(type:String):Boolean
    {
        return eventDispatcher.hasEventListener(type);
    }

    public function removeEventListener(type:String, listener:Function, useCapture:Boolean = false):void
    {
        eventDispatcher.removeEventListener(type, listener, useCapture);
    }

    public function willTrigger(type:String):Boolean
    {
        return eventDispatcher.willTrigger(type);
    }

    private var eventDispatcher:EventDispatcher;
}

}