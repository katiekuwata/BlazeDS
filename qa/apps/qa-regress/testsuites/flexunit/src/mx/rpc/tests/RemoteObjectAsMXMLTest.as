////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (C) 2003-2005 Macromedia, Inc. All Rights Reserved.
//  The following is Sample Code and is subject to all restrictions
//  on such code as contained in the End User License Agreement
//  accompanying this product.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc.tests
{

import flexunit.framework.*;
import mx.rpc.remoting.*;
import mx.rpc.remoting.mxml.*;

public class RemoteObjectAsMXMLTest extends RemoteObjectTest
{

    public static function suite() : TestSuite
    {
        var suite : TestSuite = new TestSuite();
        suite.addTest(new RemoteObjectAsMXMLTest("testDefaultResultEvent"));
        suite.addTest(new RemoteObjectAsMXMLTest("testDefaultFaultEvent"));
        suite.addTest(new RemoteObjectAsMXMLTest("testResultProperty"));
        suite.addTest(new RemoteObjectAsMXMLTest("testOperationResultEvent"));
        suite.addTest(new RemoteObjectAsMXMLTest("testOperationFaultEvent"));
        suite.addTest(new RemoteObjectAsMXMLTest("testOperationResultNotDefault"));
        suite.addTest(new RemoteObjectAsMXMLTest("testOperationFaultNotDefault"));
        suite.addTest(new RemoteObjectAsMXMLTest("testSendNoArgumentsParams"));
        suite.addTest(new RemoteObjectAsMXMLTest("testSendArgumentsNoParams"));
        suite.addTest(new RemoteObjectAsMXMLTest("testSendArgumentsParams"));
        suite.addTest(new RemoteObjectAsMXMLTest("testArgumentsAsArray"));
        suite.addTest(new RemoteObjectAsMXMLTest("testResultCall"));
        suite.addTest(new RemoteObjectAsMXMLTest("testFaultCall"));
        suite.addTest(new RemoteObjectAsMXMLTest("testResultMessage"));
        suite.addTest(new RemoteObjectAsMXMLTest("testFaultMessage"));
        suite.addTest(new RemoteObjectAsMXMLTest("testCancel"));
        return suite;
    }

    public function RemoteObjectAsMXMLTest(name : String)
    {
        super(name);
    }

    override public function getRemoteObject() : mx.rpc.remoting.RemoteObject
    {
        return new mx.rpc.remoting.mxml.RemoteObject();
    }

}

}