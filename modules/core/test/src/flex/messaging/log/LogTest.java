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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LogTest extends TestCase
{
    public LogTest(String name)
    {
        super(name);
    }
    
    private TestingTarget testTarget;
    
    public static Test suite()
    {
        return new TestSuite(LogTest.class);
    }
    
    protected void setUp() throws Exception
    {
        Log.createLog();
        // Init a ConsoleLogTarget for testing purposes.        
        testTarget = new TestingTarget(); // Defaults to a log level of none with no filters.
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        Log.flush();
        super.tearDown();
    }
    
    public void testInitialLogState()
    {
        Assert.assertEquals(Log.getTargetLevel(), LogEvent.NONE);
        Assert.assertTrue(Log.getTargets().size() == 0); 
    }
    
    public void testLogStateAfterAddingTarget()
    {        
        Assert.assertEquals(testTarget.getLevel(), LogEvent.ERROR);
        testTarget.setLevel(LogEvent.FATAL);
        Assert.assertEquals(testTarget.getLevel(), LogEvent.FATAL);        
        Assert.assertTrue(testTarget.getFilters().size() == 1);
        Log.addTarget(testTarget);
        Assert.assertEquals(Log.getTargetLevel(), LogEvent.FATAL);
        Assert.assertTrue(Log.getTargets().size() == 1);
    }
    
    public void testLogStateAfterAddRemoveTarget()
    {
        testTarget.setLevel(LogEvent.FATAL);
        Log.addTarget(testTarget);
        Log.removeTarget(testTarget);
        Assert.assertEquals(Log.getTargetLevel(), LogEvent.NONE);
        Assert.assertTrue(Log.getTargets().size() == 0);
    }
    
    public void testAddTargetGetLogger()
    {
        testTarget.setLevel(LogEvent.ALL);
        testTarget.addFilter("*");
        Assert.assertTrue(testTarget.getFilters().size() == 1);
        Log.addTarget(testTarget);
        Log.getLogger("foo");
        Assert.assertTrue(testTarget.loggerCount == 1);
        Log.getLogger("bar");
        Assert.assertTrue(testTarget.loggerCount == 2);
        Log.removeTarget(testTarget);
        Assert.assertTrue(testTarget.loggerCount == 0);
        testTarget.removeFilter("*");
        Assert.assertTrue(testTarget.getFilters().size() == 0);
    }
    
    public void testAddTargetGetLoggerThenRemoveFilter()
    {
        testTarget.setLevel(LogEvent.ALL);
        testTarget.addFilter("foo.*");
        Assert.assertTrue(testTarget.getFilters().size() == 1);
        Log.addTarget(testTarget);
        Log.getLogger("foo.bar");
        Assert.assertTrue(testTarget.loggerCount == 1);
        testTarget.removeFilter("foo.*");
        Assert.assertTrue(testTarget.loggerCount == 0);
    }
    
    public void testGetLoggerAddTarget()
    {
        // First, remove the default "*" filter.
        testTarget.removeFilter("*");
        
        Log.getLogger("foo");
        Log.getLogger("bar");
        Log.getLogger("baz"); // Shouldn't be added to the target later.
        
        testTarget.setLevel(LogEvent.ALL);
        Log.addTarget(testTarget);        
        Assert.assertTrue(testTarget.loggerCount == 0);
        
        // Now add filters.
        List filters = new ArrayList();
        filters.add("foo");
        filters.add("bar");        
        testTarget.setFilters(filters);        
        Assert.assertTrue(testTarget.loggerCount == 2);
    }
        
    public void testLogAddFilterNull()
    {
        // First, remove the default "*" filter.
        testTarget.removeFilter("*");
        
        Log.getLogger("foo");
        
        testTarget.setLevel(LogEvent.ALL);
        Log.addTarget(testTarget);        
        Assert.assertTrue(testTarget.loggerCount == 0);
        
        // Now null filters.
        List filters = new ArrayList();        
        testTarget.addFilter(null); 
        testTarget.setFilters(filters);       
        Assert.assertTrue(testTarget.loggerCount == 1);
    }
        
    public void testLogSetFilterNull()
    {
        // First, remove the default "*" filter.
        testTarget.removeFilter("*");
        
        Log.getLogger("foo");
        
        testTarget.setLevel(LogEvent.ALL);
        Log.addTarget(testTarget);        
        Assert.assertTrue(testTarget.loggerCount == 0);
        
        // Now null filters.
        List filters = new ArrayList();        
        testTarget.setFilters(filters);        
        Assert.assertTrue(testTarget.loggerCount == 1);
    }
}
