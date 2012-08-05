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

package security; 

import java.security.Principal; 

public class PerClientPrincipal implements Principal { 

    private String name; 
     
    public PerClientPrincipal(){ 
         
    } 
    public PerClientPrincipal(String inName){ 
        name = inName; 
    } 

    public String getName() { 
        return name; 
    } 
     
    public void setName(String inName) { 
        name = inName; 
    } 

}