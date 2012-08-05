package 
{
    import flash.utils.IDataInput;
    import flash.utils.IDataOutput;
    import flash.utils.IExternalizable;
    
    [RemoteClass(alias="blazeds.qa.remotingService.ExternalizableDate")]
    public class ExternalizableDate implements IExternalizable
    {
        private var _date:Date;
        public function ExternalizableDate( yearOrTimevalue:Object=null, 
				month:Number=NaN,
				date:Number = 1,
				hour:Number = 0,
				minute:Number = 0,
				second:Number = 0,
				millisecond:Number = 0 )     
	    {
					
			if( yearOrTimevalue != null ) {
				if( isNaN( month ) ) {
					_date = new Date(yearOrTimevalue);
				} else {
					_date = new Date( yearOrTimevalue, month, date, hour, minute, second, millisecond );
				}
			} else {
				_date = new Date();	
			}			
		}
		
		public function readExternal( input:IDataInput ):void 
		{
			var encoding:Object = input.readObject();
			_date.fullYear = Number(encoding.year);
			_date.month = Number(encoding.month);
			_date.date = Number(encoding.date);
		}
		
		public function writeExternal(output:IDataOutput):void 
		{
			var dateObj:Object = new Object();
			dateObj.year = "" + _date.fullYear;
			dateObj.month = "" + _date.month;
			dateObj.date = "" + _date.date;
			output.writeObject(dateObj);		
		}
		
	    public var hours:Number = 999;

    }
}