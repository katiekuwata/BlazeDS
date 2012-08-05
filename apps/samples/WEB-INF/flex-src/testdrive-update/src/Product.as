package
{
	[Bindable]
	[RemoteClass(alias="flex.samples.product.Product")]
	public class Product
	{
		public function Product()
		{
		}

		public var productId:int;

		public var name:String;

		public var description:String;

		public var image:String;

		public var category:String;

		public var price:Number;

		public var qtyInStock:int;

	}
}
