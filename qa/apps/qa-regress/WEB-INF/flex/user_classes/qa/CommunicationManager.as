package qa
{
	import mx.messaging.Consumer;
	
	public class CommunicationManager
	{
		private var consumer:Consumer = null; 
		public var error:TypeError = null; 
		
		public function CommunicationManager()
		{
			try {
				consumer = new Consumer(); 
	   			consumer.destination = "MyTopic"; 
	   			consumer.subscribe();
	  		} catch(err:TypeError) {
	  			error = err; 
	  		}  
		}

	}
}