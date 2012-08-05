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

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to code in the Tomcat valve. Why is this needed?  Because Tomcat has a classloader system
 * where code in a valve does not appear in the classloader that is used for servlets.  There is a commons
 * area that both valves and servlets share, however, which is where this interface needs to be placed.
 *
 * JAR NOTE: this class is not in flex-messaging.jar but rather flex-tomcat-common.jar.
 *
 * @author Brian Deitte
 * @author Matt Chotin
 */
public interface TomcatLogin
{
    /**
     * Attempt to login user with the specified credentials.  Return a generated 
     * Principal object if login were successful
     * 
     * @param username username
     * @param password credentials
     * @param request request via which this login attempt was made
     * @return Principal generated for user if login were successful
     */
    Principal login(String username, String password, HttpServletRequest request);
    
    /**
     * The gateway calls this method to perform programmatic authorization.
     * <p>
     * A typical implementation would simply iterate over the supplied roles and
     * check that atleast one of the roles returned true from a call to
     * HttpServletRequest.isUserInRole(String role).
     * </p>
     *
     * @param principal The principal being checked for authorization
     * @param roles    A List of role names to check, all members should be strings
     * @return true if the principal is authorized given the list of roles
     */
    boolean authorize(Principal principal, List roles);
    
    /**
     * Logs out the user associated with the passed-in request.
     * 
     * @param request whose associated user is to be loged-out
     * @return true if logout were successful
     */
    boolean logout(HttpServletRequest request);
}
