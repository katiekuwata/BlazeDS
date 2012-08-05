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

package flex.messaging.io.amf.translator;

import java.util.IdentityHashMap;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Simple test to check that the StrictReferenceTable
 * auto increments correctly, does not try to access items
 * out of bounds, and correct auto generates index-based
 * and incremented Integer values for added keys.
 *
 * @author Peter Farland
 */
public class StrictReferenceTableTest extends TestCase
{
    private IdentityHashMap table;

    private static Object ONE = new Object();
    private static Object TWO = new Object();
    private static Object THREE = new Object();
    private static Object FOUR = new Object();
    private static Object FIVE = new Object();
    private static Object[] objects = new Object[]{ONE, TWO, THREE, FOUR, FIVE};

    private static final int length = 2;

    public StrictReferenceTableTest()
    {
    }

    protected void setUp() throws Exception
    {
        table = new IdentityHashMap(length);
    }

    public static Test suite()
    {
        return new TestSuite(StrictReferenceTableTest.class);
    }

    public void testCapacity()
    {
        table.clear();
        int goal = length * 2;

        for (int i = 0; i < goal; i++)
        {
            table.put(objects[i], objects[i]);
        }

        Object one = table.get(ONE);
        if (one != ONE)
        {
            fail();
        }

        Object two = table.get(TWO);
        if (two != TWO)
        {
            fail();
        }

        Object three = table.get(THREE);
        if (three != THREE)
        {
            fail();
        }

        Object four = table.get(FOUR);
        if (four != FOUR)
        {
            fail();
        }

        Object five = table.get(FIVE);
        if (five != null)
        {
            fail();
        }

        if (table.size() != 4)
        {
            fail();
        }
    }

    public void testIndex()
    {
        table.clear();
        int goal = length * 2;

        for (int i = 0; i < goal; i++)
        {
            table.put(objects[i], new Integer(table.size()));
        }

        Integer first = (Integer)table.get(ONE);
        if (first.intValue() != 0)
        {
            fail();
        }

        Integer second = (Integer)table.get(TWO);
        if (second.intValue() != 1)
        {
            fail();
        }

        Integer third = (Integer)table.get(THREE);
        if (third.intValue() != 2)
        {
            fail();
        }

        Integer fourth = (Integer)table.get(FOUR);
        if (fourth.intValue() != 3)
        {
            fail();
        }

        Object fifth = table.get(FIVE);
        if (fifth != null)
        {
            fail();
        }
    }

    protected void tearDown() throws Exception
    {
        table = null;
    }
}
