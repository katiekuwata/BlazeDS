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

package flex.messaging.services.http;

import flex.messaging.MessageBroker;
import flex.messaging.MessageException;
import flex.messaging.util.SettingsReplaceUtil;

import java.util.ArrayList;
import java.util.Set;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SettingsReplaceUtilTest extends TestCase
{
    public SettingsReplaceUtilTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(SettingsReplaceUtilTest.class);
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

    public void testReplaceContextPathDash()
    {
        String actual = SettingsReplaceUtil.replaceContextPath("http://localhost:8100/{context-root}/foo.mxml", "/dev");
        String expected = "http://localhost:8100/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceContextPathDot()
    {
        String actual = SettingsReplaceUtil.replaceContextPath("http://localhost:8100/{context.root}/foo.mxml", "/dev");
        String expected = "http://localhost:8100/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceContextPathBegin()
    {
        String actual = SettingsReplaceUtil.replaceContextPath("{context.root}/foo.mxml", "/dev");
        String expected = "/dev/foo.mxml";
        Assert.assertEquals(expected, actual);

    }

    public void testReplaceContextPathEnd()
    {
        String actual = SettingsReplaceUtil.replaceContextPath("http://localhost:8100/{context.root}", "/dev");
        String expected = "http://localhost:8100/dev";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceContextPathNull()
    {
        try
        {
            String actual = SettingsReplaceUtil.replaceContextPath("http://localhost:8100/{context.root}", null);
            fail("MessageException expected. Result was " + actual);
        }
        catch (MessageException me)
        {
            String error = "{context.root} token cannot";
            Assert.assertTrue(me.getMessage().indexOf(error) != -1);
        }
    }

    public void testReplaceGivenServerDash()
    {
        String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("http://{server-name}:{server-port}/dev/foo.mxml",
                                                                            "/dev", "10.1.1.1", "80", "http");
        String expected = "http://10.1.1.1:80/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceGivenServerDot()
    {
        String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("http://{server.name}:{server.port}/dev/foo.mxml",
                                                                            "/dev", "10.1.1.1", "80", "http");
        String expected = "http://10.1.1.1:80/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceGivenServerNullPort()
    {
        try
        {
            String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("http://{server.name}:{server.port}/dev/foo.mxml",
                                                                                        "/dev", "10.1.1.1", null, "http");
            fail("MessageException expected. Result was " + actual);
        }
        catch (MessageException me)
        {
            String error = "{server.port} token cannot";
            Assert.assertTrue(me.getMessage().indexOf(error) != -1);
        }
    }

    public void testReplaceGivenServerStarPort()
    {
        String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("http://{server.name}:*/dev/foo.mxml",
                                                                            "/dev", "10.1.1.1", "80", "http");
        String expected = "http://10.1.1.1:*/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceGivenServerContextRoot()
    {
        String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("http://{server.name}:{server.port}/{context.root}/foo.mxml",
                                                                            "/dev", "10.1.1.1", "80", "http");
        String expected = "http://10.1.1.1:80/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceGivenServerNoPort()
    {
        String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("http://{server.name}/dev/foo.mxml",
                                                                            "/dev", "10.1.1.1", "80", "http");
        String expected = "http://10.1.1.1/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceGivenServerNull()
    {
        try
        {
            String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("http://{server.name}:{server.port}/{context.root}/foo.mxml",
                                                                            null, null, null, "http");
            fail("MessageException expected. Result was " + actual);
        }
        catch (MessageException me)
        {
            String error = "{context.root} token cannot";
            Assert.assertTrue(me.getMessage().indexOf(error) != -1);
        }
    }

    public void testReplaceGivenServerRelative()
    {
        String actual = SettingsReplaceUtil.replaceAllTokensGivenServerName("/dev/foo.mxml",
                                                                            "/dev", "10.1.1.1", "80", "http");
        String expected = "http://10.1.1.1:80/dev/foo.mxml";
        Assert.assertEquals(expected, actual);
    }

    public void testReplaceCalculateServer()
    {
        ArrayList urls = new ArrayList();
        urls.add("http://{server.name}:*/dev/foo.mxml");
        Set updatedUrls = SettingsReplaceUtil.replaceAllTokensCalculateServerName(urls, "/dev");

        Assert.assertTrue(updatedUrls.size() >= 4); // at least, localhost, 127.0.0.1, ip num, ip name
        Assert.assertTrue(updatedUrls.contains("http://localhost:*/dev/foo.mxml"));
        Assert.assertTrue(updatedUrls.contains("http://127.0.0.1:*/dev/foo.mxml"));
    }

    public void testReplaceCalculateServerPort()
    {
        try
        {
            ArrayList urls = new ArrayList();
            urls.add("http://{server.name}:{server.port}/dev/foo.mxml");
            Set updatedUrls = SettingsReplaceUtil.replaceAllTokensCalculateServerName(urls, "/dev");
            fail("MessageException expected. Instead result was " + updatedUrls);
        }
        catch (MessageException me)
        {
            String error = "{server.port} token cannot";
            Assert.assertTrue(me.getMessage().indexOf(error) != -1);
        }
    }

    public void testReplaceCalculateServerMultiple()
    {
        ArrayList urls = new ArrayList();
        urls.add("http://{server.name}:*/dev/foo.mxml");
        Set updatedUrls = SettingsReplaceUtil.replaceAllTokensCalculateServerName(urls, "/dev");
        int count = updatedUrls.size();

        urls.add("http://{server.name}:*/dev/foo.mxml");
        Assert.assertTrue(updatedUrls.size() == count); // should result in the same list
    }

    public void testIPv6ShortForm()
    {
        String src = "http://[fe80::20d:60ff:fef9:8757]:8400/qa-regress/basic.html";
        String expected = "http://[fe80:0:0:0:20d:60ff:fef9:8757]:8400/qa-regress/basic.html";
        Assert.assertEquals(expected, SettingsReplaceUtil.updateIPv6(src));

        src = "http://[fe80::20d:60ff:fef9:8757]:8400/*";
        expected = "http://[fe80:0:0:0:20d:60ff:fef9:8757]:8400/*";
        Assert.assertEquals(expected, SettingsReplaceUtil.updateIPv6(src));
 
        src = "http://[::1]:8400/*";
        expected = "http://[0:0:0:0:0:0:0:1]:8400/*";
        Assert.assertEquals(expected, SettingsReplaceUtil.updateIPv6(src));
    }
}