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
package flex.messaging.io;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.HashMap;

/**
 * Flex's ObjectProxy class allows an anonymous, dynamic ActionScript Object
 * to be bindable and report change events. Since ObjectProxy only wraps
 * the ActionScript Object type we can map the class to a java.util.HashMap on the
 * server, since the user would expect this type to be deserialized as a
 * java.util.HashMap as it is...
 *
 * @author Peter Farland
 */
public class ObjectProxy extends HashMap implements Externalizable
{
    static final long serialVersionUID = 6978936573135117900L;

    public ObjectProxy()
    {
       super();
    }

    public ObjectProxy(int initialCapacity)
    {
        super(initialCapacity);
    }

    public ObjectProxy(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        Object value = in.readObject();
        if (value instanceof Map)
        {
            putAll((Map)value);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        // We can't output "this" to the serializer as it would
        // cause a loop back to writeExternal as this is an Externalizable
        // implementation itself!

        Map map = new HashMap();
        map.putAll(this);
        out.writeObject(map);
    }
}
