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

import flex.messaging.io.PropertyProxyRegistry;
import java.util.*;

public class EnumTypeTest {
	EnumType etype;

	public EnumTypeTest(){	
		PropertyProxyRegistry registry = PropertyProxyRegistry.getRegistry();
		registry.register(EnumType.class, new EnumProxy());
	}
	
	public EnumTypeTest(EnumType etype) {
		this.etype = etype;
	}
	
	public void tellColor() {
		switch (etype) {
			case APPLE: System.out.println("Apple is red.");
					     break;					
			case ORANGE: System.out.println("Orange is orange.");
					     break;					     
			case BANANA: System.out.println("Banana is yellow.");
					     break;					     
			default:	 System.out.println("No fruit?");
					     break;
		}
	}
	
	public static void main(String[] args) {
		EnumTypeTest apple = new EnumTypeTest(EnumType.APPLE);
		apple.tellColor();
		EnumTypeTest orange = new EnumTypeTest(EnumType.ORANGE);
		orange.tellColor();
		EnumTypeTest banana = new EnumTypeTest(EnumType.BANANA);
		banana.tellColor();		
		System.out.println(  "EnumType.APPLE=" + banana.echoEnum(EnumType.APPLE) + " EnumType.Apple.value=" +                                       banana.echoEnum(EnumType.APPLE).value );
		System.out.println(  "EnumType.APPLE=" + banana.getEnum("APPLE") + " EnumType.Apple.value=" + banana.getEnum("APPLE").value                                     );
		banana.getApplePrice();
	}

	public EnumType getEnum(String type){
		
		if (type.toUpperCase().equals("APPLE")){		
			 return EnumType.APPLE;			
		} else if (type.toUpperCase().equals("ORANGE")){
			return EnumType.ORANGE;
		} else {
			return EnumType.BANANA;
		}	
	}

	public EnumType echoEnum(EnumType type){
		switch (type) {
			case APPLE: return EnumType.APPLE;					    				
			case ORANGE: return EnumType.ORANGE;
			default:	return EnumType.BANANA;
		}	
	}

	public void getApplePrice() {			
		System.out.println("Apple " + getEnumApple() + " costs " + getEnumApple().getPrice());

		// Display all Apples and prices.
		System.out.println("All apple prices:");
		for (EnumApple a : EnumApple.values())
		  System.out.println(a + " costs " + a.getPrice() + " cents.");
	 }

	 public EnumApple getEnumApple(){
		return EnumApple.A;
	 }
         
         public Map enumKeyMap() {
             Map map = new HashMap();
             map.put(EnumApple.A,  "AppleA");
             map.put(EnumApple.B, "AppleB");
             return map;
         }
}
