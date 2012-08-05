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
package qa.messaging;

import flex.messaging.io.amf.translator.ASTranslator;
/**
 * The <code>CustomTypeMarshaller</code> class is used to test that 
 * if a channel specifies a type-marshaller in it's serialization settings
 * the class that is specified is used. This class doesn't actually do anything
 * special it just calls super on the methods that it overrides in <code>ASTranslator</code>.
 * This class is just used to make sure that if a type-marshaller is specified that it
 * gets used.
 */
public class CustomTypeMarshaller extends ASTranslator {
	/**
	 * Override createInstance in ASTranslator. Just pass params along 
	 * to the superclass.
	 */
	public Object createInstance(Object source, Class desiredClass)
    {
        
	    return super.createInstance(source, desiredClass);
      
    }
	/**
	 * Override convert in ASTranslator. Just pass params along 
	 * to the superclass.
	 */
    public Object convert(Object source, Class desiredClass)
    {
        return super.convert(source, desiredClass);
    }
}
