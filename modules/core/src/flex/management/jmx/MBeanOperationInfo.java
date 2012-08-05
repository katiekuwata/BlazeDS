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
 * Remotable MBeanOperationInfo class that complies with Flash serialization requirements.
 *
 * @author shodgson
 */
public class MBeanOperationInfo
{
    /**
     * The operation name.
     */
    public String name;
    
    /**
     * The operation description.
     */
    public String description;
    
    /**
     * The operation's argument signature.
     */
    public MBeanParameterInfo[] signature;
    
    /**
     * The operation's return type.
     */
    public String returnType;
    
    /**
     * The impact of the operation; one of <code>INFO, ACTION, ACTION_INFO, UNKNOWN</code>.
     */
    public int impact;
    
    /**
     * Constructs an empty <code>MBeanOperationInfo</code> instance.    
     */
    public MBeanOperationInfo()
    {}
    
    /**
     * Constructs a <code>MBeanOperationInfo</code> instance based upon a
     * <code>javax.management.MBeanOperationInfo</code> instance.
     * 
     * @param mbeanOperationInfo The JMX <code>MBeanOperationInfo</code> instance to base this instance on.
     */
    public MBeanOperationInfo(javax.management.MBeanOperationInfo mbeanOperationInfo)
    {
        name = mbeanOperationInfo.getName();
        description = mbeanOperationInfo.getDescription();
        signature = convertSignature(mbeanOperationInfo.getSignature());
        returnType = mbeanOperationInfo.getReturnType();
        impact = mbeanOperationInfo.getImpact();
    }
    
    /**
     * Utility method to convert this <code>MBeanOperationInfo</code> to a
     * <code>javax.management.MBeanOperationInfo</code> instance.
     * 
     * @return A JMX <code>MBeanOperationInfo</code> based upon this instance.
     */
    public javax.management.MBeanOperationInfo toMBeanOperationInfo()
    {
        return new javax.management.MBeanOperationInfo(name,
                                                       description,
                                                       convertSignature(signature),
                                                       returnType,
                                                       impact);
    }
    
    /**
     * Utility method to convert JMX parameter info instances to Flash friendly parameter info instances.
     * 
     * @param source JMX parameter info instances.
     * @return Flash friendly parameter info instances.
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
     * Utility method to convert Flash friendly parameter info instances to JMX parameter info instances.
     * 
     * @param source Flash friendly parameter info instances.
     * @return JMX parameter info instances.
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
