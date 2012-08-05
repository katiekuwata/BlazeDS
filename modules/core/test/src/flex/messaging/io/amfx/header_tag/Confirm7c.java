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
package flex.messaging.io.amfx.header_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageHeader;
import flex.messaging.io.amf.ASObject;
import flex.messaging.MessageException;

public class Confirm7c extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm7c()
    {
    }

    public ActionMessage getExpectedMessage()
    {
        if (EXPECTED_VALUE == null)
        {
            ActionMessage m = new ActionMessage();

            MessageHeader header = new MessageHeader();
            header.setName("Sample Header");
            header.setData("Sample value.");
            m.addHeader(header);

            header = new MessageHeader();
            header.setName("Another Header");
            header.setMustUnderstand(false);
            ASObject aso = new ASObject();
            aso.put("prop0", "Another sample value.");
            aso.put("prop1", new Double(400.05));
            header.setData(aso);
            m.addHeader(header);

            header = new MessageHeader();
            header.setName("Yet Another Header");
            header.setMustUnderstand(true);
            Object list = createList(3);
            addToList(list, 0, new Integer(-10));
            addToList(list, 1, new Integer(0));
            addToList(list, 2, new Integer(10));
            header.setData(list);
            m.addHeader(header);

            EXPECTED_VALUE = m;
        }
        return EXPECTED_VALUE;
    }

    public MessageException getExpectedException()
    {
        return null;
    }
}
