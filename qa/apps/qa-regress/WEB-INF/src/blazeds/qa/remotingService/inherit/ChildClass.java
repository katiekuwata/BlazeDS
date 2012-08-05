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

package blazeds.qa.remotingService.inherit;

import java.io.Serializable;

public class ChildClass extends ParentClass implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static String childClassClassProp = "childClassClassProp";
    public String childClassOnlyProp = "childClassOnlyProp";
    public String overridedPropByProp = "childClassOverridedPropByProp";
    public String overridedGetterByProp = "childClassOverridedGetterByProp";
    private String childClassPrivateProp = "childClassPrivateProp";
    protected String childClassProtectedProp = "childClassProtectedProp";
    
    public String getOverridedPropByGetter() {
        return "childClassOverridedPropByGetter";
    }
    public void setOverridedPropByGetter(String s) {

    }


    public String getOverridedGetterByGetter() {
        return "childClassOverridedGetterByGetter";
    }
    public void setOverridedGetterByGetter(String s) {

    }

    public static ChildClass getChildClass() {
        return new ChildClass();
    }

    public static ParentClass getParentClass() {
        return new ParentClass();
    }
    
    public String toString() {
    	return "ChildClass: " + 
	    	"childClassClassProp = " + childClassClassProp + "\n" +
	    	"childClassOnlyProp = " + childClassOnlyProp + "\n" +
	    	"overridedPropByProp = " + overridedPropByProp + "\n" +
	    	"overridedGetterByProp = " + overridedGetterByProp + "\n" +
    		"childClassPrivateProp = " + childClassPrivateProp + "\n" +
    		"childClassProtectedProp = " + childClassProtectedProp;    		

    }
}
