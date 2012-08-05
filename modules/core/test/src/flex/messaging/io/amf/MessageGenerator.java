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

package flex.messaging.io.amf;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import flex.messaging.io.SerializationContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Test programs use this class to simulate Flash Player AMF requests.
 * Simply call <code>parse(String)</code> with the path to an AMF request sample XML file (but
 * be sure to set a DataOutputStream first!).
 * <p>
 * This class, which extends DataOutput, interprets the XML and writes an AMF request to the
 * underlying DataOutputStream.
 * </p>
 * @author Peter Farland
 */
public class MessageGenerator extends Amf0Output
{
    private DocumentBuilder docBuilder;

    public MessageGenerator()
    {
        super(new SerializationContext());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringComments(true);
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try
        {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex)
        {
            throw new RuntimeException(ex);
        }
    }


    public void parse(String path)
    {
        parse(new File(path));
    }

    public void parse(File file)
    {
        try
        {
            Document doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();
            parseAmf(doc);
        }
        catch (TransformerException ex)
        {
            throw new RuntimeException(ex);
        }
        catch (SAXException ex)
        {
            throw new RuntimeException(ex);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }


    private void parseAmf(Document doc) throws TransformerException, IOException
    {
        CachedXPathAPI xpath = new CachedXPathAPI();
        Node root = xpath.selectSingleNode(doc, "/amf-request");

        if (root != null)
        {
            // messages
            NodeList list = xpath.selectNodeList(root, "message");
            for (int i = 0; i < list.getLength(); i++)
            {
                Node message = list.item(i);
                message(xpath, message);
            }
        }
    }

    private void message(CachedXPathAPI xpath, Node message) throws TransformerException, IOException
    {
        int version = (int)xpath.eval(message, "version").num();
        out.writeShort(version);

        // Headers
        Node headers = xpath.selectSingleNode(message, "headers");
        int headerCount = (int)xpath.eval(headers, "@count").num();
        out.writeShort(headerCount);

        NodeList list = xpath.selectNodeList(headers, "header");
        for (int i = 0; i < headerCount; i++)
        {
            Node header = list.item(i);
            if (header != null)
            {
                header(xpath, header, i);
            }
            else
            {
                throw new RuntimeException("Missing header(s). Specified " + headerCount + ", found " + (i - 1));
            }
        }

        // Bodies
        Node bodies = xpath.selectSingleNode(message, "bodies");
        int bodyCount = (int)xpath.eval(bodies, "@count").num();
        out.writeShort(bodyCount);

        list = xpath.selectNodeList(bodies, "body");
        for (int i = 0; i < bodyCount; i++)
        {
            Node body = list.item(i);
            if (body != null)
            {
                body(xpath, body, i);
            }
            else
            {
                throw new RuntimeException("Missing body(ies). Specified " + bodyCount + ", found " + (i - 1));
            }
        }
    }

    private void header(CachedXPathAPI xpath, Node header, int i) throws TransformerException, IOException
    {
        String name = xpath.eval(header, "@name").toString();
        boolean mustUnderstand = xpath.eval(header, "@mustUnderstand").bool();

        if (isDebug)
            trace.startHeader(name, mustUnderstand, i);

        out.writeUTF(name);
        out.writeBoolean(mustUnderstand);

        //int length = (int)xpath.eval(body, "@length").num();
        out.writeInt(-1); //Specify unknown content length

        reset();

        Node data = xpath.selectSingleNode(header, "*"); // Only one data item can be sent as the body...
        Object value = value(xpath, data);
        writeObject(value);

         if (isDebug)
            trace.endHeader();
    }

    private void body(CachedXPathAPI xpath, Node body, int i) throws TransformerException, IOException
    {
        String targetUri = xpath.eval(body, "@targetUri").toString();
        String responseUri = xpath.eval(body, "@responseUri").toString();

        if (isDebug)
            trace.startMessage(targetUri, responseUri, i);

        out.writeUTF(targetUri);
        out.writeUTF(responseUri);

        //int length = (int)xpath.eval(body, "@length").num();
        out.writeInt(-1); //Specify unknown content length

        reset();

        Node data = xpath.selectSingleNode(body, "*"); // Only one data item can be sent as the body...
        Object value = value(xpath, data);
        writeObject(value);

        if (isDebug)
           trace.endMessage();
    }

    private Object value(CachedXPathAPI xpath, Node node) throws TransformerException, IOException
    {
        String type = node.getNodeName();
        String value = xpath.eval(node, ".").toString();

        if (value == null)
        {
            return null;
        }

        if (avmPlus)
        {
            if ("string".equals(type))
            {
                return value;
            }
            else if ("boolean".equals(type))
            {
                return new Boolean(value.trim());
            }
            else if ("integer".equals(type))
            {
                return new Integer(value.trim());
            }
            else if ("double".equals(type))
            {
                return new Double(value.trim());
            }
            else if ("array".equals(type))
            {
                List array = new ArrayList();

                int count = (int)xpath.eval(node, "@count").num();

                NodeList list = xpath.selectNodeList(node, "*");
                for (int i = 0; i < count; i++)
                {
                    Node item = list.item(i);
                    if (item != null)
                    {
                        array.add(value(xpath, item));
                    }
                    else
                    {
                        throw new RuntimeException("Missing array item(s). Specified " + count + ", found " + (i - 1));
                    }
                }

                return array;
            }
            else if ("object".equals(type))
            {
                ASObject object = new ASObject();

                NodeList list = xpath.selectNodeList(node, "*[not(self::property)]");

                List traitProperties = null;

                // TRAIT PROPERTIES
                for (int i = 0; i < list.getLength(); i++)
                {
                    if (i == 0)
                    {
                        Node traits = list.item(i);

                        String className = xpath.eval(traits, "@classname").toString().trim();
                        int count = (int)xpath.eval(traits, "@count").num();
                        // boolean dynamic = xpath.eval(traits, "@dynamic").bool();

                        traitProperties = new ArrayList(count);

                        if (className.length() > 0)
                        {
                            object.setType(className);
                        }

                        NodeList propList = xpath.selectNodeList(traits, "property");
                        for (int p = 0; p < count; p++)
                        {
                            Node prop = propList.item(p);
                            if (prop != null )
                            {
                                String propName = xpath.eval(prop, "@name").toString().trim();
                                traitProperties.add(propName);
                            }
                            else
                            {
                                throw new RuntimeException("Missing trait property(ies). Specified " + count + ", found " + (p - 1));
                            }
                        }
                    }
                    else
                    {
                        Node prop = list.item(i);
                        String propName = (String)traitProperties.get(i - 1);
                        object.put(propName, value(xpath, prop));
                    }
                }

                // DYNAMIC PROPERTIES
                list = xpath.selectNodeList(node, "property");
                for (int i = 0; i < list.getLength(); i++)
                {
                    Node prop = list.item(i);
                    String propName = xpath.eval(prop, "@name").toString();
                    Node propValue = xpath.selectSingleNode(prop, "*");
                    object.put(propName, value(xpath, propValue));
                }


                return object;
            }
            else if ("xml".equals(type))
            {
                return value;
            }
        }
        else
        {
            if ("avmplus".equals(type))
            {
                setAvmPlus(true);
                Node data = xpath.selectSingleNode(node, "*"); // Only one data item can be sent as the body...
                return value(xpath, data);
            }
            else if ("string".equals(type))
            {
                return value;
            }
            else if ("boolean".equals(type))
            {
                return new Boolean(value.trim());
            }
            else if ("number".equals(type))
            {
                return new Double(value.trim());
            }
            else if ("array".equals(type))
            {
                List array = new ArrayList();

                int count = (int)xpath.eval(node, "@count").num();

                NodeList list = xpath.selectNodeList(node, "*");
                for (int i = 0; i < count; i++)
                {
                    Node item = list.item(i);
                    if (item != null)
                    {
                        array.add(value(xpath, item));
                    }
                    else
                    {
                        throw new RuntimeException("Missing array item(s). Specified " + count + ", found " + (i - 1));
                    }
                }

                return array;
            }
            else if ("object".equals(type))
            {
                ASObject object = new ASObject();
                String className = xpath.eval(node, "@classname").toString().trim();

                if (className.length() > 0)
                {
                    object.setType(className);
                }

                NodeList list = xpath.selectNodeList(node, "property");
                for (int i = 0; i < list.getLength(); i++)
                {
                    Node prop = list.item(i);
                    String propName = xpath.eval(prop, "@name").toString().trim();
                    object.put(propName, value(xpath, prop));
                }

                return object;
            }
            else if ("xml".equals(type))
            {
                return value;
            }
        }

        return null;
    }

}
