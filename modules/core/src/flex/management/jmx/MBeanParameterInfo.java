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
 * Remotable MBeanParameterInfo class that complies with Flash serialization requirements.
 *
 * @author shodgson
 */
public class MBeanParameterInfo
{
    /**
     * The name of the parameter.
     */
    public String name;
    
    /**
     * The Java type for the parameter.
     */
    public String type;
    
    /**
     * The description for the parameter.
     */
    public String description;
    
    /**
     * Constructs an empty <code>MBeanParameterInfo</code> instance.
     */
    public MBeanParameterInfo()
    {}
    
    /**
     * Constructs a <code>MBeanParameterInfo</code> instance based upon a
     * <code>javax.management.MBeanParameterInfo</code> instance.
     * 
     * @param mbeanParameterInfo The JMX <code>MBeanParameterInfo</code> instance to base this instance on.
     */
    public MBeanParameterInfo(javax.management.MBeanParameterInfo mbeanParameterInfo)
    {
        name = mbeanParameterInfo.getName();
        type = mbeanParameterInfo.getType();
        description = mbeanParameterInfo.getDescription();
    }
    
    /**
     * Utility method to convert this <code>MBeanParameterInfo</code> to a
     * <code>javax.management.MBeanParameterInfo</code> instance.
     * 
     * @return A JMX <code>MBeanParameterInfo</code> based upon this instance.
     */
    public javax.management.MBeanParameterInfo toMBeanParameterInfo()
    {
        return new javax.management.MBeanParameterInfo(name,
                                                       type,
                                                       description);
    }

}
