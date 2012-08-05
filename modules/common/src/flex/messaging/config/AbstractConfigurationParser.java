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
package flex.messaging.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import flex.messaging.util.FileUtils;

/**
 * Provides a common base for DOM / XPath based ConfigurationParsers.
 *
 * @author Peter Farland
 * @exclude
 */
public abstract class AbstractConfigurationParser implements ConfigurationParser, ConfigurationConstants
{
    protected ServicesConfiguration config;
    protected DocumentBuilder docBuilder;
    protected ConfigurationFileResolver fileResolver;
    protected TokenReplacer tokenReplacer;

    private Map fileByDocument = new HashMap();

    protected AbstractConfigurationParser()
    {
        initializeDocumentBuilder();
        tokenReplacer = new TokenReplacer();
    }

    public void parse(String path, ConfigurationFileResolver fileResolver, ServicesConfiguration config)
    {
        this.config = config;
        this.fileResolver = fileResolver;
        Document doc = loadDocument(path, fileResolver.getConfigurationFile(path));
        initializeExpressionQuery();
        parseTopLevelConfig(doc);
    }

    public void reportTokens()
    {
        tokenReplacer.reportTokens();
    }

    protected void initializeDocumentBuilder()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);

        try
        {
            docBuilder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex)
        {
            // Error initializing configuration parser.
            ConfigurationException e = new ConfigurationException();
            e.setMessage(PARSER_INIT_ERROR);
            e.setRootCause(ex);
            throw e;
        }
    }

    protected Document loadDocument(String path, InputStream in)
    {
        try
        {
            Document doc;

            if (!in.markSupported())
                in = new BufferedInputStream(in);

            // Consume UTF-8 Byte Order Mark (BOM). The InputStream must support mark()
            // as FileUtils.consumeBOM sets a mark for 3 bytes in order to check for a BOM.
            String encoding = FileUtils.consumeBOM(in, null);
            if (FileUtils.UTF_8.equals(encoding) || FileUtils.UTF_16.equals(encoding))
            {
                InputSource inputSource = new InputSource(in);
                inputSource.setEncoding(encoding);
                doc = docBuilder.parse(inputSource);
            }
            else
            {
                doc = docBuilder.parse(in);
            }

            addFileByDocument(path, doc);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (SAXParseException ex)
        {
            Integer line = new Integer(ex.getLineNumber());
            Integer col = new Integer(ex.getColumnNumber());
            String message = ex.getMessage();

            // Configuration error on line {line}, column {col}:  '{message}'
            ConfigurationException e = new ConfigurationException();
            e.setMessage(XML_PARSER_ERROR, new Object[]{line, col, message});
            e.setRootCause(ex);
            throw e;
        }
        catch (SAXException ex)
        {
            ConfigurationException e = new ConfigurationException();
            e.setMessage(ex.getMessage());
            e.setRootCause(ex);
            throw e;
        }
        catch (IOException ex)
        {
            ConfigurationException e = new ConfigurationException();
            e.setMessage(ex.getMessage());
            e.setRootCause(ex);
            throw e;
        }
    }

    protected void addFileByDocument(String path, Node node)
    {
        String shortPath = path;
        if (shortPath != null && ((shortPath.indexOf('/') != -1) || (shortPath.indexOf("\\") != -1)) )
        {
            int start = 0;
            start = shortPath.lastIndexOf('/');
            if (start == -1)
            {
                start = shortPath.lastIndexOf("\\");
            }
            shortPath = path.substring(start + 1);
        }
        fileByDocument.put(node, shortPath);
    }

    protected String getSourceFileOf(Node node)
    {
        return (String)fileByDocument.get(node.getOwnerDocument());
    }

    protected abstract void parseTopLevelConfig(Document doc);

    protected abstract void initializeExpressionQuery();
    protected abstract Node selectSingleNode(Node source, String expression);
    protected abstract NodeList selectNodeList(Node source, String expression);
    protected abstract Object evaluateExpression(Node source, String expression);

    /**
     * Recursively processes all child elements for each of the properties in the provided
     * node list.
     * <p>
     * If a property is a simple element with a text value then it is stored as a String
     * using the element name as the property name. If the same element appears again then
     * the element is converted to a List of values and further occurences are simply added
     * to the List.
     * </p>
     * <p>
     * If a property element has child elements the children are recursively processed
     * and added as a Map.
     * </p>
     * <p>
     * The sourceFileName argument is used as a parameter for token replacement in order
     * to generate a meaningful error message when a token is failed to be replaced.
     * </p>
     */
    public ConfigMap properties(NodeList properties)
    {
        return properties(properties, ConfigurationConstants.UNKNOWN_SOURCE_FILE);
    }

    public ConfigMap properties(NodeList properties, String sourceFileName)
    {
        int length = properties.getLength();
        ConfigMap map = new ConfigMap(length);

        for (int p = 0; p < length; p++)
        {
            Node prop = properties.item(p);
            String propName = prop.getNodeName();

            if (propName != null)
            {
                propName = propName.trim();
                if (prop.getNodeType() == Node.ELEMENT_NODE)
                {
                    NodeList attributes = selectNodeList(prop, "@*");
                    NodeList children = selectNodeList(prop, "*");
                    if (children.getLength() > 0 || attributes.getLength() > 0)
                    {
                        ConfigMap childMap = new ConfigMap();

                        if (children.getLength() > 0)
                            childMap.addProperties(properties(children, sourceFileName));

                        if (attributes.getLength() > 0)
                            childMap.addProperties(properties(attributes, sourceFileName));

                        map.addProperty(propName, childMap);
                    }
                    else
                    {
                        // replace tokens before setting property
                        tokenReplacer.replaceToken(prop, sourceFileName);
                        String propValue = evaluateExpression(prop, ".").toString();
                        map.addProperty(propName, propValue);
                    }
                }
                else
                {
                    // replace tokens before setting property
                    tokenReplacer.replaceToken(prop, sourceFileName);
                    map.addProperty(propName, prop.getNodeValue());
                }
            }
        }

        return map;
    }

    public String getAttributeOrChildElement(Node node, String name)
    {
        String attr = evaluateExpression(node, "@" + name).toString().trim();
        if (attr.length() == 0)
        {
            attr = evaluateExpression(node, name).toString().trim();
        }
        return attr;
    }

    public void allowedChildElements(Node node, String[] allowed)
    {
        NodeList children = selectNodeList(node, "*");

        String unexpected = unexpected(children, allowed);
        if (unexpected != null)
        {
            // Unexpected child element '{0}' found in '{1}'.
            ConfigurationException ex = new ConfigurationException();
            Object[] args = {unexpected, node.getNodeName(), getSourceFilename(node)};
            ex.setMessage(UNEXPECTED_ELEMENT, args);
            throw ex;
        }

        NodeList textNodes = selectNodeList(node, "text()");
        for (int i = 0; i < textNodes.getLength(); i++)
        {
            String text = evaluateExpression(textNodes.item(i), ".").toString().trim();
            if (text.length() > 0)
            {
                //Unexpected text '{0}' found in '{1}'.
                ConfigurationException ex = new ConfigurationException();
                Object[] args = {text, node.getNodeName(), getSourceFilename(node)};
                ex.setMessage(UNEXPECTED_TEXT, args);
                throw ex;
            }
        }
    }

    public void allowedAttributes(Node node, String[] allowed)
    {
        NodeList attributes = selectNodeList(node, "@*");
        String unexpectedAttribute = unexpected(attributes, allowed);
        if (unexpectedAttribute != null)
        {
            //Unexpected attribute '{0}' found in '{1}'.
            ConfigurationException ex = new ConfigurationException();
            Object[] args =
                {unexpectedAttribute, node.getNodeName(), getSourceFilename(node)};
            ex.setMessage(UNEXPECTED_ATTRIBUTE, args);
            throw ex;
        }
    }

    private String getSourceFilename(Node node)
    {
        return getSourceFileOf(node);
    }

    public void allowedAttributesOrElements(Node node, String[] allowed)
    {
        allowedAttributes(node, allowed);
        allowedChildElements(node, allowed);
    }

    public void requiredAttributesOrElements(Node node, String[] required)
    {
        String nodeName = node.getNodeName();
        NodeList attributes = selectNodeList(node, "@*");

        List list = new ArrayList();
        for (int i = 0; i < required.length; i++)
        {
            list.add(required[i]);
        }

        String missingAttribute = null;

        do
        {
            missingAttribute = required(attributes, list);
            if (missingAttribute != null)
            {
                Node child = selectSingleNode(node, missingAttribute);
                if (child != null)
                {
                    list.remove(missingAttribute);
                }
                else
                {
                    // Attribute '{0}' must be specified for element '{1}'
                    ConfigurationException ex = new ConfigurationException();
                    ex.setMessage(MISSING_ATTRIBUTE, new Object[]{missingAttribute, nodeName});
                    throw ex;
                }
            }
        }
        while (missingAttribute != null && list.size() > 0);
    }

    public void requiredChildElements(Node node, String[] required)
    {
        String nodeName = node.getNodeName();
        NodeList children = selectNodeList(node, "*");

        List list = new ArrayList();
        for (int i = 0; i < required.length; i++)
        {
            list.add(required[i]);
        }

        String missing = required(children, list);
        if (missing != null)
        {
            // Child element '{0}' must be specified for element '{1}'
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(MISSING_ELEMENT, new Object[]{missing, nodeName});
            throw ex;
        }
    }

    /**
     * @return Name of the first unexpected item from the list of allowed attributes, or null if all
     *         items present were allowed.
     */
    public String unexpected(NodeList attributes, String[] allowed)
    {
        for (int i = 0; i < attributes.getLength(); i++)
        {
            Node attrib = attributes.item(i);
            String attribName = attrib.getNodeName();
            boolean match = false;

            for (int j = 0; j < allowed.length; j++)
            {
                String a = allowed[j];
                if (a.equals(attribName))
                {
                    match = true;
                    break;
                }
            }

            if (!match)
            {
                return attribName;
            }

            // Go ahead and replace tokens in node values.
            tokenReplacer.replaceToken(attrib, getSourceFilename(attrib));
        }

        return null;
    }

    /**
     * @return Name of the first missing item from the list of required attributes, or null if all required
     *         items were present.
     */
    public String required(NodeList attributes, List required)
    {
        for (int i = 0; i < required.size(); i++)
        {
            boolean found = false;
            String req = (String)required.get(i);

            Node attrib = null;
            for (int j = 0; j < attributes.getLength(); j++)
            {
                attrib = attributes.item(j);
                String attribName = attrib.getNodeName();

                if (req.equals(attribName))
                {
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                return req;
            }

            // Go ahead and replace tokens in node values.
            tokenReplacer.replaceToken(attrib, getSourceFilename(attrib));

        }

        return null;
    }


    /**
     * Tests whether a configuration element's id is a valid
     * identifier. An id must be a String that is not null,
     * greater than 0 characters in length and not contain
     * any of the list delimiter characters, i.e. commas,
     * semi-colons or colons.
     *
     * @see ConfigurationConstants#LIST_DELIMITERS
     */
    public static boolean isValidID(String id)
    {
        if (id != null && id.length() > 0 && id.length() < 256)
        {
            char[] chars = id.toCharArray();
            for (int i = 0; i < chars.length; i++)
            {
                char c = chars[i];
                if (LIST_DELIMITERS.indexOf(c) != -1)
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
