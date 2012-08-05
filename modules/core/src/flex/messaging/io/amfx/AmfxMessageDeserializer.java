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

import flex.messaging.io.MessageDeserializer;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfTrace;
import flex.messaging.io.SerializationContext;
import flex.messaging.MessageException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * SAX based AMFX Parser.
 *
 * @author Peter Farland
 */
public class AmfxMessageDeserializer extends DefaultHandler implements MessageDeserializer
{
    protected InputStream in;

    protected Locator locator;

    protected AmfxInput amfxIn;

    /*
     *  DEBUG LOGGING
     */
    protected AmfTrace debugTrace;
    protected boolean isDebug;

    public AmfxMessageDeserializer()
    {
    }

    /**
     * Establishes the context for reading in data from the given InputStream.
     * A null value can be passed for the trace parameter if a record of the
     * AMFX data should not be made.
     */
    public void initialize(SerializationContext context, InputStream in, AmfTrace trace)
    {
        amfxIn = new AmfxInput(context);
        this.in = in;

        debugTrace = trace;
        isDebug = debugTrace != null;

        if (debugTrace != null)
            amfxIn.setDebugTrace(debugTrace);
    }

    public void setSerializationContext(SerializationContext context)
    {
        amfxIn = new AmfxInput(context);
    }

    public void readMessage(ActionMessage m, ActionContext context) throws IOException
    {
        if (isDebug)
            debugTrace.startRequest("Deserializing AMFX/HTTP request");

        amfxIn.reset();
        amfxIn.setDebugTrace(debugTrace);
        amfxIn.setActionMessage(m);

        parse(m);

        context.setVersion(m.getVersion());
    }

    public Object readObject() throws ClassNotFoundException, IOException
    {
        return amfxIn.readObject();
    }

    protected void parse(ActionMessage m)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(in, this);
        }
        catch (MessageException ex)
        {
            clientMessageEncodingException(m, ex);
        }
        catch (SAXParseException e)
        {
            if (e.getException() != null)
            {
                clientMessageEncodingException(m, e.getException());
            }
            else
            {
                clientMessageEncodingException(m, e);
            }
        }
        catch (Exception ex)
        {
            clientMessageEncodingException(m, ex);
        }
    }

    /**
     * Implement {@link org.xml.sax.EntityResolver#resolveEntity(String, String)}.
     * 
     * AMFX does not need or use external entities, so disallow external entities
     * to prevent external entity injection attacks. 
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
    {
        throw new MessageException("External entities are not allowed");
    }
     
    protected void clientMessageEncodingException(ActionMessage m, Throwable t)
    {
        MessageException me;
        if (t instanceof MessageException)
        {
            me = (MessageException)t;
        }
        else
        {
            me = new MessageException("Error occurred parsing AMFX: " + t.getMessage());
        }

        me.setCode("Client.Message.Encoding");
        throw me;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        try
        {
            String methodName = "start_" + localName;
            Method method = amfxIn.getClass().getMethod(methodName, attribArr);
            method.invoke(amfxIn, new Object[]{attributes});
        }
        catch (NoSuchMethodException e)
        {
            fatalError(new SAXParseException("Unknown type: " + qName, locator));
        }
        catch (IllegalAccessException e)
        {
            fatalError(new SAXParseException(e.getMessage(), locator, e));
        }
        catch (InvocationTargetException e)
        {
            Throwable t = e.getTargetException();
            if (t instanceof SAXException)
            {
                throw (SAXException)t;
            }
            else if (t instanceof Exception)
            {
                fatalError(new SAXParseException(t.getMessage(), locator, (Exception)t));
            }
            else
            {
                fatalError(new SAXParseException(e.getMessage(), locator, e));
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        try
        {
            String methodName = "end_" + localName;
            Method method = amfxIn.getClass().getMethod(methodName, new Class[]{});
            method.invoke(amfxIn, new Object[]{});
        }
        catch (NoSuchMethodException e)
        {
            fatalError(new SAXParseException("Unfinished type: " + qName, locator));
        }
        catch (IllegalAccessException e)
        {
            fatalError(new SAXParseException(e.getMessage(), locator, e));
        }
        catch (InvocationTargetException e)
        {
            Throwable t = e.getTargetException();
            if (t instanceof SAXException)
            {
                throw (SAXException)t;
            }
            else if (t instanceof Error)
            {
                throw (Error)t;
            }
            else
            {
                fatalError(new SAXParseException(t.getMessage(), locator));
            }
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException
    {
        String chars = new String(ch, start, length);
        if (chars.length() > 0)
        {
            amfxIn.text(chars);
        }
    }

    public void setDocumentLocator(Locator l)
    {
        locator = l;
    }


    public void error(SAXParseException exception) throws SAXException
    {
        throw new MessageException(exception.getMessage());
    }

    public void fatalError(SAXParseException exception) throws SAXException
    {
        if ((exception.getException() != null) && (exception.getException() instanceof MessageException))
            throw (MessageException)exception.getException();
        throw new MessageException(exception.getMessage());
    }

    public void warning(SAXParseException exception) throws SAXException
    {
        throw new MessageException(exception.getMessage());
    }

    private static Class[] attribArr = new Class[]{Attributes.class};
}
