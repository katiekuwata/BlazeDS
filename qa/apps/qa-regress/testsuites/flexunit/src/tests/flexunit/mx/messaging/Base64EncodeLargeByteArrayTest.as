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

package tests.flexunit.mx.messaging
{

import flexunit.framework.*;

import flash.utils.ByteArray;
import mx.controls.Alert;
import mx.utils.*;
/**
 * Tests base64 encoding/decoding large byte arrays.
 */
public class Base64EncodeLargeByteArrayTest extends TestCase
{
    ////////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    ////////////////////////////////////////////////////////////////////////////   
    public function Base64EncodeLargeByteArrayTest (name : String)
    {
        super(name);
    }
    ////////////////////////////////////////////////////////////////////////////
    //
    // TestSuite
    //
    ////////////////////////////////////////////////////////////////////////////   
     public static function suite() : TestSuite
    {
        var suite : TestSuite = new TestSuite();
        suite.addTest(new Base64EncodeLargeByteArrayTest ("testEncodeLargeByteArray"));
        return suite;
    }
    ////////////////////////////////////////////////////////////////////////////
    //
    // Tests
    //
    ////////////////////////////////////////////////////////////////////////////     
    /**
     * Test base64 encoding/decoding a large byte array using the Base64Encoder
     * and Base64Decoder classes
     */
    public function testEncodeLargeByteArray():void
    {
        var length:int = 130000;
        var ba:ByteArray = new ByteArray();

        for (var i:int = 0; i < length; i++)
        {
            ba.writeByte((Math.floor(Math.random() * 26) + 65));
        }

        var encoder:Base64Encoder = new Base64Encoder();
        encoder.encodeBytes(ba);
        var result:String = encoder.drain();
        assertEquals(true, length < result.length);
    }
}
}