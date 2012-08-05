package proxy
{
	import flash.net.Socket;
    import flash.utils.ByteArray;
    public class ResponseSerializer
    {
        private var _headers:Object;
        private var _bytes:ByteArray;
        public function ResponseSerializer()
        {

        }

        public function setHeaders(headers:Object):void
        {
            _headers = headers;
        }

        public function writeStream(s:Socket):void
        {
            if (!s.connected)
            {
                throw new Error("Socket is not connected");
            }
            if (_headers == null)
            {
                throw new Error("Headers missing ");
            }
            _bytes = new ByteArray();
            for (var prop:String in _headers)
            {
                _bytes.writeMultiByte(prop + ":" + String(_headers[prop]) + "\r\n","US-ASCII");
            }
            _bytes.writeMultiByte("\r\n","US-ASCII");
            _headers = null;
            _bytes.position = 0;
            s.writeBytes(_bytes, 0, _bytes.length);
            s.flush();
        }
	}
}