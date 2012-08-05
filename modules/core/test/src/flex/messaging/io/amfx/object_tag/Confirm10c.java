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
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.io.amfx.object_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.ASObject;
import flex.messaging.MessageException;

import java.util.Date;

public class Confirm10c extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm10c()
    {
    }

    public ActionMessage getExpectedMessage()
    {
        if (EXPECTED_VALUE == null)
        {
            ActionMessage m = new ActionMessage();
            MessageBody body = new MessageBody();
            m.addBody(body);

            ASObject aso = new ASObject();

            Object prop0 = createList(2);
            ASObject prop00 = new ASObject();
            prop00.put("subprop0", new Date(1119647239994L));
            addToList(prop0, 0, prop00);

            ASObject prop01 = new ASObject();
            ASObject subprop0 = new ASObject();
            subprop0.put("subsubprop0", new Integer(1000000));
            prop01.put("subprop0", subprop0);
            addToList(prop0, 1, prop01);

            aso.put("prop0", prop0);
            aso.put("prop1", subprop0);

            body.setData(aso);
            EXPECTED_VALUE = m;
        }
        return EXPECTED_VALUE;
    }

    public MessageException getExpectedException()
    {
        return null;
    }

    protected boolean bodyValuesMatch(Object o1, Object o2)
    {
        boolean match = super.bodyValuesMatch(o1, o2);

        // Also check that by-reference serialization of objects
        if (match)
        {
            ASObject aso1 = (ASObject)o1;
            ASObject aso2 = (ASObject)o2;

            Object prop01 = aso1.get("prop0");
            Object prop02 = aso2.get("prop0");

            ASObject prop011 = (ASObject)getFromList(prop01, 1);
            ASObject prop012 = (ASObject)getFromList(prop02, 1);

            ASObject subprop01 = (ASObject)prop011.get("subprop0");
            ASObject subprop02 = (ASObject)prop012.get("subprop0");

            ASObject prop11 = (ASObject)aso1.get("prop1");
            ASObject prop12 = (ASObject)aso2.get("prop1");

            if (prop11 != subprop01)
                return false;

            if (prop12 != subprop02)
                return false;
        }

        return match;
    }
}
