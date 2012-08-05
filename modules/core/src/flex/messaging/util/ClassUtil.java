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
package flex.messaging.util;

import flex.messaging.MessageException;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.SerializationException;
import flex.messaging.validators.DeserializationValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Utility class to create instances of Complex Types
 * and handle error conditions consistently across the RemoteObject
 * code base.
 *
 * @author Peter Farland
 * @exclude
 */
public class ClassUtil
{
    private static final int TYPE_NOT_FOUND = 10008;
    private static final int UNEXPECTED_TYPE = 10009;
    private static final int CANNOT_CREATE_TYPE = 10010;
    private static final int SECURITY_ERROR = 10011;
    private static final int UNKNOWN_ERROR = 10012;

    private static final String NULL = "null";

    private ClassUtil()
    {
    }

    public static Class createClass(String type)
    {
        return createClass(type, null);
    }

    public static Class createClass(String type, ClassLoader loader)
    {
        try
        {
            if (type != null)
                type = type.trim();

            if (loader == null) // will use the loader for this class
                return Class.forName(type);
            return Class.forName(type, true, loader);
        }
        catch (ClassNotFoundException cnf)
        {
            // Cannot invoke type '{type}'
            MessageException ex = new MessageException();
            ex.setMessage(TYPE_NOT_FOUND, new Object[] {type});
            ex.setDetails(TYPE_NOT_FOUND, "0", new Object[] {type});
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            throw ex;
        }
    }

    /**
     * Creates the default instance of the class and verifies that it matches
     * with the expected class type, if one passed in.
     *
     * @param cls The class to create.
     * @param expectedInstance The expected class type.
     * @return The default instance of the class.
     */
    public static Object createDefaultInstance(Class cls, Class expectedInstance)
    {
        return ClassUtil.createDefaultInstance(cls, expectedInstance, false /*validate*/);
    }

    /**
     * Creates the default instance of the class and verifies that it matches
     * with the expected class type, if one passed in. It also validates the creation
     * of the instance with the deserialization validator, if one exists.
     *
     * @param cls The class to create.
     * @param expectedInstance The expected class type.
     * @param validate Controls whether the creation of the instance is validated
     * with the deserialization validator.
     * @return The default instance of the class.
     */
    public static Object createDefaultInstance(Class cls, Class expectedInstance, boolean validate)
    {
        if (validate)
            validateCreation(cls);

        String type = cls.getName();
        try
        {
            Object instance = cls.newInstance();

            if (expectedInstance != null && !expectedInstance.isInstance(instance))
            {
                // Given type '{name}' is not of expected type '{expectedName}'.
                MessageException ex = new MessageException();
                ex.setMessage(UNEXPECTED_TYPE, new Object[] {instance.getClass().getName(), expectedInstance.getName()});
                ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
                throw ex;
            }

            return instance;
        }
        catch (IllegalAccessException ia)
        {
            boolean details = false;
            StringBuffer message = new StringBuffer("Unable to create a new instance of type ");
            message.append(type);

            //Look for a possible cause...

            // Class might not have a suitable constructor?
            if (!hasValidDefaultConstructor(cls))
            {
                details = true;
            }

            // Unable to create a new instance of type '{type}'.
            MessageException ex = new MessageException();
            ex.setMessage(CANNOT_CREATE_TYPE, new Object[] {type});
            if (details)
            {
                //Types must have a public, no arguments constructor
                ex.setDetails(CANNOT_CREATE_TYPE, "0");
            }
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            throw ex;
        }
        catch (InstantiationException ine)
        {
            String variant = null;

            //Look for a possible cause...

            if (cls.isInterface()) // Class is really an interface?
                variant = "1"; // Interfaces cannot be instantiated
            else if (isAbstract(cls))
                variant = "2"; //Abstract types cannot be instantiated.
            else if (!hasValidDefaultConstructor(cls)) // Class might not have a suitable constructor?
                variant = "3"; // Types cannot be instantiated without a public, no arguments constructor.

            MessageException ex = new MessageException();
            ex.setMessage(CANNOT_CREATE_TYPE, new Object[] {type});
            if (variant != null)
                ex.setDetails(CANNOT_CREATE_TYPE, variant);
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            throw ex;
        }
        catch (SecurityException se)
        {
            MessageException ex = new MessageException();
            ex.setMessage(SECURITY_ERROR, new Object[] {type});
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            ex.setRootCause(se);
            throw ex;
        }
        catch (MessageException me)
        {
            throw me;
        }
        catch (Exception e)
        {
            MessageException ex = new MessageException();
            ex.setMessage(UNKNOWN_ERROR, new Object[] {type});
            ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
            ex.setRootCause(e);
            throw ex;
        }
    }

    public static boolean isAbstract(Class cls)
    {
        try
        {
            if (cls != null)
            {
                int mod = cls.getModifiers();
                return Modifier.isAbstract(mod);
            }
        }
        catch (Throwable t)
        {
            return false;
        }

        return false;
    }

    public static boolean hasValidDefaultConstructor(Class cls)
    {
        try
        {
            if (cls != null)
            {
                Constructor c = cls.getConstructor(new Class[]{});
                int mod = c.getModifiers();
                return Modifier.isPublic(mod);
            }
        }
        catch (Throwable t)
        {
            return false;
        }

        return false;
    }

    public static String classLoaderToString(ClassLoader cl)
    {
        if (cl == null)
            return NULL;

        if (cl == ClassLoader.getSystemClassLoader())
            return "system";

        StringBuffer sb = new StringBuffer();
        sb.append("hashCode: " + System.identityHashCode(cl) + " (parent " + ClassUtil.classLoaderToString(cl.getParent()) + ")");
        return sb.toString();
    }

    /**
     * Validates the assignment of a value to an index of an Array or List instance
     * against the deserialization validator. If the assignment is not valid,
     * SerializationException is thrown.
     *
     * @param instance The Array or List instance.
     * @param index The index at which the value is being assigned.
     * @param value The value that is assigned to the index.
     * @throws SerializationException if the value assignment is not valid.
     */
    public static void validateAssignment(Object obj, int index, Object value)
    {
        SerializationContext context = SerializationContext.getSerializationContext();
        DeserializationValidator validator = context.getDeserializationValidator();
        if (validator == null)
            return;

        boolean valid = true;
        try
        {
            valid = validator.validateAssignment(obj, index, value);
        }
        catch (Exception e)
        {
            // Assignment validation of the object with type '{0}' for the index '{1}' failed.
            SerializationException se = new SerializationException();
            se.setMessage(10313, new Object[]{obj == null? NULL : obj.getClass().getName(), index});
            se.setRootCause(e);
            throw se;
        }
        if (!valid)
        {
            SerializationException se = new SerializationException();
            se.setMessage(10313, new Object[]{obj == null? NULL : obj.getClass().getName(), index});
            throw se;
        }
    }

    /**
     * Validates the assignment of a property of an instance of a class to a value
     * against the deserialization validator. If the assignment is not valid,
     * SerializationException is thrown.
     *
     * @param obj The class instance whose property is being assigned to a value.
     * @param propName The name of the property that is being assigned.
     * @param value The value that the property is being assigned to.
     * @throws SerializationException if the value assignment is not valid.
     */
    public static void validateAssignment(Object obj, String propName, Object value)
    {
        SerializationContext context = SerializationContext.getSerializationContext();
        DeserializationValidator validator = context.getDeserializationValidator();
        if (validator == null)
            return;

        boolean valid = true;
        try
        {
            valid = validator.validateAssignment(obj, propName, value);
        }
        catch (Exception e)
        {
            // Assignment validation of the object with type '{0}' for the property '{1}' failed.
            SerializationException se = new SerializationException();
            se.setMessage(10312, new Object[]{obj == null? NULL : obj.getClass().getName(), propName});
            se.setRootCause(e);
            throw se;
        }
        if (!valid)
        {
            SerializationException se = new SerializationException();
            se.setMessage(10312, new Object[]{obj == null? NULL : obj.getClass().getName(), propName});
            throw se;
        }
    }

    /**
     * Validates the creation of the class instance against the deserialization
     * validator, if one exists. If the class creation is not valid,
     * SerializationException is thrown.
     *
     * @param cls The class to validate.
     * @throws SerializationException if the class creation is not valid.
     */
    public static void validateCreation(Class<?> cls)
    {
        SerializationContext context = SerializationContext.getSerializationContext();
        DeserializationValidator validator = context.getDeserializationValidator();
        if (validator == null)
            return;

        boolean valid = true;
        try
        {
            valid = validator.validateCreation(cls);
        }
        catch (Exception e)
        {
            // Creation validation for class '{0}' failed.
            SerializationException se = new SerializationException();
            se.setMessage(10311, new Object[]{cls == null? NULL : cls.getName()});
            se.setRootCause(e);
            throw se;
        }
        if (!valid)
        {
            // Creation validation for class '{0}' failed.
            SerializationException se = new SerializationException();
            se.setMessage(10311, new Object[]{cls == null? NULL : cls.getName()});
            throw se;
        }
    }
}
