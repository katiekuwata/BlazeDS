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

/**
 * Decode an ActionScript enumeration object (generally a string) to a Java enum.
 * @exclude
 */
public class EnumDecoder extends ActionScriptDecoder
{
    /**
     * Does this type have a placeholder shell?
     * True for Enumerations.
     */
    @Override
    public boolean hasShell()
    {
        return true;
    }

    /**
     * Create the enumeration object based on the string
     *
     * @param encodedObject the object
     * @param desiredClass ignored
     */
    @Override
    public Object createShell(Object encodedObject, Class desiredClass)
    {
        if (encodedObject instanceof Enum)
            return encodedObject;

        if (encodedObject == null)
            return null;

        @SuppressWarnings("unchecked")
        Enum value = Enum.valueOf(desiredClass, encodedObject.toString());
        return value;
    }

    /**
     * Decode an object.
     * For the enum type, the createShell has already done the work, so we just
     * return the shell itself.
     */
    @Override
    public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
    {
        return (shell == null || encodedObject == null)? null : shell;
    }
}