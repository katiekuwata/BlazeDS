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
package flex.messaging.services.http.httpclient;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @exclude
 * Simple wrapper around PostMethod that exposes one method for ProxyServlet.
 *
 * @author Brian Deitte
 */
public class FlexGetMethod extends GetMethod
{
    public FlexGetMethod(String str)
    {
        super(str);
    }

    public void setConnectionForced(boolean bool)
    {
        setConnectionCloseForced(bool);
    }

    protected String getContentCharSet(Header contentheader)
    {
        String charset = null;
        if (contentheader != null)
        {
            HeaderElement values[] = contentheader.getElements();
            if (values.length == 1)
            {
                NameValuePair param = values[0].getParameterByName("charset");
                if (param != null)
                {
                    charset = param.getValue();
                }
            }
        }
        if (charset == null)
        {
            charset = "UTF-8";
        }
        return charset;
    }
}
