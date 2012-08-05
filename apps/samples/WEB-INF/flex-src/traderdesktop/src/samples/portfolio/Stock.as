package samples.portfolio
{
	[RemoteClass(alias="flex.samples.marketdata.Stock")]
	[Bindable]
	public class Stock
	{	    
		public var symbol:String;
		public var name:String;
		public var low:Number;
		public var high:Number;
		public var open:Number;
		public var last:Number;
		public var change:Number = 0;
		public var date:Date;
	}
	
}
