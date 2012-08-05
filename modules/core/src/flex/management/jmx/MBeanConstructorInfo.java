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
 * Remotable <code>MBeanConstructorInfo</code> class that complies with Flash serialization requirements.
 *
 * @author shodgson
 */
public class MBeanConstructorInfo
{
    /**
     * The name of the constructor.
     */
    public String name;
    
    /**
     * The description of the constructor.
     */
    public String description;
    
    /**
     * The constructor's parameter signature.
     */
    public MBeanParameterInfo[] signature;
    
    /**
     * Constructs an empty <code>MBeanConstructorInfo</code> instance.
     *
     */
    public MBeanConstructorInfo()
    {}
    
    /**
     * Constructs a <code>MBeanConstructorInfo</code> instance based upon a
     * <code>javax.management.MBeanConstructorInfo</code> instance.
     * 
     * @param mbeanConstructorInfo The <code>javax.management.MBeanConstructorInfo</code> to base this instance on.
     */
    public MBeanConstructorInfo(javax.management.MBeanConstructorInfo mbeanConstructorInfo)
    {
        name = mbeanConstructorInfo.getName();
        description = mbeanConstructorInfo.getDescription();
        signature = convertSignature(mbeanConstructorInfo.getSignature());
    }
    
    /**
     * Utility method to convert this <code>MBeanConstructorInfo</code> instance to a
     * <code>javax.management.MBeanConstructorInfo</code> instance.
     * 
     * @return A JMX <code>MBeanConstructorInfo</code> based upon this instance.
     */
    public javax.management.MBeanConstructorInfo toMBeanConstructorInfo()
    {
        return new javax.management.MBeanConstructorInfo(name,
                                                         description,
                                                         convertSignature(signature));
    }    
    
    /**
     * Utility method to convert the JMX constructor signature to our Flash friendly param type.
     * 
     * @param source The JMX constructor signature params.
     * @return Flash friendly signature params.
     */
    private MBeanParameterInfo[] convertSignature(javax.management.MBeanParameterInfo[] source)
    {
        MBeanParameterInfo[] signature = new MBeanParameterInfo[source.length];
        for (int i = 0; i < source.length; i++)
        {
            signature[i] = new MBeanParameterInfo(source[i]);
        }
        return signature;
    }
    
    /**
     * Utility method to convert a Flash friendly construtor param signature to the JMX params.
     * 
     * @param source The Flash friendly signature params.
     * @return The JMX constructor signature params.
     */
    private javax.management.MBeanParameterInfo[] convertSignature(MBeanParameterInfo[] source)
    {
        javax.management.MBeanParameterInfo[] signature = new javax.management.MBeanParameterInfo[source.length];
        for (int i = 0; i < source.length; i++)
        {
            signature[i] = source[i].toMBeanParameterInfo();
        }
        return signature;
    }   

}
