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
package flex.messaging.io.amfx.array_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.ASObject;
import flex.messaging.MessageException;

public class Confirm2f extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm2f()
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
            ASObject prop0 = new ASObject();

            Object subprop0 = createList(1);
            addToList(subprop0, 0, "Neutrino");
            prop0.put("subprop0", subprop0);

            aso.put("prop0", prop0);

            Object prop1 = createList(1);
            addToList(prop1, 0, subprop0);

            aso.put("prop1", prop1);

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

        // Also check that by-reference serialization of the array restored
        // the pointers to the original array in the parent object's sub property...
        if (match)
        {
            ASObject aso1 = (ASObject)o1;
            ASObject aso2 = (ASObject)o2;

            ASObject prop01 = (ASObject)aso1.get("prop0");
            ASObject prop02 = (ASObject)aso2.get("prop0");

            Object subprop01 = prop01.get("subprop0");
            Object subprop02 = prop02.get("subprop0");

            Object prop11 = aso1.get("prop1");
            Object prop12 = aso2.get("prop1");

            if (getFromList(prop11, 0) != subprop01)
                return false;

            if (getFromList(prop12, 0) != subprop02)
                return false;
        }

        return match;
    }
}
