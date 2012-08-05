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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Stack;

/**
 * @exclude
 */
public class LocalFileResolver implements ConfigurationFileResolver
{
    private Stack configurationPathStack = new Stack();
    int version = CLIENT;

    public static int CLIENT = 0;
    public static int SERVER = 1;
    public static int LIVECYCLE = 2;

    public LocalFileResolver()
    {
    }

    public LocalFileResolver(int version)
    {
        this.version = version;
    }

    public void setErrorMessage(ConfigurationException e, String path)
    {
        if (version == LIVECYCLE)
        {
            e.setMessage(11122, new Object[] { path });
        }
        else if (version == SERVER)
        {
            // Please specify a valid ''services.configuration.file'' in web.xml.
            e.setMessage(11108);
        }
        else
        {
            // Please specify a valid <services/> file path in flex-config.xml.
            e.setMessage(11106);
        }
    }

    public InputStream getConfigurationFile(String path)
    {
        File f = new File(path);
        try
        {
            if (f != null && f.exists() && f.isAbsolute())
            {
                FileInputStream fin = new FileInputStream(f);
                pushConfigurationFile(f.getParent());
                return fin;
            }
            else
            {
                ConfigurationException e = new ConfigurationException();
                setErrorMessage(e, path);
                throw e;
            }
        }
        catch (FileNotFoundException ex)
        {
            ConfigurationException e = new ConfigurationException();
            setErrorMessage(e, path);
            e.setRootCause(ex);
            throw e;
        }
        catch (SecurityException se)
        {
            ConfigurationException e = new ConfigurationException();
            setErrorMessage(e, path);
            e.setRootCause(se);
            throw e;
        }
    }

    public InputStream getIncludedFile(String src)
    {
        String path = configurationPathStack.peek() + File.separator + src;
        File f = new File(path);
        try
        {
            if (f != null && f.exists() && f.isAbsolute())
            {
                FileInputStream fin = new FileInputStream(f);
                pushConfigurationFile(f.getParent());
                return fin;
            }
            else
            {
                // Please specify a valid include file. ''{0}'' is invalid.
                ConfigurationException e = new ConfigurationException();
                e.setMessage(11107, new Object[] {path});
                throw e;
            }
        }
        catch (FileNotFoundException ex)
        {
            // Please specify a valid include file. ''{0}'' is invalid.
            ConfigurationException e = new ConfigurationException();
            e.setMessage(11107, new Object[] {path});
            e.setRootCause(ex);
            throw e;
        }
        catch (SecurityException se)
        {
            // Please specify a valid include file. ''{0}'' is invalid.
            ConfigurationException e = new ConfigurationException();
            e.setMessage(11107, new Object[] {path});
            e.setRootCause(se);
            throw e;
        }
    }

    public void popIncludedFile()
    {
        configurationPathStack.pop();
    }

    private void pushConfigurationFile(String topLevelPath)
    {
        configurationPathStack.push(topLevelPath);
    }

    public String getIncludedPath(String src)
    {
        return configurationPathStack.peek() + File.separator + src;
    }

    public long getIncludedLastModified(String src)
    {
        String path = configurationPathStack.peek() + File.separator + src;
        File f = new File(path);
        return f.lastModified();
    }
}