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
package flex.messaging.validators;

import flex.messaging.FlexConfigurable;

/**
 * Deserialization validator is registered with the Message broker and provide the
 * opportunity to validate the creation of classes and assignment of a property
 * of an instance to a value for incoming (client-to-server) deserialization.
 */
public interface DeserializationValidator extends FlexConfigurable
{

    /**
     * Validate the assignment of a value to an index of an Array or List instance.
     *
     * @param instance The Array or List instance.
     * @param index The index at which the value is being assigned.
     * @param value The value that is assigned to the index.
     * @return True if the assignment is valid.
     */
    boolean validateAssignment(Object instance, int index, Object value);

    /**
     * Validate the assignment of a property of an instance to a value.
     *
     * @param instance The instance with the property that is being assigned a new value.
     * @param propertyName The name of the property that is being assigned.
     * @param value The value that the property is being assigned to.
     * @return True if the assignment is valid.
     */
    boolean validateAssignment(Object instance, String propertyName, Object value);

    /**
     * Validate creation of a class.
     *
     * @param c The class that is being created.
     * @return True if the creation is valid.
     */
    boolean validateCreation(Class<?> c);
}
