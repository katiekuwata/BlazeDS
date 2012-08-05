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
package flex.messaging.io.amfx.double_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.ASObject;
import flex.messaging.MessageException;

public class Confirm5d extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm5d()
    {
        ActionMessage m = new ActionMessage();
        MessageBody body = new MessageBody();
        m.addBody(body);

        ASObject aso = new ASObject();
        aso.put("prop0", new Double(-1.000001));
        aso.put("prop1", new Double(2.2));
        aso.put("prop2", new Double(-3));
        aso.put("prop3", new Double(4.040005));
        aso.put("prop4", new Double(-5.0505));
        aso.put("prop5", new Double(6.60005));
        aso.put("prop6", new Double(-7.755));
        aso.put("prop7", new Double(8.890));
        aso.put("prop8", new Double(-9.999999));
        aso.put("prop9", new Double(10.1010));

        body.setData(aso);
        EXPECTED_VALUE = m;
    }

    public ActionMessage getExpectedMessage()
    {
        return EXPECTED_VALUE;
    }

    public MessageException getExpectedException()
    {
        return null;
    }
}
