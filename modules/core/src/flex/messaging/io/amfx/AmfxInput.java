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
package flex.messaging.io.amfx;

import flex.messaging.io.AbstractProxy;
import flex.messaging.io.BeanProxy;
import flex.messaging.io.ClassAliasRegistry;
import flex.messaging.io.PropertyProxy;
import flex.messaging.io.PropertyProxyRegistry;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.SerializationException;
import flex.messaging.io.TypeMarshallingContext;
import flex.messaging.io.amf.AmfTrace;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.MessageHeader;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.Amf3Input;
import flex.messaging.io.ArrayCollection;
import flex.messaging.util.ClassUtil;
import flex.messaging.util.Hex;
import flex.messaging.util.XMLUtil;
import flex.messaging.MessageException;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.EmptyStackException;
import java.io.IOException;
import java.io.Externalizable;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;

import org.xml.sax.Attributes;

/**
 * Context for AMFX specific SAX handler.Contains start and end tag handlers for each of
 * the XML elements that occur in an AMFX request. The AmfxMessageDeserializer enforces 
 * a naming convention for these handlers of xyz_start for the start handler and xyz_end 
 * for the end handler of element xyz.
 *
 * Note that this context MUST be reset if reused between AMFX packet parsings.
 *
 * @see AmfxMessageDeserializer
 * @see AmfxOutput
 */
public class AmfxInput
{
    /**
     * This is the initial capacity that will be used for AMF arrays that have 
     * length greater than 1024.
     */
    public static final int INITIAL_ARRAY_CAPACITY = 1024;

    private SerializationContext context;
    private BeanProxy beanproxy = new BeanProxy();

    private final ArrayList objectTable;
    private final ArrayList stringTable;
    private final ArrayList traitsTable;

    private StringBuffer text;

    private ActionMessage message;
    private MessageHeader currentHeader;
    private MessageBody currentBody;
    private Stack objectStack;
    private Stack proxyStack;
    private Stack arrayPropertyStack;
    private Stack ecmaArrayIndexStack;
    private Stack strictArrayIndexStack;
    private Stack traitsStack;
    private boolean isStringReference;
    private boolean isTraitProperty;

    /*
     *  DEBUG LOGGING
     */
    protected boolean isDebug;
    protected AmfTrace trace;

    public AmfxInput(SerializationContext context)
    {
        this.context = context;

        stringTable = new ArrayList(64);
        objectTable = new ArrayList(64);
        traitsTable = new ArrayList(10);

        objectStack = new Stack();
        proxyStack = new Stack();
        arrayPropertyStack = new Stack();
        strictArrayIndexStack = new Stack();
        ecmaArrayIndexStack = new Stack();
        traitsStack = new Stack();

        text = new StringBuffer(32);
    }

    public void reset()
    {
        stringTable.clear();
        objectTable.clear();
        traitsTable.clear();
        objectStack.clear();
        proxyStack.clear();
        arrayPropertyStack.clear();
        traitsStack.clear();
        currentBody = null;
        currentHeader = null;

        TypeMarshallingContext marshallingContext = TypeMarshallingContext.getTypeMarshallingContext();
        marshallingContext.reset();
    }

    public void setDebugTrace(AmfTrace trace)
    {
        this.trace = trace;
        isDebug = this.trace != null;
    }

    public void setActionMessage(ActionMessage msg)
    {
        message = msg;
    }

    public Object readObject() throws IOException
    {
        return null;
    }


    // XML Considerations

    public void text(String s)
    {
        text.append(s);
    }


    //
    // AMFX Message Structure
    //

    public void start_amfx(Attributes attributes)
    {
        String ver = attributes.getValue("ver");
        int version = ActionMessage.CURRENT_VERSION;
        if (ver != null)
        {
            try
            {
                version = Integer.parseInt(ver);
            }
            catch (NumberFormatException ex)
            {
                throw new MessageException("Unknown version: " + ver);
            }
        }

        if (isDebug)
            trace.version(version);

        message.setVersion(version);
    }

    public void end_amfx()
    {
    }

    public void start_header(Attributes attributes)
    {
        if (currentHeader != null || currentBody != null)
            throw new MessageException("Unexpected header tag.");

        currentHeader = new MessageHeader();

        String name = attributes.getValue("name");
        currentHeader.setName(name);

        String mu = attributes.getValue("mustUnderstand");
        boolean mustUnderstand = false;
        if (mu != null)
        {
            mustUnderstand = Boolean.valueOf(mu).booleanValue();
            currentHeader.setMustUnderstand(mustUnderstand);
        }

        if (isDebug)
            trace.startHeader(name, mustUnderstand, message.getHeaderCount());
    }

    public void end_header()
    {
        message.addHeader(currentHeader);
        currentHeader = null;

        if (isDebug)
            trace.endHeader();
    }

    public void start_body(Attributes attributes)
    {
        if (currentBody != null || currentHeader != null)
            throw new MessageException("Unexpected body tag.");

        currentBody = new MessageBody();        

        if (isDebug)
            trace.startMessage("", "", message.getBodyCount());
    }

    public void end_body()
    {
        message.addBody(currentBody);
        currentBody = null;

        if (isDebug)
            trace.endMessage();
    }


    //
    // ActionScript Types
    //

    public void start_array(Attributes attributes)
    {
        int length = 10;
        String len = attributes.getValue("length");
        if (len != null)
        {
            try
            {
                len = len.trim();
                length = Integer.parseInt(len);
                if (length < 0)
                    throw new NumberFormatException();
            }
            catch (NumberFormatException ex)
            {
                throw new MessageException("Invalid array length: " + len);
            }
        }


        String ecma = attributes.getValue("ecma");
        boolean isECMA = "true".equalsIgnoreCase(ecma);

        Object array;
        boolean useListTemporarily = false;
        if (isECMA)
        {
            array = ClassUtil.createDefaultInstance(HashMap.class, null, true /*validate*/);
        }
        else
        {
            // Don't instantiate List/Array right away with the supplied size if it is more than
            // INITIAL_ARRAY_CAPACITY in case the supplied size has been tampered. This at least
            // requires the user to pass in the actual objects for the List/Array to grow beyond.
            if (context.legacyCollection || length > INITIAL_ARRAY_CAPACITY)
            {
                useListTemporarily = !context.legacyCollection;
                ClassUtil.validateCreation(ArrayList.class);
                int initialCapacity = length < INITIAL_ARRAY_CAPACITY? length : INITIAL_ARRAY_CAPACITY;
                array = new ArrayList(initialCapacity);
            }
            else
            {
                ClassUtil.validateCreation(Object[].class);
                array = new Object[length];
            }
        }
        
        setValue(array);

        ecmaArrayIndexStack.push(new int[]{0});
        strictArrayIndexStack.push(new int[]{0});

        objectTable.add(array);
        // Don't add the array to the object stack if the List is being used temporarily
        // for the length tampering detection. In that case, setValue method will add
        // an ObjectPropertyValueTuple to the object stack instead.
        if (!useListTemporarily)
            objectStack.push(array);
        proxyStack.push(null);

        if (isECMA)
        {
            if (isDebug)
                trace.startECMAArray(objectTable.size() - 1);
        }
        else
        {
            if (isDebug)
                trace.startAMFArray(objectTable.size() - 1);
        }
    }

    public void end_array()
    {
        try
        {
            Object obj = objectStack.pop();
            if (obj instanceof ObjectPropertyValueTuple)
            {
                // Means List was being used temporarily to guard against array length tampering.
                // Convert back to Object array and set it on the parent object using the proxy
                // and property saved in the tuple.
                ObjectPropertyValueTuple tuple = (ObjectPropertyValueTuple)obj;
                int objectId = objectTable.indexOf(tuple.value);
                Object newValue = ((ArrayList)tuple.value).toArray();
                objectTable.set(objectId, newValue);
                tuple.proxy.setValue(tuple.obj, tuple.property, newValue);
            }
            proxyStack.pop();
            ecmaArrayIndexStack.pop();
            strictArrayIndexStack.pop();
        }
        catch (EmptyStackException ex)
        {
            throw new MessageException("Unexpected end of array");
        }

        if (isDebug)
            trace.endAMFArray();
    }

    // <bytearray>010F0A</bytearray>

    public void start_bytearray(Attributes attributes)
    {
        text.delete(0, text.length());
    }

    public void end_bytearray()
    {
        ClassUtil.validateCreation(byte[].class);

        String bs = text.toString().trim();

        Hex.Decoder decoder = new Hex.Decoder();
        decoder.decode(bs);
        byte[] value = decoder.drain();

        setValue(value);

        if (isDebug)
            trace.startByteArray(objectTable.size() - 1, bs.length());
    }

    public void start_date(Attributes attributes)
    {
        text.delete(0, text.length());
    }

    public void end_date()
    {
        ClassUtil.validateCreation(Date.class);

        String d = text.toString().trim();
        try
        {
            long l = Long.parseLong(d);
            Date date = new Date(l);
            setValue(date);

            objectTable.add(date); //Dates can be sent by reference

            if (isDebug)
                trace.write(date);
        }
        catch (NumberFormatException ex)
        {
            throw new MessageException("Invalid date: " + d);
        }
    }

    public void start_double(Attributes attributes)
    {
        text.delete(0, text.length());
    }

    public void end_double()
    {
        ClassUtil.validateCreation(Double.class);

        String ds = text.toString().trim();
        try
        {
            Double d = Double.valueOf(ds);
            setValue(d);

            if (isDebug)
                trace.write(d.doubleValue());
        }
        catch (NumberFormatException ex)
        {
            throw new MessageException("Invalid double: " + ds);
        }
    }

    public void start_false(Attributes attributes)
    {
        ClassUtil.validateCreation(Boolean.class);
        setValue(Boolean.FALSE);
        if (isDebug)
            trace.write(false);
    }

    public void end_false()
    {
    }

    public void start_item(Attributes attributes)
    {
        String name = attributes.getValue("name");
        if (name != null)
        {
            name = name.trim();
            if (name.length() <= 0)
                throw new MessageException("Array item names cannot be the empty string.");

            char c = name.charAt(0);
            if (!(Character.isLetterOrDigit(c) || c == '_'))
                throw new MessageException("Invalid item name: " + name +
                        ". Array item names must start with a letter, a digit or the underscore '_' character.");
        }
        else
        {
            throw new MessageException("Array item must have a name attribute.");
        }

        //Check that we're expecting an ECMA array
        Object o = objectStackPeek();
        if (!(o instanceof Map))
        {
            throw new MessageException("Unexpected array item name: " + name +
                    ". Please set the ecma attribute to 'true'.");
        }

        arrayPropertyStack.push(name);
    }

    public void end_item()
    {
        arrayPropertyStack.pop();
    }

    public void start_int(Attributes attributes)
    {
        text.delete(0, text.length());
    }

    public void end_int()
    {
        ClassUtil.validateCreation(Integer.class);

        String is = text.toString().trim();
        try
        {
            Integer i = Integer.valueOf(is);
            setValue(i);

            if (isDebug)
                trace.write(i.intValue());
        }
        catch (NumberFormatException ex)
        {
            throw new MessageException("Invalid int: " + is);
        }
    }

    public void start_null(Attributes attributes)
    {
        setValue(null);

        if (isDebug)
            trace.writeNull();
    }

    public void end_null()
    {
    }

    // <object type="com.my.Class">

    public void start_object(Attributes attributes)
    {
        PropertyProxy proxy = null;

        String type = attributes.getValue("type");
        if (type != null)
        {
            type = type.trim();
        }

        Object object;

        if (type != null && type.length() > 0)
        {
            // Check for any registered class aliases
            String aliasedClass = ClassAliasRegistry.getRegistry().getClassName(type);
            if (aliasedClass != null)
                type = aliasedClass;

            if (type == null || type.length() == 0)
            {
                object = ClassUtil.createDefaultInstance(ASObject.class, null, true /*validate*/);
            }
            else if (type.startsWith(">")) // Handle [RemoteClass] (no server alias)
            {
                object = ClassUtil.createDefaultInstance(ASObject.class, null, true /*validate*/);
                ((ASObject)object).setType(type);
            }
            else if (context.instantiateTypes || type.startsWith("flex."))
            {
                object = getInstantiatedObject(type, proxy);
            }
            else
            {
                // Just return type info with an ASObject...
                object = ClassUtil.createDefaultInstance(ASObject.class, null, true /*validate*/);
                ((ASObject)object).setType(type);
            }
        }
        else
        {
            // TODO: QUESTION: Pete, Investigate why setValue for ASObject is delayed to endObject
            ClassUtil.validateCreation(ASObject.class);
            object = new ASObject(type);
        }

        if (proxy == null)
            proxy = PropertyProxyRegistry.getProxyAndRegister(object);

        objectStack.push(object);
        proxyStack.push(proxy);
        objectTable.add(object);

        if (isDebug)
            trace.startAMFObject(type, objectTable.size() - 1);
    }


    // </object>

    public void end_object()
    {
        if (!traitsStack.empty())
            traitsStack.pop();

        if (!objectStack.empty())
        {
            Object obj = objectStack.pop();
            PropertyProxy proxy = (PropertyProxy) proxyStack.pop();

            Object newObj = proxy == null ? obj : proxy.instanceComplete(obj);
            if (newObj != obj)
            {
                int i;
                // Find the index in the list of the old objct and replace it with
                // the new one.
                for (i = 0; i < objectTable.size(); i++)
                    if (objectTable.get(i) == obj)
                        break;

                if (i != objectTable.size())
                    objectTable.set(i, newObj);

                obj = newObj;
            }
            setValue(obj);
        }
        else
        {
            throw new MessageException("Unexpected end of object.");
        }

        if (isDebug)
            trace.endAMFObject();
    }

    public void start_ref(Attributes attributes)
    {
        String id = attributes.getValue("id");
        if (id != null)
        {
            try
            {
                int i = Integer.parseInt(id);
                Object o = objectTable.get(i);
                setValue(o);

                if (isDebug)
                    trace.writeRef(i);
            }
            catch (NumberFormatException ex)
            {
                throw new MessageException("Invalid object reference: " + id);
            }
            catch (IndexOutOfBoundsException ex)
            {
                throw new MessageException("Unknown object reference: " + id);
            }
        }
        else
        {
            throw new MessageException("Unknown object reference: " + id);
        }

    }

    public void end_ref()
    {
    }

    public void start_string(Attributes attributes)
    {
        String id = attributes.getValue("id");
        if (id != null)
        {
            isStringReference = true;

            try
            {
                int i = Integer.parseInt(id);
                String s = (String)stringTable.get(i);
                if (isTraitProperty)
                {
                    TraitsContext traitsContext = (TraitsContext)traitsStack.peek();
                    traitsContext.add(s);
                }
                else
                {
                    ClassUtil.validateCreation(String.class);
                    setValue(s);
                }
            }
            catch (NumberFormatException ex)
            {
                throw new MessageException("Invalid string reference: " + id);
            }
            catch (IndexOutOfBoundsException ex)
            {
                throw new MessageException("Unknown string reference: " + id);
            }
        }
        else
        {
            text.delete(0, text.length());
            isStringReference = false;
        }
    }

    public void end_string()
    {
        if (!isStringReference)
        {
            String s = text.toString();

            // Special case the empty string as it isn't counted as in
            // the string reference table
            if (s.length() > 0)
            {
                // Traits won't contain CDATA
                if (!isTraitProperty)
                    s = unescapeCloseCDATA(s);

                stringTable.add(s);
            }

            if (isTraitProperty)
            {
                TraitsContext traitsContext = (TraitsContext)traitsStack.peek();
                traitsContext.add(s);
            }
            else
            {
                ClassUtil.validateCreation(String.class);
                setValue(s);

                if (isDebug)
                    trace.writeString(s);
            }
        }
    }


    public void start_traits(Attributes attributes)
    {
        if (!objectStack.empty())
        {
            List traitsList = new ArrayList();
            TraitsContext traitsContext = new TraitsContext(traitsList);
            traitsStack.push(traitsContext);

            String id = attributes.getValue("id");
            if (id != null)
            {
                try
                {
                    int i = Integer.parseInt(id);
                    List l = (List)traitsTable.get(i);

                    Iterator it = l.iterator();
                    while (it.hasNext())
                    {
                        String prop = (String)it.next();
                        traitsList.add(prop);
                    }
                }
                catch (NumberFormatException ex)
                {
                    throw new MessageException("Invalid traits reference: " + id);
                }
                catch (IndexOutOfBoundsException ex)
                {
                    throw new MessageException("Unknown traits reference: " + id);
                }
            }
            else
            {
                boolean externalizable = false;

                String ext = attributes.getValue("externalizable");
                if (ext != null)
                {
                    externalizable = "true".equals(ext.trim());
                }

                Object obj = objectStackPeek();
                if (externalizable && !(obj instanceof Externalizable))
                {
                    //Class '{className}' must implement java.io.Externalizable to receive client IExternalizable instances.
                    SerializationException ex = new SerializationException();
                    ex.setMessage(10305, new Object[] {obj.getClass().getName()});
                    throw ex;
                }

                traitsTable.add(traitsList);
            }

            isTraitProperty = true;
        }
        else
        {
            throw new MessageException("Unexpected traits");
        }
    }

    public void end_traits()
    {
        isTraitProperty = false;
    }


    public void start_true(Attributes attributes)
    {
        ClassUtil.validateCreation(Boolean.class);

        setValue(Boolean.TRUE);

        if (isDebug)
            trace.write(true);
    }

    public void end_true()
    {
    }

    public void start_undefined(Attributes attributes)
    {
        setValue(null);

        if (isDebug)
            trace.writeUndefined();
    }

    public void end_undefined()
    {
    }

    public void start_xml(Attributes attributes)
    {
        text.delete(0, text.length());
    }

    public void end_xml()
    {
        String xml = text.toString();
        xml = unescapeCloseCDATA(xml);

        // Validation performed in XMLUtil#stringToDocument.
        Object value = XMLUtil.stringToDocument(xml, !(context.legacyXMLNamespaces));
        setValue(value);
    }

    private String unescapeCloseCDATA(String s)
    {
        //Only check if string could possibly have an encoded closing for a CDATA "]]>"
        if (s.length() > 5 && s.indexOf("]]&gt;") != -1)
        {
            s = s.replaceAll("]]&gt;", "]]>");
        }

        return s;
    }

    private void setValue(Object value)
    {
        if (objectStack.empty())
        {
            // Headers
            if (currentHeader != null)
            {
                currentHeader.setData(value);
            }

            // Body
            else if (currentBody  != null)
            {
                currentBody.setData(value);
            }

            else
            {
                throw new MessageException("Unexpected value: " + value);
            }

            return;
        }


        // ActionScript Data
        Object obj = objectStackPeek();

        // <object type="..."> <traits externalizable="true">
        if (obj instanceof Externalizable)
        {
            if (value != null && value.getClass().isArray() && Byte.TYPE.equals(value.getClass().getComponentType()))
            {
                Externalizable extern = (Externalizable)obj;
                Amf3Input objIn = new Amf3Input(context);
                byte[] ba = (byte[])value;
                ByteArrayInputStream baIn = new ByteArrayInputStream(ba);
                try
                {
                    //objIn.setDebugTrace(trace);
                    objIn.setInputStream(baIn);
                    extern.readExternal(objIn);
                }
                catch (ClassNotFoundException ex)
                {
                    throw new MessageException("Error while reading Externalizable class " + extern.getClass().getName(), ex);
                }
                catch (IOException ex)
                {
                    throw new MessageException("Error while reading Externalizable class " + extern.getClass().getName(), ex);
                }
                finally
                {
                    try
                    {
                        objIn.close();
                    }
                    catch (IOException ex)
                    {
                    }
                }
            }
            else
            {
                throw new MessageException("Error while reading Externalizable class. Value must be a byte array.");
            }
        }

        // <object>
        else if (obj instanceof ASObject)
        {
            String prop;

            TraitsContext traitsContext = (TraitsContext)traitsStack.peek();
            try
            {
                prop = traitsContext.next();
            }
            catch (IndexOutOfBoundsException ex)
            {
                throw new MessageException("Object has no trait info for value: " + value);
            }

            ASObject aso = (ASObject)obj;
            ClassUtil.validateAssignment(aso, prop, value);
            aso.put(prop, value);

            if (isDebug)
                trace.namedElement(prop);
        }

        // <array ecma="false"> in ArrayList form
        else if (obj instanceof ArrayList && !(obj instanceof ArrayCollection))
        {
            ArrayList list = (ArrayList)obj;
            ClassUtil.validateAssignment(list, list.size(), value);
            list.add(value);

            if (isDebug)
                trace.arrayElement(list.size() - 1);
        }

        // <array ecma="false"> in Object[] form
        else if (obj.getClass().isArray())
        {
            if (!strictArrayIndexStack.empty())
            {
                int[] indexObj = (int[])strictArrayIndexStack.peek();
                int index = indexObj[0];

                if (Array.getLength(obj) > index)
                {
                    ClassUtil.validateAssignment(obj, index, value);
                    Array.set(obj, index, value);
                }
                else
                {
                    throw new MessageException("Index out of bounds at: " + index + " cannot set array value: " + value + "");
                }
                indexObj[0]++;
            }
        }

        // <array ecma="true">
        else if (obj instanceof Map)
        {
            Map map = (Map)obj;

            // <item name="prop">
            if (!arrayPropertyStack.empty())
            {
                String prop = (String)arrayPropertyStack.peek();
                ClassUtil.validateAssignment(map, prop, value);
                map.put(prop, value);

                if (isDebug)
                    trace.namedElement(prop);

                return;
            }

            // Mixed content, auto-generate string for ECMA Array index
            if (!ecmaArrayIndexStack.empty())
            {
                int[] index = (int[])ecmaArrayIndexStack.peek();

                String prop = String.valueOf(index[0]);
                index[0]++;

                ClassUtil.validateAssignment(map, prop, value);
                map.put(prop, value);

                if (isDebug)
                    trace.namedElement(prop);
            }
        }

        // <object type="...">
        else
        {
            setObjectValue(obj, value);
        }
    }

    private void setObjectValue(Object obj, Object value)
    {
        String prop;

        TraitsContext traitsContext = (TraitsContext)traitsStack.peek();
        try
        {
            prop = traitsContext.next();
        }
        catch (IndexOutOfBoundsException ex)
        {
            throw new MessageException("Object has no trait info for value: " + value, ex);
        }

        try
        {
            // Then check if there's a more suitable proxy now that we have an instance
            PropertyProxy proxy = (PropertyProxy) proxyStack.peek();
            if (proxy == null)
                proxy = beanproxy;
            proxy.setValue(obj, prop, value);
            if (value instanceof ArrayList && !(value instanceof ArrayCollection)
                    && !context.legacyCollection)
            {
                // Means List is being used temporarily, see start_array method for explanation.
                objectStack.push(new ObjectPropertyValueTuple(proxy, obj, prop, value));
            }
        }
        catch (Exception ex)
        {
            throw new MessageException("Failed to set property '" + prop + "' with value: " + value, ex);
        }


        if (isDebug)
            trace.namedElement(prop);
    }

    /**
     * Utility method to peek the object in the object stack which can be an Object
     * or an <tt>ObjectPropertyValueTuple</tt>.
     * 
     * @return The Object at the top of the object stack.
     */
    private Object objectStackPeek()
    {
        Object obj = objectStack.peek();
        return (obj instanceof ObjectPropertyValueTuple)? ((ObjectPropertyValueTuple)obj).value : obj;
    }

    private Object getInstantiatedObject(String className, PropertyProxy proxy)
    {
        Class<?> desiredClass = null;
        try
        {
            desiredClass = AbstractProxy.getClassFromClassName(className);
        }
        catch (MessageException me)
        {
            // Type not found but don't mind using ASObject for the missing type.
            if (me.getCode().startsWith(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE)
                    && context.createASObjectForMissingType)
            {
                ASObject object = (ASObject)ClassUtil.createDefaultInstance(ASObject.class, null, true /*validate*/);
                object.setType(className);
                return object;
            }
            throw me; // Rethrow.
        }

        // Type exists.
        proxy = PropertyProxyRegistry.getRegistry().getProxyAndRegister(desiredClass);
        return proxy == null? ClassUtil.createDefaultInstance(desiredClass, null, true /*validate*/) :
                    proxy.createInstance(className); // Validation is performed in the proxy.
    }

    /**
     * Helper class used in the case where the supplied array length is more than the
     * INITIAL_ARRAY_CAPACITY. In that case, the List/Object[] on the server is not 
     * initialized with that length in case the supplied length has been tampered. 
     * Instead, a temporary List of length INITIAL_ARRAY_CAPACITY is constructed and List
     * grows as array members are supplied from the client. This way the user is required to
     * pass in the actual array members for the List to grow. This helper class is needed to
     * convert the temporary List into Object[] if needed.
     */
    private static class ObjectPropertyValueTuple
    {
        private PropertyProxy proxy;
        private Object obj;
        private String property;
        private Object value;

        private ObjectPropertyValueTuple(PropertyProxy proxy, Object obj, String property, Object value)
        {
            this.proxy = proxy;
            this.obj = obj;
            this.property = property;
            this.value = value;
        }
    }

    private class TraitsContext
    {
        private List traits;
        private int counter;

        private TraitsContext(List traits)
        {
            this.traits = traits;
        }

        private void add(String trait)
        {
            trait = trait.trim();

            if (trait.length() <= 0)
                throw new MessageException("Traits cannot be the empty string.");

            char c = trait.charAt(0);
            if (!(Character.isLetterOrDigit(c) || c == '_'))
                throw new MessageException("Invalid trait name: " + trait +
                        ". Object property names must start with a letter, a digit or the underscore '_' character.");


            traits.add(trait);
        }

        private String next()
        {
            String trait = (String)traits.get(counter);
            counter++;
            return trait;
        }
    }
}
