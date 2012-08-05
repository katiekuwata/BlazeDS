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

public class HexTest extends TestCase
{
    public HexTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(HexTest.class);
    }

    /**
     * Try encoding and decoding 10,000 random combinations of bytes.
     */
    public void testEncodingAndDecoding()
    {
        int randomLimit = 500;
        boolean success = true;

        for (int myCount = 0; myCount < 10000; myCount++)
        {
            byte raw [] = new byte[(int)(Math.random() * randomLimit)];

            for (int i = 0; i < raw.length; ++i)
            {
                if ((i % 1024) < 256)
                    raw[i] = (byte)(i % 1024);
                else
                    raw[i] = (byte)((int)(Math.random() * 255) - 128);
            }
            Hex.Encoder encoder = new Hex.Encoder(100);
            encoder.encode(raw);

            String encoded = encoder.drain();

            Hex.Decoder decoder = new Hex.Decoder();
            decoder.decode(encoded);
            byte check[] = decoder.flush();

            if (check.length != raw.length)
            {
                success = false;
            }
            else
            {
                for (int i = 0; i < check.length; ++i)
                {
                    if (check[i] != raw[i])
                    {
                        success = false;
                        break;
                    }
                }
            }

            if (!success)
            {
                break;
            }
        }

        assertTrue(success);
    }
}
