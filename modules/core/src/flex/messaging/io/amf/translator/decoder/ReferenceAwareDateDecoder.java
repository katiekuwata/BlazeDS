/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
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
package flex.messaging.io.amf.translator.decoder;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.TypeMarshallingContext;

/**
 * @author Peter Farland
 *
 * @exclude
 */
public class ReferenceAwareDateDecoder extends DateDecoder
{
    public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        Object result = super.decodeObject(shell, encodedObject, desiredClass);

        // Only AMF 3 Dates can be sent by reference so we only
        // need to remember this translation to re-establish pointers
        // to the encodedObject if the incoming type was a Date object.
        if (result != null
                && SerializationContext.getSerializationContext().supportDatesByReference
                && encodedObject instanceof java.util.Date)
        {
            TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
            context.getKnownObjects().put(encodedObject, result);
        }

        return result;
    }
}
