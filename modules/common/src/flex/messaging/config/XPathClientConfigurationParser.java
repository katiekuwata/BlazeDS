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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Uses Sun's JDK 1.5 XPath implementation on a DOM
 * representation of a messaging configuration file.
 * <p>
 * NOTE: Since reference ids are used between elements, certain
 * sections of the document need to be parsed first.
 * </p>
 *
 * @author Peter Farland
 * @exclude
 */
public class XPathClientConfigurationParser extends ClientConfigurationParser
{
    private XPath xpath;

    protected void initializeExpressionQuery()
    {
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    protected Node selectSingleNode(Node source, String expression)
    {
        try
        {
            return (Node) xpath.evaluate(expression, source, XPathConstants.NODE);
        }
        catch (XPathExpressionException expressionException)
        {
            throw wrapException(expressionException);
        }
    }

    protected NodeList selectNodeList(Node source, String expression)
    {
        try
        {
            return (NodeList) xpath.evaluate(expression, source, XPathConstants.NODESET);
        }
        catch (XPathExpressionException expressionException)
        {
            throw wrapException(expressionException);
        }
    }

    protected Object evaluateExpression(Node source, String expression)
    {
        try
        {
            return xpath.evaluate(expression, source, XPathConstants.STRING);
        }
        catch (XPathExpressionException expressionException)
        {
            throw wrapException(expressionException);
        }
    }

    private ConfigurationException wrapException(XPathExpressionException exception)
    {
       ConfigurationException result = new ConfigurationException();
       result.setDetails(PARSER_INTERNAL_ERROR);
       result.setRootCause(exception);
       return result;
    }
}
