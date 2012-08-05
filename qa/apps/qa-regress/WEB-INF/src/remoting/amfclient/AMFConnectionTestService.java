/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

package remoting.amfclient;

/**
 * A class used as a remoting destination source for AMF connection JUnit tests.
 */
 import java.util.Date;
 
public class AMFConnectionTestService
{
    public String echoString(String text)
    {
        return text;
    }

    public int echoInt(int value)
    {
        return value;
    }

    public boolean echoBoolean(boolean value)
    {
        return value;
    }

    public Date echoDate(Date value)
    {
        return value;
    }

    public short echoShort(short value)
    {
        return value;
    }

    public double echoDouble(double value)
    {
        return value;
    }

    // Object argument, Object return.
    public Object echoObject1(Object customType)
    {
        return customType;
    }
    
    // Object argument, CustomType return.
    public ServerCustomType echoObject2(Object customType)
    {
        return (ServerCustomType)customType;
    }
    
    // CustomType argument, Object return
    public Object echoObject3(ServerCustomType customType)
    {
        return customType;
    }

    // CustomType argument, CustomType return
    public ServerCustomType echoObject4(ServerCustomType customType)
    {
        return customType;
    }

    // Object argument, Object return.
    public Object[] echoObject5(Object[] customType)
    {
        return customType;
    }

    // No argument, Object return
    public Object getObject1()
    {
        ServerCustomType ct = new ServerCustomType();
        ct.setId(1);
        return ct;
    }

    // No argument, CustomType return.
    public ServerCustomType getObject2()
    {
        ServerCustomType ct = new ServerCustomType();
        ct.setId(1);
        return ct;
    }
    
    // No argument, an Array of Objects.
    public Object[] getObjectArray1()
    {
        Object[] customTypes = new Object[10];
        for (int i = 0; i < customTypes.length; i++)
        {
            ServerCustomType sct = new ServerCustomType();
            sct.setId(i);
            customTypes[i] = sct;
        }
        return customTypes;
    }
/*
    public Document echoXML(Document d)
    {
        return d;
    }
    */
}
