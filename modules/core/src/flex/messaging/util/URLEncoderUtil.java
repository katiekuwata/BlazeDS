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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utility class for URL encoding
 * 
 * @exclude
 */
public class URLEncoderUtil
{
    /**
     * Encode a URL into using the specified encoding string.
     * 
     * @param s URL to encode
     * @param encoding encoding string
     * @return encoded URL
     * @throws UnsupportedEncodingException Throws UnsupportedEncodingException
     */
    public static String encode(String s, String encoding)
        throws UnsupportedEncodingException
    {
        return URLEncoder.encode(s, encoding);
    }
}
