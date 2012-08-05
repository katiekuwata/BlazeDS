/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

package mx.utils.tests
{

import flexunit.framework.*;

import flash.utils.ByteArray;
import mx.controls.Alert;
import mx.utils.*;

public class ObjectUtilTest extends TestCase
{
    public static function suite():TestSuite
    {
        var suite:TestSuite = new TestSuite();
        suite.addTest(new ObjectUtilTest("testCompareArrays"));
        return suite;
    }

    public function ObjectUtilTest(name:String)
    {
        super(name);
    }

    public function testCompareArrays():void
    {
        var a0:Array;
        var a1:Array;
        var result:int;

        // Empty Arrays
        a0 = [];
        a1 = [];
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result == 0);

        // One Item Arrays
        a0 = [];
        a0[0] = "Zero";
        a1 = [];
        a1[0] = "Zero";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result == 0);

        // Two Item Arrays
        a0 = [];
        a0[0] = "Zero";
        a0[1] = "One";
        a1 = [];
        a1[0] = "Zero";
        a1[1] = "One";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result == 0);

        // Sparse Arrays
        a0 = [];
        a0[0] = "Zero";
        a0[10] = "Ten";
        a1 = [];
        a1[0] = "Zero";
        a1[10] = "Ten";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result == 0);

        // Associative Arrays
        a0 = [];
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a1 = [];
        a1["first"] = "firstValue";
        a1["second"] = "secondValue";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result == 0);

        // Mixed Arrays
        a0 = [];
        a0[0] = "Zero";
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a0[1] = "One";
        a1 = [];
        a1[0] = "Zero";
        a1["first"] = "firstValue";
        a1["second"] = "secondValue";
        a1[1] = "One";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result == 0);

        // Mixed Sparse Arrays
        a0 = [];
        a0[0] = "Zero";
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a0[10] = "Ten";
        a1 = [];
        a1[0] = "Zero";
        a1["first"] = "firstValue";
        a1["second"] = "secondValue";
        a1[10] = "Ten";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result == 0);

        //Negative: Empty vs Non-Empty
        a0 = [];
        a1 = [];
        a1[0] = "Zero";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: One-Item vs More-Than-One-Item
        a0 = [];
        a0[0] = "Zero";
        a1 = [];
        a1[0] = "Zero";
        a1[1] = "One";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Two-Items vs More-Than-Two-Items
        a0 = [];
        a0[0] = "Zero";
        a0[1] = "One";
        a1 = [];
        a1[0] = "Zero";
        a1[1] = "One";
        a1[2] = "Two";
        a1[3] = "Three";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Different Sparse Arrays
        a0 = [];
        a0[0] = "Zero";
        a0[10] = "Ten";
        a1 = [];
        a1[0] = "Zero";
        a1[1] = "One";
        a1[10] = "Ten";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Different Sparse Arrays Same Length
        a0 = [];
        a0[1] = "One";
        a0[10] = "Ten";
        a1 = [];
        a1[0] = "Zero";
        a1[10] = "Ten";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Different Associative Arrays
        a0 = [];
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a1 = [];
        a1["first"] = "firstValue";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Different Associative Arrays Same Length
        a0 = [];
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a1 = [];
        a1["first"] = "firstValue";
        a1["third"] = "thirdValue";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Same Associative Arrays Same Length Different Values
        a0 = [];
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a1 = [];
        a1["first"] = "firstValue";
        a1["second"] = "thirdValue";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Different Mixed Arrays
        a0 = [];
        a0[0] = "Zero";
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a0[1] = "One";
        a1 = [];
        a1[0] = "Zero";
        a1["first"] = "firstValue";
        a1["third"] = "thirdValue";
        a1[1] = "One";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);

        //Negative: Mixed Sparse Arrays
        a0 = [];
        a0[0] = "Zero";
        a0["first"] = "firstValue";
        a0["second"] = "secondValue";
        a0[10] = "Ten";
        a1 = [];
        a1[0] = "Zero";
        a1["first"] = "firstValue";
        a1["second"] = "secondValue";
        a1[11] = "Eleven";
        result = ObjectUtil.compare(a0, a1);
        assertEquals(true, result != 0);
        result = ObjectUtil.compare(a1, a0);
        assertEquals(true, result != 0);
    }
}

}