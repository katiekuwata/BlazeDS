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
 * Remotable <code>MBeanAttributeInfo</code> class that complies with Flash serialization requirements. 
 * The <code>isIs</code> property is not named <code>is</code> because <code>is</code> is 
 * an ActionScript keyword.
 *
 * @author shodgson
 */
public class MBeanAttributeInfo
{
    /**
     * The name of the attribute.
     */
    public String name;
    
    /**
     * The class name of the attribute.
     */
    public String type;
    
    /**
     * The description of the attribute.
     */
    public String description;

    /**
     * Whether the attribute can be read.
     */
    public boolean readable;
    
    /**
     * Whether the attribute can be written.
     */
    public boolean writable; 
    
    /**
     * Whether the attribute has an "is" getter.
     */
    public boolean isIs;
    
    /**
     * Constructs an empty <code>MBeanAttributeInfo</code> instance.
     */
    public MBeanAttributeInfo()
    {}
    
    /**
     * Constructs a <code>MBeanAttributeInfo</code> instance based upon a
     * <code>javax.management.MBeanAttributeInfo</code> instance.
     * 
     * @param mbeanAttributeInfo The JMX <code>MBeanAttributeInfo</code> instance to base this instance on.
     */
    public MBeanAttributeInfo(javax.management.MBeanAttributeInfo mbeanAttributeInfo)
    {
        name = mbeanAttributeInfo.getName();
        type = mbeanAttributeInfo.getType();
        description = mbeanAttributeInfo.getDescription();
        readable = mbeanAttributeInfo.isReadable();
        writable = mbeanAttributeInfo.isWritable();
        isIs = mbeanAttributeInfo.isIs();
    }
    
    /**
     * Utility method to convert this <code>MBeanAttributeInfo</code> to a
     * <code>javax.management.MBeanAttributeInfo</code> instance.
     * 
     * @return A JMX <code>MBeanAttributeInfo</code> based upon this instance.
     */
    public javax.management.MBeanAttributeInfo toMBeanAttributeInfo()
    {
        return new javax.management.MBeanAttributeInfo(name,
                                                       type,
                                                       description,
                                                       readable,
                                                       writable,
                                                       isIs);
    }

}
