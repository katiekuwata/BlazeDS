package
{

import flash.net.*;

public class MissingTestTypedObject
{
    private var _prop1:int;
    public var theCollection:Array;
    public var me:MissingTestTypedObject;
    public var prop2:String;
    internal var prop3:String="I am not public";
    public var map:Object;
    public var extra:String;  //this variable doesn't exist on server side

    public function MissingTestTypedObject()
    {
        registerClassAlias("missing.TestTypedObject",MissingTestTypedObject);
    }

    public function set prop1(value:int):void
    {
        _prop1 = value;
    }

    public function get prop1():int
    {
        return _prop1;
    }

    public function get readOnlyProp1():int
    {
        return _prop1;
    }
}
}