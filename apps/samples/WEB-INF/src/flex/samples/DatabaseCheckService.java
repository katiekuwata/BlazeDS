package flex.samples;

import java.sql.Connection;
import java.sql.SQLException;

import flex.messaging.config.ConfigMap;
import flex.messaging.services.AbstractBootstrapService;

public class DatabaseCheckService extends AbstractBootstrapService
{
    public void initialize(String id, ConfigMap properties)
    {
    	Connection c = null;
    	try 
    	{	
    		// Check that the database is running...
    		c = ConnectionHelper.getConnection();
    		// ... if yes return
    		return;
    	}
    	catch (SQLException e)
    	{
    		System.out.println("******************************************************************************");
    		System.out.println("*                                                                            *");
    		System.out.println("*  Unable to connect to the samples database.                                *");
    		System.out.println("*  You must start the samples database before you can run the samples.       *");
    		System.out.println("*  To start the samples database:                                            *");
    		System.out.println("*    1. Open a command prompt and go to the {install-dir}/sampledb dir       *");
    		System.out.println("*    2. Run startdb.bat (Windows) or startdb.sh (Unix-based systems)         *");
    		System.out.println("*                                                                            *");
    		System.out.println("******************************************************************************");
    	} 
    	finally
    	{
    		ConnectionHelper.close(c);
    	}
    	
    }

    public void start()
    {
    }


    public void stop()
    {
    }

}
