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

package dev.remoting.inherit;

import java.io.Serializable;

public class ParentClass implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public String inheritedProp = "parentClassInheritedProp";
    public String overridedPropByProp = "parentClassOverridedPropByProp";
    public String overridedPropByGetter = "parentClassOverridedPropByGetter";
    public static String parentClassClassProp = "parentClassClassProp";
    private String parentClassPrivateProp = "parentClassPrivateProp";
    protected String parentCleassProtectedProp;

    public String getInheritedPropGetter() {
        return "parentClassInheritedPropGetter";
    }
    public void setInheritedPropGetter(String s) {

    }

    public String getOverridedGetterByGetter() {
        return "parentClassOverridedGetterByGetter";
    }
    public void setOverridedGetterByGetter(String s) {

    }

    public String getOverridedGetterByProp() {
        return "parentClassOverridedGetterByProp";
    }
    public void setOverridedGetterByProp(String s) {

    }
    
    public String toString() {
    	return "Parent Class: " + 
	    	"inheritedProp = " + inheritedProp + "\n" +
	    	"overridedPropByProp = " + overridedPropByProp + "\n" +
	    	"overridedPropByGetter = " + overridedPropByGetter + "\n" +
	    	"parentClassClassProp = " + parentClassClassProp + "\n" +
	    	"parentClassPrivateProp = " + parentClassPrivateProp + "\n" +
	    	"parentCleassProtectedProp = " + parentCleassProtectedProp + "\n";
    }
}
