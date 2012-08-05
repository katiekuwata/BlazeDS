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

import java.util.Date;

public class Confirm2c extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm2c()
    {
    }

    public ActionMessage getExpectedMessage()
    {
        if (EXPECTED_VALUE == null)
        {
            ActionMessage m = new ActionMessage();
            MessageBody body = new MessageBody();
            m.addBody(body);

            Object list = createList(8);
            addToList(list, 0, new Date(1119647239994L));
            addToList(list, 1, new Integer(-2000));
            addToList(list, 2, new Double(1000000));
            addToList(list, 3, "Macromedia");
            addToList(list, 4, null);
            addToList(list, 5, null);
            addToList(list, 6, Boolean.TRUE);
            addToList(list, 7, Boolean.FALSE);

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
