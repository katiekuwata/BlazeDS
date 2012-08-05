/**
 * A class that is meant to be used by a remoting destination to create destinations
 * dynamically after server startup. The remoting destination is meant to be
 * invoked by Flex clients to create dynamic destinations and use them.
 */
package features.runtimeconfig;

import flex.messaging.MessageBroker;
import flex.messaging.MessageDestination;
import flex.messaging.services.MessageService;
import flex.messaging.services.messaging.adapters.JMSAdapter;
import flex.messaging.services.messaging.adapters.JMSSettings;

public class RuntimeConfigurator
{
    MessageBroker msgBroker;
    
    public RuntimeConfigurator()
    {
        msgBroker = MessageBroker.getMessageBroker(null);
    }

    /*
    <destination id="messaging_AMF_Poll_Runtime" channels="my-amf-poll"/>
     */
    public String createMessageDestination()
    {
        String serviceId = "message-service";
        String id = "messaging_AMF_Poll_Runtime";

        MessageService msgService = (MessageService)msgBroker.getService(serviceId);
        MessageDestination msgDestination = (MessageDestination)msgService.createDestination(id);
        msgDestination.addChannel("my-amf-poll");

        if (msgService.isStarted())
            msgDestination.start();

        return "Destination: " + id + " created for Service: " + serviceId;
    }

    /*
    <destination id="messaging_AMF_Poll_Runtime" channels="my-amf-poll"/>
     */
    public String removeMessageDestination()
    {
        String serviceId = "message-service";
        String id = "messaging_AMF_Poll_Runtime";

        MessageService msgService = (MessageService)msgBroker.getService(serviceId);
        msgService.removeDestination(id);

        return "Destination: " + id + " removed from Service: " + serviceId;
    }

    /*
    <destination id="messaging_AMF_Poll_JMS_Topic_Runtime" channels="my-amf-poll">
    <adapter ref="jms"/>
    <properties>
        <jms>
            <connection-factory>java:comp/env/jms/flex/TopicConnectionFactory</connection-factory>
            <destination-type>Topic</destination-type>
            <destination-jndi-name>java:comp/env/jms/topic/flex/simpletopic</destination-jndi-name>
            <message-type>javax.jms.TextMessage</message-type>
        </jms>
    </properties>
    </destination>
    */    
    public String createMessageDestinationWithJMSAdapter()
    {
        String serviceId = "message-service";
        String id = "messaging_AMF_Poll_JMS_Topic_Runtime";

        MessageService msgService = (MessageService)msgBroker.getService(serviceId);
        MessageDestination msgDestination = (MessageDestination)msgService.createDestination(id);
        msgDestination.addChannel("my-amf-poll");

        // Use JMSSettings object for the <jms> properties above
        JMSSettings js = new JMSSettings();
        js.setConnectionFactory("java:comp/env/jms/flex/TopicConnectionFactory");
        js.setDestinationType("Topic");        
        js.setMessageType("javax.jms.TextMessage");        
        js.setDestinationJNDIName("java:comp/env/jms/topic/flex/simpletopic");

        JMSAdapter adapter = new JMSAdapter();
        adapter.setId("jms");
        adapter.setJMSSettings(js);
        adapter.setDestination(msgDestination);

        if (msgService.isStarted())
            msgDestination.start();

        return "Destination: " + id + " created for Service: " + serviceId;
    }
}
