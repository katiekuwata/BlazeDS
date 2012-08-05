package qa.data {

import mx.utils.UIDUtil;
import flash.utils.describeType;



public class Company extends Object {

    public var name:String;
    public var address:Address;
    public var isActive:Boolean;
    public var businessSummary:String;
    public var industry:String;
    public var employeeCount:uint;
    public var webSite:String;
    public var id:String;


    public function Company(name:String = "", isActive:Boolean = true, businessSummary:String = "", industry:String = "", employeeCount:uint = 0, webSite:String = "", theID:String = null):void {
        this.name = name;
        this.isActive = isActive;
        this.businessSummary = businessSummary;
        this.industry = industry;
        this.employeeCount = employeeCount;
        this.webSite = webSite;

        if (theID != null) {
            this.id = theID;
        } else {
            this.id = UIDUtil.createUID();;
        }
    }


	public function toString():String {
	    var output:String = "name = " + name + "\n" +
	        "isActive = " + String(isActive) + "\n" +
	        "businessSummary = " + businessSummary + "\n" +
	        "industry = " + industry + "\n" +
	        "employeeCount = " + String(employeeCount) + "\n" +
	        "webSite = " + webSite + "\n" +  
	        "id = " + id + "\n";
	         // + "uid = " + uid + "\n";
	    return output;
	}


	public function equalsUntyped (otherCompany:Object):Boolean {
	    try {
	        if (this.id == otherCompany.id &&
                    this.name == otherCompany.name &&
                    this.isActive == otherCompany.isActive &&
                    this.businessSummary == otherCompany.businessSummary &&
                    this.industry == otherCompany.industry &&
                    this.employeeCount == otherCompany.employeeCount &&
                    this.webSite == otherCompany.webSite) {
	            return true;
            } else {
                return false;
            }

	    } catch (error:Error) {
	        return false;
	    }

	    return false;
	}

	public function equals(otherCompany:Object):Boolean {
	    if (XML(describeType(otherCompany)).@name.toString() == XML(describeType(this)).@name.toString() &&
	            equalsUntyped(otherCompany) ) {
            return true;
        } else {
            return false;
        }
	}
}
}
