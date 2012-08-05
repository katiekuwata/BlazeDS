/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
 
package flex.messaging.services;

import java.util.List;
import java.util.Map;

import flex.management.BaseControl;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationException;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;

/**
 * The purpose of <code>AbstractBootstrapService</code> is to enable creation
 * of dynamic services, destinations, and adapters. <code>MessageBroker</code> 
 * creates an instance of this class and calls <code>initialize</code> after all 
 * of the server components are created but right before they are started. 
 * <code>MessageBroker</code> also calls <code>start</code> as server starts and 
 * <code>stop</code> as server stops. Subclasses should have their dynamic 
 * component creation code in one of <code>initialize</code>, <code>start</code>, 
 * and <code>stop</code> methods depending on when they want their components 
 * to be created.  
 * 
 * @author matamel
 *
 */
public abstract class AbstractBootstrapService implements Service
{
    // Errors
    private static final int NULL_COMPONENT_PROPERTY = 11116;
    
    protected String id;
    protected MessageBroker broker;
    
    /**
     * Default constructor which is no-op. 
     */
    public AbstractBootstrapService()
    {
        // No-op
    }
    
    /**
     * Returns the id of the <code>AbstractBootstrapService</code>.
     * 
     * @return The id of the <code>AbstractBootstrapService</code>.
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * Sets the id of the <code>AbstractBootstrapService</code>. If the 
     * <code>AbstractBootstrapService</code> has a <code>MessageBroker</code> 
     * already assigned, it also updates the id in the <code>MessageBroker</code>.
     */
    public void setId(String id)
    {
        String oldId = getId();
        
        if (id == null)
        {
            // Id of a component cannot be null.
            ConfigurationException ce = new ConfigurationException();
            ce.setMessage(NULL_COMPONENT_PROPERTY, new Object[]{"id"});
            throw ce;
        }      
        
        this.id = id;
        
        // Update the service id in the broker
        MessageBroker broker = getMessageBroker();
        if (broker != null)
        {
            // broker must have the service then
            broker.removeService(oldId);
            broker.addService(this);
        }            
    }
    
    /**
     * Returns the <code>MessageBroker</code> managing this <code>AbstractBootstrapService</code>.
     * 
     * @return MessageBroker of the <code>AbstractBootstrapService</code>.
     */
    public MessageBroker getMessageBroker()
    {
        return broker;
    }
    
    /**
     * Sets the <code>MessageBroker</code> managing this <code>AbstractBootstrapService</code>.
     * Removes the <code>AbstractService</code> from the old broker (if there was one) 
     * and adds to the list of services in the new broker.
     * 
     * @param broker <code>MessageBroker</code> of the <code>AbstractBootstrapService</code>.
     */
    public void setMessageBroker(MessageBroker broker)
    {
        MessageBroker oldBroker = getMessageBroker();                                    

        this.broker = broker;

        if (oldBroker != null)
        {
            oldBroker.removeService(getId());
        }       
                
        // Add service to the new broker if needed
        if (broker.getService(getId()) != this)
            broker.addService(this);        
    }
    
    /**
     * Always unmanaged.
     * 
     * @return <code>false</code>.
     */
    public boolean isManaged()
    {
        return false;
    }

    /**
     * Management is always disabled.
     */
    public void setManaged(boolean enableManagement)
    {         
        // No-op
    }
    
    /**
     * Called by the <code>MessageBroker</code> after all of the server 
     * components are created but right before they are started. This is 
     * usually the place to create dynamic components.
     * 
     * @param id Id of the <code>AbstractBootstrapService</code>.
     * @param properties Properties for the <code>AbstractBootstrapService</code>. 
     */
    public abstract void initialize(String id, ConfigMap properties);
        
    /**
     * Called by the <code>MessageBroker</code> as server starts. Useful for
     * custom code that needs to run after all the components are initialized
     * and the server is starting up. 
     */  
    public abstract void start();

    /**
     * Called by the <code>MessageBroker</code> as server stops. Useful for 
     * custom code that needs to run as the server is shutting down.
     */
    public abstract void stop();
       
    /** @exclude **/
    public ConfigMap describeService(Endpoint endpoint)
    {
        return null;
    }
    
    /** @exclude **/
    public BaseControl getControl()
    {
        throw new UnsupportedOperationException();
    }
    
    /** @exclude **/
    public void setControl(BaseControl control)
    {        
        throw new UnsupportedOperationException();
    }
    
    /** @exclude **/
    public void addDefaultChannel(String id)
    {        
        // No-op
    }
    
    /** @exclude **/
    public void setDefaultChannels(List<String> ids)
    {
        // No-op
    }

    /** @exclude **/
    public boolean removeDefaultChannel(String id)
    {
        return false;
    }

    /** @exclude **/
    public void addDestination(Destination destination)
    {   
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public Destination createDestination(String destId)
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public Destination removeDestination(String id)
    {
        throw new UnsupportedOperationException();
    }
    
    /** @exclude **/
    public String getDefaultAdapter()
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public void setDefaultAdapter(String id)
    {        
        throw new UnsupportedOperationException();
    }
    
    /** @exclude **/
    public List<String> getDefaultChannels()
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public Destination getDestination(Message message)
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public Destination getDestination(String id)
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public Map<String, Destination> getDestinations()
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public Map<String, String> getRegisteredAdapters()
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public boolean isStarted()
    {
        return false;
    }
    
    /** @exclude **/
    public boolean isSupportedMessage(Message message)
    {
        return false;
    }

    /** @exclude **/
    public boolean isSupportedMessageType(String messageClassName)
    {
        return false;
    }

    /** @exclude **/
    public String registerAdapter(String id, String className)
    {
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public String unregisterAdapter(String id)
    {
        throw new UnsupportedOperationException();
    }
    
    /** @exclude **/
    public Object serviceCommand(CommandMessage message)
    {
        throw new UnsupportedOperationException();
    }
    
    /** @exclude **/
    public Object serviceMessage(Message message)
    {
        throw new UnsupportedOperationException();
    }
    
    /** @exclude **/
    public List getMessageTypes()
    {        
        throw new UnsupportedOperationException();
    }

    /** @exclude **/
    public void addMessageType(String messageType)
    {
        throw new UnsupportedOperationException();   
    }

    /** @exclude **/
    public void setMessageTypes(List messageTypes)
    {        
        throw new UnsupportedOperationException();
    }   
        
    /** @exclude **/
    public boolean removeMessageType(String messageType)
    {
        throw new UnsupportedOperationException();
    }       
}
