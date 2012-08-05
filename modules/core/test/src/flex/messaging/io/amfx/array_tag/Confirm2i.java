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
import flex.messaging.MessageException;

import java.util.Map;
import java.util.HashMap;

public class Confirm2i extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm2i()
    {
    }

    public ActionMessage getExpectedMessage()
    {
        if (EXPECTED_VALUE == null)
        {
            ActionMessage m = new ActionMessage();
            MessageBody body = new MessageBody();
            m.addBody(body);

            Object list = createList(2);

            Map first = new HashMap();

            Map prop0 = new HashMap();
            prop0.put("subprop0", "Quark");
            first.put("prop0", prop0);

            Object auto0 = createList(2);
            addToList(auto0, 0, Boolean.TRUE);
            addToList(auto0, 1, Boolean.FALSE);
            first.put("0", auto0);

            addToList(list, 0, first);

            Object second = createList(2);
            addToList(second, 0, null);
            addToList(second, 1, prop0);
            addToList(list, 1, second);

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
