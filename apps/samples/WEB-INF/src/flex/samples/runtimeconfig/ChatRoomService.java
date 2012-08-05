package flex.samples.runtimeconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import flex.messaging.MessageBroker;
import flex.messaging.MessageDestination;
//import flex.messaging.config.ServerSettings;
import flex.messaging.services.MessageService;

/**
 * Simplistic implementation of a chat room management service. Clients can add rooms,
 * and obtain a list of rooms. The interesting part of this example is the "on-the-fly" 
 * creation of a message destination. The same technique can be used to create DataService
 * and Remoting destinations. 
 */
public class ChatRoomService {

	private List rooms;
	
	public ChatRoomService()
	{
		rooms = Collections.synchronizedList(new ArrayList());
	}

	public List getRoomList()
	{
		return rooms;
	}
	
	public void createRoom(String id) {

		if (roomExists(id))
		{
			throw new RuntimeException("Room already exists");
		}
		
		// Create a new Message destination dynamically
		String serviceId = "message-service";
		MessageBroker broker = MessageBroker.getMessageBroker(null);
		MessageService service = (MessageService) broker.getService(serviceId);
		MessageDestination destination = (MessageDestination) service.createDestination(id);

		if (service.isStarted())
		{
			destination.start();
		}

		rooms.add(id);
		
	}
	
	public boolean roomExists(String id)
	{
		int size = rooms.size();
		for (int i=0; i<size; i++)
		{
			if ( ((String)rooms.get(i)).equals(id) ) 
			{
				return true;
			}
		}
		return false;
	}
}