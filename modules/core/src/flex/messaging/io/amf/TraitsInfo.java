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
package flex.messaging.io.amf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * AVM+ Serialization optimizes object serialization by
 * serializing the traits of a type once, and then
 * sending only the values of each instance of the type
 * as it occurs in the stream.
 *
 * @author Peter Farland
 * @exclude
 */
public class TraitsInfo
{
    private final String className;
    private final boolean dynamic;
    private final boolean externalizable;
    private List<String> properties;

    public TraitsInfo(String className)
    {
        this(className, false, false, 10);
    }

    public TraitsInfo(String className, int initialCount)
    {
        this(className, false, false, initialCount);
    }

    public TraitsInfo(String className, boolean dynamic, boolean externalizable, int initialCount)
    {
        this(className, dynamic, externalizable, new ArrayList<String>(initialCount));
    }
    
    public TraitsInfo(String className, boolean dynamic, boolean externalizable, List<String> properties)
    {
        if (className == null)
            className = "";

        this.className = className;
        this.properties = properties;
        this.dynamic = dynamic;
        this.externalizable = externalizable;
    }

    public boolean isDynamic()
    {
        return dynamic;
    }

    public boolean isExternalizable()
    {
        return externalizable;
    }

    public int length()
    {
        return properties != null? properties.size() : 0;
    }

    public String getClassName()
    {
        return className;
    }

    public void addProperty(String name)
    {
        if (properties == null)
            properties = new ArrayList<String>();
        properties.add(name);
    }
    
    public void addAllProperties(Collection props)
    {
        if (properties == null)
            properties = new ArrayList<String>();
        properties.addAll(props);
    }

    public String getProperty(int i)
    {
        return properties != null? properties.get(i) : null;
    }
    
    public List<String> getProperties()
    {
        return properties;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof TraitsInfo)
        {
            TraitsInfo other = (TraitsInfo)obj;

            if (!this.className.equals(other.className))
            {
                return false;
            }

            if (!(this.dynamic == other.dynamic))
            {
                return false;
            }

            List thisProperties = this.properties;
            List otherProperties = other.properties;
            
            if (thisProperties != otherProperties)
            {
                int thisCount = thisProperties.size();

                if (thisCount != otherProperties.size())
                {
                    return false;
                }

                for (int i = 0; i < thisCount; i++)
                {
                    Object thisProp = thisProperties.get(i);
                    Object otherProp = otherProperties.get(i);
                    if (thisProp != null && otherProp != null && !thisProp.equals(otherProp))
                        return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Instances of types with the same classname and number of properties may
     * return the same hash code, however, an equality test will fully
     * test whether they match exactly on individual property names.
     */
    public int hashCode()
    {
        int size = properties != null? properties.size() : 0;
        int c = className.hashCode();
        c = dynamic ? c << 2 : c << 1;
        c = c | (size << 24);
        return c;
    }
}
