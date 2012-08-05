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
package flex.management.jmx;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;

/**
 * Remotable ObjectName representation that complies with Flash serialization requirements. 
 * This class is JMX 1.1 compliant.
 * 
 * @author shodgson
 */
public class ObjectName
{
    /**
     * String representation of the list of key properties sorted in lexical order.
     */
    public String canonicalKeyPropertyListString;
    
    /**
     * Canonical form of the name with properties sorted in lexical order.
     */
    public String canonicalName;
    
    /**
     * The domain part of the object name.
     */
    public String domain;
    
    /**
     * A Hashtable containing key-property pairs.
     */
    public Hashtable keyPropertyList;
    
    /**
     * String representation of the key properties. 
     */
    public String keyPropertyListString;    
    
    /**
     * Indicates whether the object name is a pattern.
     */
    public boolean pattern;
    
    /**
     * Indicates whether the object name is a pattern on key properties.
     */
    public boolean propertyPattern;
    
    /**
     * Constructs an empty <code>ObjectName</code> instance.
     */
    public ObjectName()
    {}
    
    /**
     * Constructs a <code>ObjectName</code> instance based upon a
     * <code>javax.management.ObjectName</code> instance.
     * 
     * @param objectName The JMX <code>ObjectName</code> instance to base this instance on.
     */
    public ObjectName(javax.management.ObjectName objectName)
    {
        canonicalKeyPropertyListString = objectName.getCanonicalKeyPropertyListString();
        canonicalName = objectName.getCanonicalName();
        domain = objectName.getDomain();
        keyPropertyList = objectName.getKeyPropertyList();
        keyPropertyListString = objectName.getKeyPropertyListString();
        pattern = objectName.isPattern();        
        propertyPattern = objectName.isPropertyPattern();
    }
    
    /**
     * Utility method to convert this <code>ObjectName</code> to a
     * <code>javax.management.ObjectName</code> instance.
     * 
     * @return A JMX <code>ObjectName</code> based upon this instance.
     */
    public javax.management.ObjectName toObjectName() throws MalformedObjectNameException
    {
        return new javax.management.ObjectName(domain, keyPropertyList);
    }

}
