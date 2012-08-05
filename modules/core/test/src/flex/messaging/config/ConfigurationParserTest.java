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

package flex.messaging.config;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import flex.messaging.LocalizedException;
import flex.messaging.MessageBroker;

public class ConfigurationParserTest extends TestCase
{
    private String resourceBase = "/flex/messaging/config/";
    private String baseFilePath;

    public ConfigurationParserTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ConfigurationParserTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        MessageBroker broker = new MessageBroker(false);
        broker.initThreadLocals();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }


    /*
     *  TESTS
     */

    public void testConfiguration()
    {
        try
        {
            URL url = this.getClass().getResource(resourceBase + "ConfigurationParserTest.class");
            File file = new File(url.getFile());
            File dir = new File(file.getParent());

            baseFilePath = dir.getAbsolutePath();

            if (dir.isDirectory())
            {
                String resourcePath = getResourcePath(baseFilePath);
                processDirectory(dir, resourcePath);
            }
        }
        catch (IOException ioe)
        {
            fail("FDS Configuration test failed: " + ioe.getMessage());
        }
    }

    private void processDirectory(File dir, String resourcePath) throws IOException
    {
        File[] files = dir.listFiles(new XMLFilenameFilter());

        for (int i = 0; i < files.length; i++)
        {
            File f = files[i];
            if (f.isDirectory())
            {
                resourcePath = getResourcePath(f.getAbsolutePath());
                processDirectory(f, resourcePath);
            }
            else
            {
                String testName = getTestName(f.getName());
                System.out.println("Running test: " + testName);
                processRequest(f.toString(), testName, resourcePath);
            }
        }
    }

    private String getResourcePath(String path)
    {
        if (path.startsWith(baseFilePath))
        {
            path = path.substring(baseFilePath.length());
            path = path.replace('\\', '/');
            return path;
        }

        return null;
    }

    private String getTestName(String fileName)
    {
        fileName = fileName.toLowerCase();
        int pos = fileName.indexOf(".xml");
        return fileName.substring(0, pos);
    }

    private void processRequest(String filename, String testName, String resourcePath) throws IOException
    {
        MessagingConfiguration config = new MessagingConfiguration();
        ConfigurationParser parser = new ApacheXPathServerConfigurationParser();
        try
        {
            parser.parse(filename, new LocalFileResolver(), config);

            ConfigurationConfirmation confirmation = getConfirmation(resourcePath, testName);

            if (!confirmation.matches(config))
                fail("MessagingConfiguration did not match test: " + testName);
        }
        catch (LocalizedException ex)
        {
            ConfigurationConfirmation confirmation = getConfirmation(resourcePath, testName);

            if (confirmation.isNegativeTest())
            {
                if (!confirmation.negativeMatches(ex))
                    fail("MessageException did not match negative test " + testName);
            }
            else
            {
                throw ex;
            }
        }
    }

    private ConfigurationConfirmation getConfirmation(String resourcePath, String testName) throws IOException
    {
        try
        {
            if (resourceBase.endsWith("/") && resourcePath.startsWith("/"))
                resourcePath = resourcePath.substring(1);

            String resource = resourceBase + resourcePath;
            if (resource.startsWith("/"))
                resource = resource.substring(1);

            String packageName = resource.replace('/', '.');

            if (packageName.length() > 0 && !packageName.endsWith("."))
                packageName = packageName + ".";

            String className = packageName + "Confirm" + testName;
            Class c = Class.forName(className);

            Object obj = c.newInstance();
            ConfigurationConfirmation confirm = (ConfigurationConfirmation)obj;
            return confirm;
        }
        catch (Exception e)
        {
            throw new IOException("Unable to load FDS confirmation for testName: " + testName + " in " + resourcePath);
        }
    }

    static class XMLFilenameFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            String child = dir.getAbsolutePath() + File.separator + name;
            File f = new File(child);

            if (f.isDirectory())
                return true;
            else if (name.toLowerCase().endsWith(".xml") && name.indexOf("include") == -1)
                return true;
            else
                return false;
        }
    }

}