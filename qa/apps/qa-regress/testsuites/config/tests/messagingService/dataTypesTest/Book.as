package
{
    import flash.net.*;

    public class Book extends SuperBook
    {
        public var title:String;
        public var titleCopy:String;
        public var author:String;
        public var published:Date;
        public var pages:uint=3000;
        private var _ISBN:String;
        //turn on debug log to check, and this field should not be contained in the object
        private var shouldNotSerialized:String="shouldNotSerialized!!";

        public function Book()
        {
            registerClassAlias("blazeds.qa.remotingService.Book",Book);
        }

        public function set ISBN(isbn:String):void
        {
            if (isbn != null)
            {
                _ISBN = isbn;
            }
        }

        public function get ISBN():String
        {
            return _ISBN;
        }
    }
}