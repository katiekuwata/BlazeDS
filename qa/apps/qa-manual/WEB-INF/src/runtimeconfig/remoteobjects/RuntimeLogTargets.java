/************************************************************************
*
* ADOBE CONFIDENTIAL
* __________________
*
*  [2002] - [2007] Adobe Systems Incorporated
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
package runtimeconfig.remoteobjects;

import flex.messaging.log.ConsoleTarget;
import flex.messaging.log.Log;
import flex.messaging.log.LogEvent;

/*
 * This class allows logging levels to be changed dynamically from Error to Debug (and vice-versa).
 */
public class RuntimeLogTargets
{    

	public String createLogTarget(String target) 
	{
		short currentLevel = ((ConsoleTarget)Log.getTargets().get(0)).getLevel();
		String retVal = "Log level did not change. Default target level: " + LogEvent.getLevelString(currentLevel);
		
		if (target.equals("Debug")) 
		{
			currentLevel = createDebugLogTarget();
			retVal = LogEvent.getLevelString(currentLevel);
		}
		else if (target.equals("Error"))
		{
			currentLevel = createErrorLogTarget();
			retVal = LogEvent.getLevelString(currentLevel);			
		}
		
		return retVal; 
	}
	
	/*
	 * The purpose of this method is to replace the default "Error" logging target
	 * with Debug log level and specific filters.  
	 * Verification of the log must be manually done against the console output.
	 *
	 	<target class="flex.messaging.log.ConsoleTarget" level="Debug">
		    <properties>
		        <prefix>[LCDS] </prefix>
		        <includeDate>false</includeDate>
		        <includeTime>true</includeTime>
		        <includeLevel>true</includeLevel>
		        <includeCategory>true</includeCategory>
		    </properties>
		    <filters>
		        <pattern>Service.Data.*</pattern>
		        <pattern>Message.*</pattern>
		    </filters>
		</target>
     */
    private short createDebugLogTarget()
    {
    	//Remove any other targets
    	Log.reset();
    	
        //Up the logging level to debug, and create a logging target dynamically
    	ConsoleTarget myTarget = new ConsoleTarget();
        myTarget.setLevel(LogEvent.DEBUG);
        myTarget.setPrefix("[LCDS] ");
        //myTarget.setIncludeDate(true);
        myTarget.setIncludeTime(true);
        myTarget.setIncludeLevel(true);
        myTarget.setIncludeCategory(true);
        //myTarget.addFilter("Startup.*");
        myTarget.addFilter("Service.Data.*");
        myTarget.addFilter("Message.*");
        myTarget.addFilter("Endpoint.*");
        
        //Add the new Debug target
        Log.addTarget(myTarget);
        
        return ((ConsoleTarget)Log.getTargets().get(0)).getLevel();
    }

	/*
	 * Use this method to return to the Error log level in qa-manual app.
	 *
	 	<target class="flex.messaging.log.ConsoleTarget" level="Error">
		    <properties>
		        <prefix>[Flex] </prefix>
		        <includeDate>false</includeDate>
		        <includeTime>true</includeTime>
		        <includeLevel>true</includeLevel>
		        <includeCategory>true</includeCategory>
		    </properties>
		    <filters>
		        <pattern>Service.*</pattern>
		        <pattern>Message.*</pattern>
		        <pattern>DataService.*</pattern>
		        <!--<pattern>Endpoint.*</pattern>-->
		    </filters>
		</target>
     */
    private short createErrorLogTarget()
    {
    	//Remove any other targets
    	Log.reset();
    	
    	//Up the logging level to debug, and create a logging target dynamically
    	ConsoleTarget myTarget = new ConsoleTarget();
        myTarget.setLevel(LogEvent.ERROR);
        myTarget.setPrefix("[Flex] ");
        myTarget.setIncludeDate(false);
        myTarget.setIncludeTime(true);
        myTarget.setIncludeLevel(true);
        myTarget.setIncludeCategory(true);
        myTarget.addFilter("Service.*");
        myTarget.addFilter("Message.*");
        myTarget.addFilter("DataService.*");        
        Log.addTarget(myTarget);
        
        return ((ConsoleTarget)Log.getTargets().get(0)).getLevel();
    }
}


