/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import javax.servlet.ServletContext;

/**
 * @exclude
 */
public class ServletResourceResolver implements ConfigurationFileResolver
{
    private ServletContext context;
    private Stack configurationPathStack = new Stack();

    public ServletResourceResolver(ServletContext context)
    {
        this.context = context;
    }

    public boolean isAvailable(String path, boolean throwError) throws ConfigurationException
    {
        boolean available = false;
        InputStream is = context.getResourceAsStream(path);
        if (is != null)
        {
            try { is.close(); } catch (IOException ignore){}
            pushConfigurationFile(path);
            available = true;
        }
        else
        {
            if (throwError)
            {
                // Please specify a valid ''services.configuration.file'' in web.xml.
                ConfigurationException e = new ConfigurationException();
                e.setMessage(11108, new Object[] {path});
                throw e;
            }
        }

        return available;
    }

    public InputStream getConfigurationFile(String path)
    {
        InputStream is = context.getResourceAsStream(path);
        if (is != null)
        {
            pushConfigurationFile(path);
            return is;
        }
        else
        {
            // Please specify a valid ''services.configuration.file'' in web.xml.
            ConfigurationException e = new ConfigurationException();
            e.setMessage(11108, new Object[] {path});
            throw e;
        }
    }

    public InputStream getIncludedFile(String src)
    {
        String path = configurationPathStack.peek() + "/" + src;
        InputStream is = context.getResourceAsStream(path);

        if (is != null)
        {
            pushConfigurationFile(path);
            return is;
        }
        else
        {
            // Please specify a valid include file. ''{0}'' is invalid.
            ConfigurationException e = new ConfigurationException();
            e.setMessage(11107, new Object[] {path});
            throw e;
        }
    }

    public void popIncludedFile()
    {
        configurationPathStack.pop();
    }

    private void pushConfigurationFile(String path)
    {
        String topLevelPath = path.substring(0, path.lastIndexOf('/'));
        configurationPathStack.push(topLevelPath);
    }
}
