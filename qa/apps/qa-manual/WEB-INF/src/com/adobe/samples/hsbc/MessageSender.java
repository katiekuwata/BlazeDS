package com.adobe.samples.hsbc;

import java.util.Date;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;

public class MessageSender 
{
	public void sendMessageToClients(String messageBody, String dst)
	{
		AsyncMessage msg = new AsyncMessage();

		msg.setClientId("Java-Based-Producer-For-Messaging");
		msg.setTimestamp(new Date().getTime());
		//you can create a unique id
		msg.setMessageId("Java-Based-Producer-For-Messaging-ID");
		//destination to which the message is to be sent
		msg.setDestination(dst);	
System.out.println("ok1");	
		//set message body
		msg.setBody(messageBody != null?messageBody:"");
		//set message header
		msg.setHeader("sender", "From the server");
System.out.println("ok2");
		//send message to destination
		MessageBroker.getMessageBroker(null).routeMessageToService(msg, null);
System.out.println("ok3" + msg.getBody());				
	}

}

