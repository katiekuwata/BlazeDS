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
package flex.messaging.io.amfx.traits_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.ASObject;
import flex.messaging.MessageException;

public class Confirm12b extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm12b()
    {
    }

    public ActionMessage getExpectedMessage()
    {
        if (EXPECTED_VALUE == null)
        {
            ActionMessage m = new ActionMessage();
            MessageBody body = new MessageBody();
            m.addBody(body);

            Object list = createList(3);

            ASObject aso1 = new ASObject();
            aso1.put("prop0", Boolean.TRUE);
            aso1.put("prop1", Boolean.FALSE);
            aso1.put("prop2", Boolean.FALSE);
            addToList(list, 0, aso1);

            ASObject aso2 = new ASObject();
            aso2.put("prop0", Boolean.FALSE);
            aso2.put("prop1", Boolean.FALSE);
            aso2.put("prop2", Boolean.FALSE);
            addToList(list, 1, aso2);

            ASObject aso3 = new ASObject();
            aso3.put("prop0", Boolean.TRUE);
            aso3.put("prop1", Boolean.TRUE);
            aso3.put("prop2", Boolean.FALSE);
            addToList(list, 2, aso3);

            body.setData(list);
            EXPECTED_VALUE = m;
        }
        return EXPECTED_VALUE;
    }

    public MessageException getExpectedException()
    {
        return null;
    }
}
