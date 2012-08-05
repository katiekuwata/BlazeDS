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

public class Confirm2b extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm2b()
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

            Object list1 = createList(2);
            addToList(list1, 0, new Integer(200));
            addToList(list1, 1, new Integer(400));

            Object list2 = createList(4);
            addToList(list2, 0, new Double(200.5));
            addToList(list2, 1, new Double(400.5));
            addToList(list2, 2, new Double(600.5));
            addToList(list2, 3, new Double(800.5));

            aso.put("prop0", list1);
            aso.put("prop1", list2);

            body.setData(aso);
            EXPECTED_VALUE = m;
        }

        return EXPECTED_VALUE;
    }

    public MessageException getExpectedException()
    {
        return null;
    }
}
