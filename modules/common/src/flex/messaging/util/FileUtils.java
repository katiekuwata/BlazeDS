/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
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

import java.io.IOException;
import java.io.InputStream;

/**
 * @exclude
 */
public class FileUtils
{
    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16 = "UTF-16";

    /**
     * Sets a mark in the InputStream for 3 bytes to check for a BOM. If the BOM
     * stands for UTF-8 encoded content then the stream will not be reset, otherwise
     * for UTF-16 with a BOM or any other encoding situation the stream is reset to the
     * mark (as for UTF-16 the parser will handle the BOM).
     *
     * @param in InputStream containing BOM and must support mark().
     * @param default_encoding The default character set encoding. null or "" => system default
     * @return The file character set encoding.
     * @throws IOException
     */
    public static final String consumeBOM(InputStream in, String default_encoding) throws IOException
    {
        in.mark(3);

        // Determine file encoding...
        // ASCII - no header (use the supplied encoding)
        // UTF8  - EF BB BF
        // UTF16 - FF FE or FE FF (decoder chooses endian-ness)
        if (in.read() == 0xef && in.read() == 0xbb && in.read() == 0xbf)
        {
            // UTF-8 reader does not consume BOM, so do not reset
            if (System.getProperty("flex.platform.CLR") != null)
            {
                return "UTF8";
            }
            else
            {
                return UTF_8;
            }
        }
        else
        {
            in.reset();
            int b0 = in.read();
            int b1 = in.read();
            if (b0 == 0xff && b1 == 0xfe || b0 == 0xfe && b1 == 0xff)
            {
                in.reset();
                // UTF-16 reader will consume BOM
                if (System.getProperty("flex.platform.CLR") != null)
                {
                    return "UTF16";
                }
                else
                {
                    return UTF_16;
                }
            }
            else
            {
                // no BOM found
                in.reset();
                if (default_encoding != null && default_encoding.length() != 0)
                {
                    return default_encoding;
                }
                else
                {
                    return System.getProperty("file.encoding");
                }
            }
        }
    }

}
