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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

/**
 * @exclude
 */
public class ManagedObjectProxy extends ObjectProxy 
{
    static final long serialVersionUID = 255140415084514484L;

    public ManagedObjectProxy()
    {
       super();
    }

    public ManagedObjectProxy(int initialCapacity)
    {
        super(initialCapacity);
    }

    public ManagedObjectProxy(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        int count = this.size();
        out.writeInt(count);

        // TODO: QUESTION: Jeff, We could copy the client approach to check a destination
        // for lazy associations to exclude them from serialization.

        Iterator it = keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            out.writeObject(key);
            out.writeObject(get(key));
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int count = in.readInt();

        for (int i = 0; i < count; i++)
        {
            Object key = in.readObject();
            Object value = in.readObject();
            put(key, value);
        }
    }
}
