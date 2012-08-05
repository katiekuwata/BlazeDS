package qa.data {

import mx.utils.UIDUtil;
import flash.utils.describeType;



public class Address extends Object {

    public var street1:String;
    public var street2:String;
    public var city:String;
    public var state:String;
    public var zip:String;
    public var id:String;

    public function Address(street1:String = "", street2:String = "", city:String = "", state:String = "", zip:String = "", theID:String = null):void {
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zip = zip;

        if (theID != null) {
            this.id = theID;
        } else {
            this.id = UIDUtil.createUID();;
        }
    }


	public function toString():String {
	    var output:String = "street1 = " + street1 + "\n" +
	        "street2 = " + street2 + "\n" +
	        "city = " + city + "\n" +
	        "state = " + state + "\n" +
	        "zip = " + zip + "\n" +
	        "id = " + id + "\n";
	    return output;
	}


	public function equalsUntyped (otherAddress:Object):Boolean {
	    try {
	        if (this.id == otherAddress.id &&
                    this.street1 == otherAddress.street1 &&
                    this.street2 == otherAddress.street2 &&
                    this.city == otherAddress.city &&
                    this.state == otherAddress.state &&
                    this.zip == otherAddress.zip) {
	            return true;
            } else {
                return false;
            }

	    } catch (error:Error) {
	        return false;
	    }

	    return false;
	}

	public function equals(otherAddress:Object):Boolean {
	    if (XML(describeType(otherAddress)).@name.toString() == XML(describeType(this)).@name.toString() &&
	            equalsUntyped(otherAddress) ) {
            return true;
        } else {
            return false;
        }
	}
}
}
