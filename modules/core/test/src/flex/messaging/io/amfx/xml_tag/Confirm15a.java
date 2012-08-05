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
package flex.messaging.io.amfx.xml_tag;

import flex.messaging.io.amfx.DeserializationConfirmation;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.util.XMLUtil;
import flex.messaging.MessageException;

public class Confirm15a extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm15a()
    {
        ActionMessage m = new ActionMessage();
        MessageBody body = new MessageBody();
        m.addBody(body);

        try
        {
            StringBuffer xml = new StringBuffer(512);
            xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n");
            xml.append("<stock>");
            xml.append("    <item id=\"5\">");
            xml.append("        <discontinued />");
            xml.append("    </item>");
            xml.append("</stock>");

            Object data = XMLUtil.stringToDocument(xml.toString());
            body.setData(data);
        }
        catch (Throwable t)
        {
            throw new MessageException("Error creating expected message for test 15a: " + t.getMessage());
        }

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
