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
package flex.messaging.security;

/**
 * Contains the last good TomcatLogin for use by the
 * TomcatLoginCommand.
 *
 * JAR NOTE: this class is not in flex-messaging.jar but rather flex-tomcat-common.jar
 *
 * @author Matt Chotin
 *
 */
public class TomcatLoginHolder
{
    private static ThreadLocal logins = new ThreadLocal();
    private static TomcatLogin lastLogin;

    private TomcatLoginHolder()
    {
    }

    /**
     * Saves the last valid login.
     * 
     * @param login last valid login
     */
    public static void setLogin(TomcatLogin login)
    {
        logins.set(login);
        lastLogin = login;
    }

    /**
     * Retrieves the last valid login.
     * @return last valid login.
     */
    public static TomcatLogin getLogin()
    {
        if (logins.get() != null)
        {
            return (TomcatLogin)logins.get();
        }
        return lastLogin;
    }
}
