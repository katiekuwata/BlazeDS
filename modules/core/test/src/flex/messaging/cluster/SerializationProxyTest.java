/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2009 Adobe Systems Incorporated
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
package flex.messaging.cluster;

import flex.messaging.io.SerializationProxy;
import flex.messaging.io.amf.ASObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;


public class SerializationProxyTest extends TestCase
{
    public SerializationProxyTest(String valueExpression)
    {
        super(valueExpression);
    }

    /**
     * For clustering, SerializationProxys must be serialized and
     * deserialized.  This is because any message with a SerializationProxy,
     * for example referencedIds, on deleteItem must be passed via
     * jgroups, which uses Java serialization, to the next node in the
     * cluster.
     * Bug: LCDS-910
     */
    public void testRoundTripSerialization() throws Exception
    {
        ASObject as = new ASObject();
        as.put("name", "cathy");
        SerializationProxy proxy = new SerializationProxy(as);

        // serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(proxy);
        oos.close();

        //deserialize
        byte[] bytes = out.toByteArray();
        InputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(in);
        Object o = ois.readObject();

        assertNotNull("The object should be correctly serialized/deserialized.", o);
    }
}
