package flex.samples.runtimeconfig;

import flex.messaging.config.ConfigMap;
import flex.messaging.services.AbstractBootstrapService;
import flex.messaging.services.RemotingService;
import flex.messaging.services.remoting.RemotingDestination;

public class EmployeeRuntimeRemotingDestination extends AbstractBootstrapService
{
	private RemotingService remotingService;

    /**
     * This method is called by FDS when FDS has been initialized but not started. 
     */    
    public void initialize(String id, ConfigMap properties)
    {
        remotingService = (RemotingService) getMessageBroker().getService("remoting-service");
        RemotingDestination destination = (RemotingDestination) remotingService.createDestination(id);
        destination.setSource("flex.samples.crm.employee.EmployeeDAO");
    }
    
    /**
     * This method is called by FDS as FDS starts up (after initialization). 
     */
    public void start()
    {
        // No-op
    }

    /**
     * This method is called by FDS as FDS shuts down.
     */
    public void stop()
    {
        // No-op
    }
    
}
