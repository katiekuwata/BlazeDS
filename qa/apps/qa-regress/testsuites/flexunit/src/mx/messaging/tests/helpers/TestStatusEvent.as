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

/**
 * A helper class for raw NetConnection tests so that
 * a Responder's status object can be treated like a status (fault)
 * event in the flexunit framework.
 *
 * @private
 */
public class TestStatusEvent extends Event
{
    public static const STATUS:String = "status";

    public function TestStatusEvent(s:Object)
    {
        super(STATUS);
        _status = s;
    }

    public function get status():Object
    {
        return _status;
    }

    private var _status:Object;
}

}