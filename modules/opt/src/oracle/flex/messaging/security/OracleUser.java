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

import oracle.security.jazn.realm.Realm;
import oracle.security.jazn.realm.RealmUser;
import oracle.security.jazn.realm.RealmRole;

import java.security.Principal;
import java.util.*;
import javax.security.auth.*;
import javax.security.auth.login.*;

/**
 * An Oracle specific implementation of java.security.Principal.
 * 
 * @exclude
 */
public class OracleUser implements Principal
{
    private LoginContext context;
    private Subject subject;

    public OracleUser(LoginContext context) throws LoginException
    {
        this.context = context;
        context.logout();
        context.login();
        this.subject = context.getSubject();
    }

    public void logout() throws LoginException
    {
        context.logout();
    }

    private Principal userPrincipal()
    {
        Set possibleUsers = subject.getPrincipals(RealmUser.class);
        return (Principal) possibleUsers.iterator().next();
    }

    public boolean isMemberOf(List roleNames)
    {
        boolean result = false;
        Set possibleUsers = subject.getPrincipals(RealmRole.class);
        Iterator itr = possibleUsers.iterator();
        while (itr.hasNext())
        {
            RealmRole role = (RealmRole) itr.next();
            Realm realm = role.getRealm();
            String realmFullName = realm.getFullName();
            String roleSimpleName = role.getName();
            if ((realmFullName.length() > 0) &&
                roleSimpleName.startsWith(realmFullName))
            {
                // Format is "<realm full name>\<role name>"
                roleSimpleName = roleSimpleName.substring
                    (realmFullName.length() + 1);
            }
            
            if (roleNames.contains(roleSimpleName))
            {
                result = true;
                break;
            }
        }
        return result;
    }
   
    public boolean equals(Object object)
    {
        boolean result = false;
        if (object == this)
        {
            result = true;
        }
        else if (object instanceof OracleUser)
        {
            OracleUser other = (OracleUser) object;
            result = this.subject.equals(other.subject);
        }
        return result;
    }

    public String getName() 
    {
        return userPrincipal().getName();
    }

    public int hashCode() 
    {
        return this.subject.hashCode();
    }

    public String toString()
    {
        return this.subject.toString();
    }
}
