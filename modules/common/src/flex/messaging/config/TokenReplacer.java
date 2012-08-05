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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.util.StringUtils;

/**
 * This class is used by configuration parser to replace tokens of the format
 * {...} with actual values.
 *
 * @exclude
 */
public class TokenReplacer
{
    private static final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
    private final Map replacedTokens;

    /**
     * Default constructor.
     */
    public TokenReplacer()
    {
        replacedTokens = new LinkedHashMap();
    }

    /**
     * Replace any tokens in the value of the node or the text child of the node.
     *
     * @param node The node whose value will be searched for tokens.
     * @param sourceFileName The source file where the node came from.
     */
    public void replaceToken(Node node, String sourceFileName)
    {
        // exit if we are attempting to replace one of the forbidden nodes - nodes
        // that may have curly brackets as part of their syntax
        if (ConfigurationConstants.IP_ADDRESS_PATTERN.equals(node.getNodeName()))
            return;

        // replacementNode is either the original node if it has a value or the text
        // child of the node if it does not have a value
        Node replacementNode;
        if (node.getNodeValue() == null)
        {
            if (node.getChildNodes().getLength() == 1 && node.getFirstChild() instanceof Text)
                replacementNode = node.getFirstChild();
            else
                return;
        }
        else
        {
            replacementNode = node;
        }

        String nodeValue = replacementNode.getNodeValue();
        Matcher matcher = pattern.matcher(nodeValue);
        while (matcher.find()) // Means the node value has token(s)
        {
            String tokenWithCurlyBraces = matcher.group();
            String tokenWithoutCurlyBraces = matcher.group(1);
            // See if there is a JVM property for the token
            String propertyValue = System.getProperty(tokenWithoutCurlyBraces);
            if (propertyValue != null)
            {
                nodeValue = StringUtils.substitute(nodeValue, tokenWithCurlyBraces, propertyValue);
                replacedTokens.put(tokenWithCurlyBraces, propertyValue);
            }
            // context-path, server-name and server-port tokens can be replaced
            // later, therefore, no warning is necessary if they cannot be replaced
            // at this point.
            else if (!ConfigurationConstants.CONTEXT_PATH_TOKEN.equals(tokenWithCurlyBraces)
                    && !ConfigurationConstants.CONTEXT_PATH_ALT_TOKEN.equals(tokenWithCurlyBraces)
                    && !ConfigurationConstants.SERVER_NAME_TOKEN.equals(tokenWithCurlyBraces)
                    && !ConfigurationConstants.SERVER_PORT_TOKEN.equals(tokenWithCurlyBraces))
            {
                // Token ''{0}'' in ''{1}'' was not replaced. Either supply a value to this token with a JVM option, or remove it from the configuration.
                ConfigurationException ex = new ConfigurationException();
                Object[] args = {tokenWithCurlyBraces, sourceFileName};
                ex.setMessage(ConfigurationConstants.IRREPLACABLE_TOKEN, args);
                throw ex;
            }
        }
        replacementNode.setNodeValue(nodeValue);
    }

    /**
     * Used by the parser to report the replaced tokens once logging is setup.
     */
    public void reportTokens()
    {
        if (Log.isWarn())
        {
            for (Iterator iter = replacedTokens.entrySet().iterator(); iter.hasNext();)
            {
                Map.Entry entry = (Map.Entry)iter.next();
                String tokenWithParanthesis = (String)entry.getKey();
                String propertyValue = (String)entry.getValue();
                // Issue a special warning for context.root replacements,
                if (ConfigurationConstants.CONTEXT_PATH_TOKEN.equals(tokenWithParanthesis)
                        || ConfigurationConstants.CONTEXT_PATH_ALT_TOKEN.equals(tokenWithParanthesis))
                {
                    if (Log.isWarn())
                        Log.getLogger(LogCategories.CONFIGURATION).warn("Token '{0}' was replaced with '{1}'. Note that this will apply to all applications on the JVM",
                                new Object[]{tokenWithParanthesis, propertyValue});
                }
                else if (Log.isDebug())
                {
                    Log.getLogger(LogCategories.CONFIGURATION).debug("Token '{0}' was replaced with '{1}'", new Object[]{tokenWithParanthesis, propertyValue});
                }
            }
        }
    }

    /**
     * Given a String, determines whether the string contains tokens. 
     * 
     * @param value The String to check.
     * @return True if the String contains tokens.
     */
    public static boolean containsTokens(String value)
    {
        return (value != null && value.length() > 0)? pattern.matcher(value).find() : false;
    }
}
