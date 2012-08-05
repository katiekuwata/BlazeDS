package messaging;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import flex.messaging.Destination;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import flex.messaging.FlexSessionListener;
import flex.messaging.MessageBroker;
import flex.messaging.MessageClientListener;
import flex.messaging.MessageDestination;
import flex.messaging.client.FlexClient;
import flex.messaging.client.FlexClientListener;
import flex.messaging.client.FlexClientManager;
import flex.messaging.services.Service;
import flex.messaging.services.messaging.SubscriptionManager;
import flex.messaging.MessageClient;

public class ClientManagerRO implements FlexSessionListener, FlexClientListener, MessageClientListener{
	
	private final Map<String,FlexSession> flexSessions = new ConcurrentHashMap<String, FlexSession>();
	
	private final Map<String,FlexClient> flexClients = new ConcurrentHashMap<String, FlexClient>();
	
	private final Map<String,MessageClient> messageClients = new ConcurrentHashMap<String, MessageClient>();
	
	private final String serverName = "QA Regress";
	 
	public ClientManagerRO() {
		System.out.println("ClientManager created.");
		FlexSession.addSessionCreatedListener(this);
		FlexClient.addClientCreatedListener(this);
		MessageClient.addMessageClientCreatedListener(this);
	}

	public void sessionCreated(FlexSession session) 
    { 
		System.out.println("FlexSession created for server " + serverName + ": " + session.getId()); 
        flexSessions.put(session.getId(), session);
        // Add the FlexSession destroyed listener. 
        session.addSessionDestroyedListener(this); 
    } 
	
	public void sessionDestroyed(FlexSession session) 
    { 
		System.out.println("FlexSession destroyed for server " + serverName + ": " + session.getId()); 
        flexSessions.remove(session.getId());
    }  

	public void clientCreated(FlexClient client) 
    { 
		System.out.println("FlexClient created for server " + serverName + ": " + client.getId());
        flexClients.put(client.getId(), client);
        // Add the FlexClient destroyed listener. 
        client.addClientDestroyedListener(this); 
    } 	
	
	public void clientDestroyed(FlexClient client) 
    { 
		System.out.println("FlexClient destroyed for server " + serverName + ": " + client.getId());
        flexClients.remove(client.getId());
    } 

	public void messageClientCreated(MessageClient messageClient) 
    { 
		System.out.println("MessageClient created for server " + serverName + ": " + messageClient.getClientId());
        messageClients.put((String)messageClient.getClientId(), messageClient);
        // Add the MessageClient destroyed listener. 
        messageClient.addMessageClientDestroyedListener(this); 
    } 

	public void messageClientDestroyed(MessageClient messageClient) 
    { 
		System.out.println("MessageClient destroyed for server " + serverName + ": " + messageClient.getClientId());
        messageClients.remove(messageClient.getClientId());
    } 

	public Set<String> getSessions() {
		return flexSessions.keySet();	
	}
	
	public Set<String> getFlexClients() {
		return flexClients.keySet();	
	}
	
	public Set<String> getMessageClients() {
		return messageClients.keySet();	
	}
	
	public void invalidateMessageClientForSubscriber(String subscriberID) {
		MessageClient messageClient = messageClients.get(subscriberID);
		messageClient.invalidate(true);
    }
    
    public void invalidateFlexClientForSubscriber(String subscriberID) {
    	MessageClient messageClient = messageClients.get(subscriberID);
		messageClient.getFlexClient().invalidate();
    }
    
    public void invalidateFlexSessionForSubscriber(String subscriberID) {
    	MessageClient messageClient = messageClients.get(subscriberID);
		messageClient.getFlexSession().invalidate();
    }
	
    public String getDestinationForSubscriber(String subscriberID) {
    	MessageClient messageClient = messageClients.get(subscriberID);
		return messageClient.getDestinationId();
    }
}
