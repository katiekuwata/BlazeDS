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

package remoting.datatype;

import java.util.*;

public class MapTypeTest {

	private HashMap mapStringKey;
	private HashMap mapIntegerKey; 
	
	public MapTypeTest(){	
		mapStringKey = new HashMap(); 
		mapStringKey.put("one", "value1");
		mapStringKey.put("two", "value2");
		
		mapIntegerKey = new HashMap();
		mapIntegerKey.put(new Integer(1), "value1");
		mapIntegerKey.put(new Integer(2), "value2");
	}
		
	public Map getMapStringKey(){
		return mapStringKey; 
	}
	
	public Map getMapIntegerKey() {
		return mapIntegerKey;
	}	
}
