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
package flex.messaging.services.http.proxy;

/**
 * @exclude
 * @author Brian Deitte
 */
public class CookieInfo
{
    public String clientName;
    public String domain;
    public String name;
    public String value;
    public String path;
    // for Java
    public int maxAge;
    // for .NET
    public Object maxAgeObj;
    public boolean secure;

    public CookieInfo(String clientName, String domain, String name, String value, String path,
                      int maxAge, Object maxAgeObj, boolean secure)
    {
        this.clientName = clientName;
        this.domain = domain;
        this.name = name;
        this.value = value;
        this.path = path;
        this.maxAge = maxAge;
        this.maxAgeObj = maxAgeObj;
        this.secure = secure;
    }

    public String toString()
    {
        return "domain = '" + domain +
                "', path = '" + path +
                "', client name = '" + clientName +
                "', endpoint name = '" + name +
                "', value = '" + value;
    }
}
