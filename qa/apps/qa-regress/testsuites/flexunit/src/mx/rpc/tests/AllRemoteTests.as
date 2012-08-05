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

public class AllRemoteTests
{

    public static function allTestsSuite():TestSuite
    {
        var suite:TestSuite = new TestSuite();
        suite.addTest(allSchemaTests());
        suite.addTest(allHTTPServiceTests());
        suite.addTest(allRemoteObjectTests());
        suite.addTest(allWebServiceTests());
        return suite;
    }
    
    public static function allSchemaTests():TestSuite
    {
        var suite:TestSuite = new TestSuite();
        suite.addTest(WalmsleySchemaTest.suite());
        return suite;
    }

    public static function allHTTPServiceTests():TestSuite
    {
        var suite:TestSuite = new TestSuite();
        suite.addTest(HTTPServiceTest.suite());
        suite.addTest(HTTPServiceNoProxyTest.suite());
        suite.addTest(HTTPServiceAsMXMLTest.suite());
        suite.addTest(MXMLHTTPServiceTest.suite());
        suite.addTest(URLUtilTest.suite());
        return suite;
    }

    public static function allRemoteObjectTests():TestSuite
    {
        var suite:TestSuite = new TestSuite();
        suite.addTest(RemoteObjectTest.suite());
        suite.addTest(RemoteObjectAsMXMLTest.suite());
        suite.addTest(MXMLRemoteObjectTest.suite());
        return suite;
    }

    public static function allWebServiceTests():TestSuite
    {
        var suite:TestSuite = new TestSuite();
        suite.addTest(WebServiceTest.suite());
        suite.addTest(WebServiceAsMXMLTest.suite());
        suite.addTest(MXMLWebServiceTest.suite());
        suite.addTest(WebServiceNoProxyTest.suite());
        return suite;
    }

}

}