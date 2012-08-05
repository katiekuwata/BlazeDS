package blazeds.qa.remotingService;

import flex.messaging.io.amf.ASObject;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;


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
public class InstantiationTest
{
    public String getASObjectType(ASObject obj)
    {
        return obj.getType();
    }

    public String getClassName(Object obj)
    {
        return obj.getClass().getName();
    }
    /*
    Use TestTypedObject class as a testing object
        public Collection theCollection; values [Book, HashMap, [Book, HashMap]]
        public HashMap map;  {book:Book, hashmap:HashMap}
        public TestTypedObject me;
    */
    public Map getTypedObjectASObjectTypes(ASObject aso)
    {
        Map typeMap = new HashMap();
        typeMap.put("main", aso.getType());
        Object obj = aso.get("theCollection");
        Object[] theCollection = (Object[]) obj;
        typeMap.put("theCollection0", ((ASObject)theCollection[0]).getType());
        typeMap.put("theCollection1", ((ASObject)theCollection[1]).getType());
        typeMap.put("theCollection2.0", ((ASObject)((ArrayList)theCollection[2]).get(0)).getType());
        typeMap.put("theCollection2.1", ((ASObject)((ArrayList)theCollection[2]).get(1)).getType());
        typeMap.put("mapbook", ((ASObject)((ASObject) aso.get("map")).get("book")).getType());
        typeMap.put("hashmap", ((ASObject)((ASObject) aso.get("map")).get("hashmap")).getType());
        typeMap.put("me", ((ASObject) aso.get("me")).getType());
		typeMap.put("map", ((ASObject) aso.get("map")).getType());
        return typeMap;
    }

}
