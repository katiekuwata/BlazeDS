package
{
    import flash.utils.IDataInput;
    import flash.utils.IDataOutput;
    import flash.utils.IExternalizable;

    /**
     * A simple class that uses IExternalizable interface to read and write its properties.
     */ 
    [RemoteClass(alias="features.remoting.externalizable.ExternalizableClass")]
    public class ExternalizableClass implements IExternalizable
    {
        public var property1:String;
        public var property2:String;

        public function ExternalizableClass()
        {
        }

        public function readExternal(input:IDataInput):void
        {
            property1 = input.readObject() as String;
            property2 = input.readObject() as String;
        }

        public function writeExternal(output:IDataOutput):void
        {
            output.writeObject(property1);
            output.writeObject(property2);
        }

        public function toString():String
        {
            return "ExternalizableClass [property1: " + property1 + ", property2: " + property2 + "]";
        }
        
    }
}