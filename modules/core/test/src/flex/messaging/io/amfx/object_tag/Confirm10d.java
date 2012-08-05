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
import flex.messaging.io.amfx.testtypes.Bleu_dAuvergne;
import flex.messaging.io.amfx.testtypes.Roquefort;
import flex.messaging.io.amfx.testtypes.Cheese;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.MessageException;

public class Confirm10d extends DeserializationConfirmation
{
    private ActionMessage EXPECTED_VALUE;

    public Confirm10d()
    {
        ActionMessage m = new ActionMessage();
        MessageBody body = new MessageBody();
        m.addBody(body);

        Bleu_dAuvergne b = new Bleu_dAuvergne();
        Roquefort r = new Roquefort();

        b.rating = 3.5;
        b.setParing(r);

        r.rating = 4.5;
        r.setParing(b);

        body.setData(b);
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

    protected boolean bodyValuesMatch(Object o1, Object o2)
    {
        if (!(o1 instanceof Bleu_dAuvergne) || !(o2 instanceof Bleu_dAuvergne))
        {
            return false;
        }

        Bleu_dAuvergne b1 = (Bleu_dAuvergne)o1;
        Bleu_dAuvergne b2 = (Bleu_dAuvergne)o2;

        if (!b1.getName().equals(b2.getName()))
            return false;

        if (!b1.getRegion().equals(b2.getRegion()))
            return false;

        if (b1.rating != b2.rating || b1.rating != 3.5 || b2.rating != 3.5)
            return false;

        Cheese p1 = b1.getParing();
        Cheese p2 = b2.getParing();

        if (!(p1 instanceof Roquefort) || !(p2 instanceof Roquefort))
            return false;

        Roquefort r1 = (Roquefort)p1;
        Roquefort r2 = (Roquefort)p2;

        if (r1.getParing() != b1 || r2.getParing() != b2)
            return false;

        return true;
    }
}
