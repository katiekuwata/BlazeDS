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
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package features.validators.deserialization;

import java.util.HashSet;
import java.util.Set;

import flex.messaging.config.ConfigMap;
import flex.messaging.log.Log;
import flex.messaging.validators.DeserializationValidator;

/**
 * This is a sample deserialization validator. It does not perform any real assignment
 * and creation validations but instead, it keeps track of an internal set of class
 * types, and when the class type is first encountered, it logs out the type of the class.
 * One can use this validator to determine what class types are deserialized on the server.
 */
public class ClassLoggingDeserializationValidator implements DeserializationValidator
{
    //--------------------------------------------------------------------------
    //
    // Public Static Constants
    //
    //--------------------------------------------------------------------------

    public static String LOG_CATEGORY = "Endpoint.Deserialization.Creation";

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Instance level lock for thread-safe state changes.
     */
    protected final Object lock = new Object();

    /**
     * Keeps track of encountered class names.
     */
    protected final Set<String> classNames = new HashSet<String>();

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * No assignment validation; simply returns true.
     *
     * @param instance The Array or List instance.
     * @param index The index at which the value is being assigned.
     * @param value The value that is assigned to the index.
     * @return True if the assignment is valid.
     */
    public boolean validateAssignment(Object instance, int index, Object value)
    {
        return true;
    }

    /**
     * No assignment validation; simply returns true.
     *
     * @param instance The instance with the property that is being assigned a new value.
     * @param propertyName The name of the property that is being assigned.
     * @param value The value that the property is being assigned to.
     * @return True.
     */
    public boolean validateAssignment(Object instance, String propertyName, Object value)
    {
        return true;
    }

    /**
     * No creation validation; simply returns true, but when a class is
     * encountered the first time an Info level log message is printed
     * to the Endpoint.Deserialization.Creation category listing the class name.
     * Registering this validator in a development or test environment allows
     * all required types used by the application to be captured in the server log,
     * and this information may be used to configure a <tt>ClassDeserializationValidator</tt>
     * in the production environment that disallows creation of all non-required types.
     *
     * @param c The class that is being created.
     * @return True.
     */
    public boolean validateCreation(Class<?> c)
    {
        String className = c == null? null : c.getName();
        if (className != null)
        {
            synchronized(lock)
            {
                if (!classNames.contains(className))
                {
                    if (Log.isInfo())
                        Log.getLogger(LOG_CATEGORY).info(className);
                    classNames.add(className);
                }
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see flex.messaging.FlexConfigurable#initialize(java.lang.String, flex.messaging.config.ConfigMap)
     */
    public void initialize(String id, ConfigMap configMap)
    {
        // No-op.
    }
}