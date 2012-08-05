package blazeds.qa.remotingService;
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

import java.util.Collection;
import java.util.HashMap;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wchan
 * Date: Apr 25, 2005
 * Time: 9:20:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestTypedObject implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Collection theCollection;
    public HashMap map;
    public TestTypedObject me;
    private Object _prop1;
    //public transient double myNo = 0.2;
    private String _prop2="b";

    public TestTypedObject() {
        System.out.println("Constructor Call................\n\n\n\n");
    }
    public void setProp1(Object p) {
        _prop1 = p;
    }
    public Object getProp1() {
        return _prop1;
    }

    public void setProp2(String p) {
        _prop2 = p;
    }
    public String getProp2() {
        return _prop2;
    }
    public Object getReadOnlyProp1() {
        return "This is a ServerSide ReadOnly Property";
    }
}
