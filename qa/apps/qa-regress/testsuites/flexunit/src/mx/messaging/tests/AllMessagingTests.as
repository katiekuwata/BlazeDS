/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
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


package mx.messaging.tests
{

import flexunit.framework.*;
import mx.rpc.tests.*;
/**
 * TestSuite used to run all the messaging tests. 
 */ 
public class AllMessagingTests
{
	////////////////////////////////////////////////////////////////////////////
    //
    // TestSuite
    //
    ////////////////////////////////////////////////////////////////////////////   
   
    public static function suite():TestSuite
    {
        var tests:TestSuite = new TestSuite();
        tests.addTest(AMF3Test.suite());
        tests.addTest(AMF0Test.suite());
        tests.addTest(URLUtilTest.suite());
        tests.addTest(HexEncoderDecoderTest.suite());
        tests.addTest(Base64EncoderDecoderTest.suite());
        tests.addTest(AMFChannelTest.suite());
        tests.addTest(ServerConfigTest.suite());
        tests.addTest(ChannelSetTest.suite());
        tests.addTest(MessageAgentTest.suite());
        tests.addTest(ConsumerWithServerTest.suite());
        tests.addTest(ConsumerTest.suite());
        tests.addTest(RemoteObjectTest.suite());
        tests.addTest(HTTPServiceNoProxyTest.suite());
        tests.addTest(HTTPServiceTest.suite());
        tests.addTest(HTTPServiceAsMXMLTest.suite());
        tests.addTest(MXMLHTTPServiceTest.suite());
        tests.addTest(WebServiceTest.suite());
        tests.addTest(WebServiceAsMXMLTest.suite());
        tests.addTest(MXMLWebServiceTest.suite());
        tests.addTest(WebServiceNoProxyTest.suite());
        return tests;
    }
}

}
