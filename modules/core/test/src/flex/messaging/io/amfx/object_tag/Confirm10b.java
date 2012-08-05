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

public class Confirm10b extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm10b()
    {
        ActionMessage m = new ActionMessage();
        MessageBody body = new MessageBody();
        m.addBody(body);

        ASObject aso = new ASObject();

        ASObject prop0 = new ASObject();
        prop0.put("subprop0", "200");
        prop0.put("subprop1", new Double(200.0));
        aso.put("prop0", prop0);

        ASObject prop1 = new ASObject();
        prop1.put("subprop0", "200");
        prop1.put("subprop1", new Double(200.0));
        aso.put("prop1", prop1);

        ASObject prop2 = new ASObject();

        ASObject subprop0 = new ASObject();
        subprop0.put("subprop0", "200");
        subprop0.put("subprop1", new Double(200.0));

        ASObject subprop1 = new ASObject();
        subprop1.put("subprop0", "200");
        subprop1.put("subprop1", new Double(200.0));
        prop2.put("subprop0", subprop0);
        prop2.put("subprop1", subprop1);

        aso.put("prop2", prop2);

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
