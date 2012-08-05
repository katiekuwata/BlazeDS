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

package flex.messaging.log;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.Assert;

public class LoggerTest extends TestCase
{
    private TestingTarget target;
    private TestingTarget target2;

    public LoggerTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(LoggerTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        Log.createLog();
        target = new TestingTarget(); 
        target.setLevel(LogEvent.ALL);
        target.addFilter("*");
        Log.addTarget(target);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testLogDebug()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        lg1.debug("testing");
        Assert.assertEquals(LogEvent.DEBUG, target.lastEvent.level);
        Assert.assertEquals("testing", target.lastEvent.message);
    }

    public void testLogInfo()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        lg1.info("testing");
        Assert.assertEquals(LogEvent.INFO, target.lastEvent.level);
        Assert.assertEquals("testing", target.lastEvent.message);
    }

    public void testLogWarning()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        lg1.warn("testing");
        Assert.assertEquals(LogEvent.WARN, target.lastEvent.level);
        Assert.assertEquals("testing", target.lastEvent.message);
    }

    public void testLogError()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        lg1.error("testing");
        Assert.assertEquals(LogEvent.ERROR, target.lastEvent.level);
        Assert.assertEquals("testing", target.lastEvent.message);
    }

    public void testLogFatal()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        lg1.fatal("testing");
        Assert.assertEquals(LogEvent.FATAL, target.lastEvent.level);
        Assert.assertEquals("testing", target.lastEvent.message);
    }

    public void testTargetLevelDebug()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        target.level = LogEvent.DEBUG;
        lg1.error("testing");
        Assert.assertNotNull(target.lastEvent);
        Assert.assertEquals(LogEvent.ERROR, target.lastEvent.level);
        Assert.assertEquals("testing", target.lastEvent.message);
    }

    public void testTargetLevelInfo()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        target.level = LogEvent.INFO;
        lg1.debug("testing");
        Assert.assertNull(target.lastEvent);
    }

    public void testTargetLevelFatal()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        target.level = LogEvent.FATAL;
        lg1.info("testing");
        Assert.assertNull(target.lastEvent);
        lg1.fatal("fatal");
        assertNotNull(target.lastEvent);
        assertEquals(LogEvent.FATAL, target.lastEvent.level);
        assertEquals("fatal", target.lastEvent.message);
    }

    public void testTargetLevelWarn()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        target.level = LogEvent.WARN;
        lg1.error("testing");
        Assert.assertNotNull(target.lastEvent);
        assertEquals(LogEvent.ERROR, target.lastEvent.level);
        assertEquals("testing", target.lastEvent.message);
        lg1.warn("warn");
        Assert.assertNotNull(target.lastEvent);
        assertEquals(LogEvent.WARN, target.lastEvent.level);
        assertEquals("warn", target.lastEvent.message);
    }

    public void testTargetLevelError()
    {
        Logger lg1 = Log.getLogger("mx.rpc.SOAP");
        target.level = LogEvent.ERROR;
        lg1.info("testing");
        Assert.assertNull(target.lastEvent);
        lg1.error("error");
        Assert.assertNotNull(target.lastEvent);
        assertEquals(LogEvent.ERROR, target.lastEvent.level);
        assertEquals("error", target.lastEvent.message);
    }

    public void testGetTargetId()
    {
        String tid = target.getId();
        Assert.assertNotNull(tid);
        target2 = new TestingTarget(); 
        target2.setLevel(LogEvent.ALL);
        target2.addFilter("*");
        Log.addTarget(target2);
        String tid2 = target2.getId();
        Assert.assertNotNull(tid2);
        Assert.assertNotSame(tid, tid2);
    }

    
}
