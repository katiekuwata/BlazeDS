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
package flex.messaging.services.messaging;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SubtopicTest extends TestCase
{
    private static final String DEFAULT_SEPERATOR = ".";
    private static final String ANOTHER_SEPERATOR = "*";

    public SubtopicTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(SubtopicTest.class);
    }

    public void testMatches1()
    {    
        Subtopic s1 = new Subtopic("foo", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo", DEFAULT_SEPERATOR);
        boolean result = s1.matches(s2);
        Assert.assertTrue(result);
    }

    public void testMatches2()
    {    
        Subtopic s1 = new Subtopic("foo", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("bar", DEFAULT_SEPERATOR);
        boolean result = s1.matches(s2);
        Assert.assertFalse(result);
    }
    
    public void testMatches3()
    {    
        Subtopic s1 = new Subtopic("foo.bar", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo.bar", DEFAULT_SEPERATOR);
        boolean result = s1.matches(s2);
        Assert.assertTrue(result);
    }

    public void testMatches4()
    {    
        Subtopic s1 = new Subtopic("foo.bar", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo*bar", ANOTHER_SEPERATOR);
        boolean result = s1.matches(s2);
        Assert.assertFalse(result);
    }

    public void testMatches5()
    {
        Subtopic s1 = new Subtopic("*", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo.bar.foo", DEFAULT_SEPERATOR); 
        boolean result = s1.matches(s2);
        Assert.assertTrue(result);
    }

    public void testMatches6()
    {
        Subtopic s1 = new Subtopic("foo.*", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo.bar", DEFAULT_SEPERATOR);
        boolean result = s1.matches(s2);
        Assert.assertTrue(result);
    }

    public void testMatches7()
    {
        Subtopic s1 = new Subtopic("foo.*", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo.bar.foo", DEFAULT_SEPERATOR);
        boolean result = s1.matches(s2);
        Assert.assertTrue(result);
    }

    public void testMatches8()
    {
        Subtopic s1 = new Subtopic("foo.bar.foo", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo.*", DEFAULT_SEPERATOR); 
        boolean result = s1.matches(s2);
        Assert.assertTrue(result);
    }

    public void testMatches9()
    {
        Subtopic s1 = new Subtopic("foo.bar.*", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo.bar.foo", DEFAULT_SEPERATOR); 
        boolean result = s1.matches(s2);
        Assert.assertTrue(result);
    }

    public void testMatches10()
    {
        Subtopic s1 = new Subtopic("foo.*", DEFAULT_SEPERATOR);
        Subtopic s2 = new Subtopic("foo", DEFAULT_SEPERATOR);
        boolean result = s1.matches(s2);
        Assert.assertFalse(result);
    }

}
