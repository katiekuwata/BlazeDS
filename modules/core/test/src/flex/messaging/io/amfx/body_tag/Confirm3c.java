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
package flex.messaging.io.amfx.body_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.ASObject;
import flex.messaging.MessageException;

public class Confirm3c extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm3c()
    {
    }

    public ActionMessage getExpectedMessage()
    {
        if (EXPECTED_VALUE == null)
        {
            ActionMessage m = new ActionMessage();

            MessageBody body = new MessageBody();
            body.setData("Sample Value");
            m.addBody(body);

            body = new MessageBody();
            ASObject aso = new ASObject();
            aso.put("prop0", new Double(Double.NEGATIVE_INFINITY));
            aso.put("prop1", new Double(Double.POSITIVE_INFINITY));
            body.setData(aso);
            m.addBody(body);

            body = new MessageBody();
            Object list = createList(2);
            addToList(list, 0, Boolean.FALSE);
            addToList(list, 1, Boolean.TRUE);
            body.setData(list);
            m.addBody(body);

            EXPECTED_VALUE = m;
        }
        return EXPECTED_VALUE;
    }

    public MessageException getExpectedException()
    {
        return null;
    }
}
