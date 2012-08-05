/*
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
 */

package
{
    public dynamic class TestCase203212
    {
        public var lastName:String = "Anonymous";
        public var firstName:String = "Anonymous";
        public var phoneNumber:String = "000-000-0000";
        private var _dynamicProps:Object;
        private var _anotherProp:String = "";
        
        public function TestCase203212(dynProps:Object, anotherProp:String)
        {
            _dynamicProps = dynProps;
            _anotherProp = anotherProp;
        }
        public function get dynamicProps():Object
        {
            return _dynamicProps;
        }

        public function set dynamicProps(value:Object):void
        {
            _dynamicProps = value;
        }
        public function get anotherProp():String
        {
            return _anotherProp;
        }

        public function set anotherProp(value:String):void
        {
            _anotherProp = value;
        }

    }
}