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
package flex.messaging.io.amfx.string_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.MessageException;

public class Confirm11e extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm11e()
    {
    }

    public ActionMessage getExpectedMessage()
    {
        if (EXPECTED_VALUE == null)
        {
            ActionMessage m = new ActionMessage();
            MessageBody body = new MessageBody();
            m.addBody(body);

            String s = "Locus classicus";
            Object list = createList(3);
            addToList(list, 0, s);
            addToList(list, 1, s);
            addToList(list, 2, s);

            body.setData(list);
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

        // Also check that by-reference serialization of dates restored
        // the pointers to the original date...
        Object list1 = o1;
        Object list2 = o2;

        String s11 = (String)getFromList(list1, 0);
        String s12 = (String)getFromList(list1, 1);
        String s13 = (String)getFromList(list1, 2);

        if (s11 != s12 || s12 != s13)
            return false;

        String s21 = (String)getFromList(list2, 0);
        String s22 = (String)getFromList(list2, 1);
        String s23 = (String)getFromList(list2, 2);

        if (s21 != s22 || s22 != s23)
            return false;

        return match;
    }
}
