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

/**
 * Remotable <code>Attribute</code> class that complies with Flash serialization requirements.
 *
 * @author shodgson
 */
public class Attribute
{
    /**
     * The name of the attribute.
     */
    public String name;
    
    /**
     * The value of the attribute.
     */
    public Object value;
    
    /**
     * Constructs an empty <code>Attribute</code> instance.
     *
     */
    public Attribute()
    {}
    
    /**
     * Constructs an <code>Attribute</code> instance based upon a <code>javax.management.Attribute</code> instance.
     * 
     * @param attribute The JMX <code>Attribute</code> to base this instance on.
     */
    public Attribute(javax.management.Attribute attribute)
    {
        name = attribute.getName();
        value = attribute.getValue();
    }
    
    /**
     * Utility method to convert this <code>Attribute</code> instance to a <code>javax.management.Attribute</code> instance.
     * 
     * @return A JMX <code>Attribute</code> based upon this instance.
     */
    public javax.management.Attribute toAttribute()
    {
        return new javax.management.Attribute(name, value);
    }
}
