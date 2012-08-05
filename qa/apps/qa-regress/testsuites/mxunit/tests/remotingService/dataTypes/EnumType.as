package {

/* 
 * Strongly typed enum class with one limitation - you must use the equals method
 * to do the comparison
 */
[RemoteClass(alias="remoting.datatype.EnumType")]
public class EnumType
{
    public static const APPLE:EnumType = new EnumType("APPLE");
    public static const ORANGE:EnumType = new EnumType("ORANGE");
    public static const BANANA:EnumType = new EnumType("BANANA");

    public var fruit:String;

    public function EnumType(v:String="unset")
    {
        fruit = v;
    }

    public function equals(other:EnumType):Boolean
    {
        return other.fruit == fruit;
    }

    public function toString():String
    {
        return fruit;
    }
}

}

