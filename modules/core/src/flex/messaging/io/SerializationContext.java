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
package flex.messaging.io;

import flex.messaging.util.ClassUtil;
import flex.messaging.validators.DeserializationValidator;

import java.io.Serializable;

/**
 * A simple context to get settings from an endpoint to a deserializer
 * or serializer.
 *
 * @author Peter Farland
 */
public class SerializationContext implements Serializable, Cloneable
{
    static final long serialVersionUID = -3020985035377116475L;

    // Endpoint serialization configuration flags
    public boolean legacyXMLDocument;
    public boolean legacyXMLNamespaces;
    public boolean legacyCollection;
    public boolean legacyMap;
    public boolean legacyThrowable;
    public boolean legacyBigNumbers;
    public boolean legacyExternalizable;
    public boolean restoreReferences;
    public boolean supportRemoteClass;
    public boolean supportDatesByReference; // Typically used by AMF Version 3 requests

    /**
     * Determines whether an ASObject is created by default for a type that is
     * missing on the server, instead of throwing a server resource not found
     * exception.
     */
    public boolean createASObjectForMissingType = false;

    /**
     * Provides a way to control whether small messages should be sent even
     * if the client can support them. If set to false, small messages
     * will not be sent.
     *
     * The default is true.
     */
    public boolean enableSmallMessages = true;

    /**
     * Determines whether type information will be used to instantiate a new instance.
     * If set to false, types will be deserialized as flex.messaging.io.ASObject instances
     * with type information retained but not used to create an instance.
     *
     * Note that types in the flex.* package (and any subpackage) will always be
     * instantiated.
     *
     * The default is true.
     */
    public boolean instantiateTypes = true;
    public boolean ignorePropertyErrors = true;
    public boolean logPropertyErrors = false;
    public boolean includeReadOnly = false;

    private Class deserializer;
    private Class serializer;
    private DeserializationValidator deserializationValidator;

    /**
     * The default constructor.
     */
    public SerializationContext()
    {
    }

    /**
     * Returns the deserializer class.
     *
     * @return The deserializer class.
     */
    public Class getDeserializerClass()
    {
        return deserializer;
    }

    /**
     * Sets the deserializer class.
     *
     * @param c The deserializer class.
     */
    public void setDeserializerClass(Class c)
    {
        deserializer = c;
    }

    /**
     * Returns the serializer class.
     *
     * @return The serializer class.
     */
    public Class getSerializerClass()
    {
        return serializer;
    }

    /**
     * Sets the serializer class.
     *
     * @param c The serializer class.
     */
    public void setSerializerClass(Class c)
    {
        serializer = c;
    }

    /**
     * Instantiates a new message deserializer.
     *
     * @return A new message deserializer instance.
     */
    public MessageDeserializer newMessageDeserializer()
    {
        MessageDeserializer deserializer = (MessageDeserializer)ClassUtil.createDefaultInstance(getDeserializerClass(), MessageDeserializer.class);
        return deserializer;
    }

    /**
     * Instantiates a new message serializer.
     *
     * @return A new message serializer instance.
     */
    public MessageSerializer newMessageSerializer()
    {
        MessageSerializer serializer = (MessageSerializer)ClassUtil.createDefaultInstance(getSerializerClass(), MessageSerializer.class);
        return serializer;
    }

    /**
     * Returns the deserialization validator.
     *
     * @return The deserialization validator.
     */
    public DeserializationValidator getDeserializationValidator()
    {
        return deserializationValidator;
    }

    /**
     * Sets the deserialization validator.
     *
     * @param deserializationValidator The deserialization validator.
     */
    public void setDeserializationValidator(DeserializationValidator deserializationValidator)
    {
        this.deserializationValidator = deserializationValidator;
    }

    @Override
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            // this should never happen since this class extends object
            // but just in case revert to manual clone
            SerializationContext context = new SerializationContext();
            context.createASObjectForMissingType = createASObjectForMissingType;
            context.legacyXMLDocument = legacyXMLDocument;
            context.legacyXMLNamespaces = legacyXMLNamespaces;
            context.legacyCollection = legacyCollection;
            context.legacyMap = legacyMap;
            context.legacyThrowable = legacyThrowable;
            context.legacyBigNumbers = legacyBigNumbers;
            context.legacyExternalizable = legacyExternalizable;
            context.restoreReferences = restoreReferences;
            context.supportRemoteClass = supportRemoteClass;
            context.supportDatesByReference = supportDatesByReference; // Typically used by AMF Version 3 requests
            context.instantiateTypes = instantiateTypes;
            context.ignorePropertyErrors = ignorePropertyErrors;
            context.includeReadOnly = includeReadOnly;
            context.logPropertyErrors = logPropertyErrors;
            context.deserializer = deserializer;
            context.serializer = serializer;
            context.deserializationValidator = deserializationValidator;
            return context;
        }

    }

    private static ThreadLocal<SerializationContext> contexts = new ThreadLocal<SerializationContext>();

    /**
     * Establishes a SerializationContext for the current thread.
     * Users are not expected to call this function.
     * @param context The current SerializationContext.
     */
    public static void setSerializationContext(SerializationContext context)
    {
        if (context == null)
            contexts.remove();
        else
            contexts.set(context);
    }

    /**
     * @return The current thread's SerializationContext.
     */
    public static SerializationContext getSerializationContext()
    {
        SerializationContext sc = contexts.get();
        if (sc == null)
        {
            sc = new SerializationContext();
            SerializationContext.setSerializationContext(sc);
        }
        return sc;
    }
    /**
     * Clears out the thread local state after the request completes.
     */
    public static void clearThreadLocalObjects()
    {
        contexts.remove();
    }

    /**
     * @exclude
     * Create thread local storage.
     */
    public static void createThreadLocalObjects()
    {
        if (contexts == null)
            contexts = new ThreadLocal();
    }

    /**
     * @exclude
     * Destroy thread local storage.
     * Call ONLY on shutdown.
     */
    public static void releaseThreadLocalObjects()
    {
        contexts = null;
    }

}
