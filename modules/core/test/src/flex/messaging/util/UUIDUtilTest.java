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

package flex.messaging.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UUIDUtilTest extends TestCase
{
    public UUIDUtilTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(UUIDUtilTest.class);
    }

    public void testIsUID()
    {
        // Randomly generated
        String uid = UUIDUtils.createUUID();
        boolean result = UUIDUtils.isUID(uid);
        assertEquals(true, result);

        // Pre-determined normal UID
        uid = "8653177A-930D-F5DC-6FAA-E1E6F557504E";
        result = UUIDUtils.isUID(uid);
        assertEquals(true, result);

        // All numbers
        uid = "06531779-9303-5532-6123-617685575043";
        result = UUIDUtils.isUID(uid);
        assertEquals(true, result);

        // All letters
        uid = "FEABCDEE-FFCC-FCED-DAAB-BCDEFFABCDAE";
        result = UUIDUtils.isUID(uid);
        assertEquals(true, result);

        // All Fs
        uid = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
        result = UUIDUtils.isUID(uid);
        assertEquals(true, result);

        // All 0s
        uid = "00000000-0000-0000-0000-000000000000";
        result = UUIDUtils.isUID(uid);
        assertEquals(true, result);

        // Invalid: Too long
        uid = "8653177A-930D-F5DC-6FAA-E1E6F557504EA";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: Too short
        uid = "8653177A-930D-F5DC-6FAA-E1E6F557504";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: all hyphens
        uid = "------------------------------------";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: No hyphens
        uid = "8653177A0930D0F5DC06FAA0E1E6F557504E";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: non-hex char at end
        uid = "8653177A-930D-F5DC-6FAA-E1E6F557504Z";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: non-hex char at start
        uid = "G653177A-930D-F5DC-6FAA-E1E6F557504E";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: null-char
        uid = "8653177A-930D-F5DC-6FAA-E1E6F557504\u0000";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: random word
        uid = "Cacophony";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: empty string
        uid = "";
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);

        // Invalid: null
        uid = null;
        result = UUIDUtils.isUID(uid);
        assertEquals(false, result);
    }

    public void testUIDToByteArray()
    {
        // Randomly generated
        String uid = UUIDUtils.createUUID();
        byte[] result = UUIDUtils.toByteArray(uid);
        assertEquals(true, result.length == 16);

        // Pre-determined normal UID
        uid = "8FEB51AC-1443-CA4C-D3A7-1AC1C5DC517B";
        result = UUIDUtils.toByteArray(uid);
        assertEquals(true, result.length == 16);

        // Invalid UID
        uid = "8FEB51AC-1443-CA4C-D3A7-1AC1C5DC517Z";
        result = UUIDUtils.toByteArray(uid);
        assertEquals(null, result);
    }

    public void testUIDFromByteArray()
    {
        // Randomly generated
        String uid1 = UUIDUtils.createUUID();
        byte[] ba = UUIDUtils.toByteArray(uid1);
        String uid2 = UUIDUtils.fromByteArray(ba);
        assertEquals(uid1, uid2);

        // Pre-determined normal UID
        uid1 = "86531839-0109-8B18-BB3A-CF8F546D0399";
        ba = UUIDUtils.toByteArray(uid1);
        uid2 = UUIDUtils.fromByteArray(ba);
        assertEquals(uid1, uid2);

        // Invalid: ByteArray too short
        ba = new byte[1];
        ba[0] = 19;
        uid2 = UUIDUtils.fromByteArray(ba);
        assertEquals(null, uid2);

        // Invalid: null ByteArray
        ba = null;
        uid2 = UUIDUtils.fromByteArray(ba);
        assertEquals(null, uid2);
    }
}