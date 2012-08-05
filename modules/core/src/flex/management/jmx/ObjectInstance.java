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

import javax.management.MalformedObjectNameException;

/**
 * Remotable ObjectInstance representation that complies with Flash serialization requirements.
 * 
 * @author shodgson
 */
public class ObjectInstance
{
    /**
     * The object name part of the <code>ObjectInstance</code>.
     */
    public ObjectName objectName;
    
    /**
     * The class name part of the <code>ObjectInstance</code>.
     */
    public String className;
    
    /**
     * Constructs an empty <code>ObjectInstance</code> instance.
     *
     */
    public ObjectInstance()
    {}
    
    /**
     * Constructs a <code>ObjectInstance</code> instance based upon a
     * <code>javax.management.ObjectInstance</code> instance.
     * 
     * @param objectInstance The JMX <code>ObjectInstance</code> instance to base this instance on.
     */
    public ObjectInstance(javax.management.ObjectInstance objectInstance)
    {
        objectName = new ObjectName(objectInstance.getObjectName());
        className = objectInstance.getClassName();
    }
    
    /**
     * Utility method to convert this <code>ObjectInstance</code> to a
     * <code>javax.management.ObjectInstance</code> instance.
     * 
     * @return A JMX <code>ObjectInstance</code> based upon this instance.
     */
    public javax.management.ObjectInstance toObjectInstance() throws MalformedObjectNameException
    {
        return new javax.management.ObjectInstance(objectName.toObjectName(), className);
    }
}
