/*************************************************************************
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
package flex.messaging;

/**
 * Class representing the build version of Data Services.
 * 
 *@exclude
 */
public class VersionInfo
{
    //Cache this info as it should not change during the time class is loaded
    public static String BUILD_MESSAGE;
    public static String BUILD_NUMBER_STRING;
    public static String BUILD_TITLE;
    public static long BUILD_NUMBER;
    
    private static final String LCDS_CLASS = "flex.data.DataService";

    public static String buildMessage()
    {
        if (BUILD_MESSAGE == null)
        {
            try
            {
                //Ensure we've parsed build info
                getBuild();

                if (BUILD_NUMBER_STRING == null || BUILD_NUMBER_STRING == "")
                {
                    BUILD_MESSAGE = BUILD_TITLE;
                }
                else
                {
                    BUILD_MESSAGE = BUILD_TITLE + ": " + BUILD_NUMBER_STRING;
                }
            }
            catch (Throwable t)
            {
                BUILD_MESSAGE = BUILD_TITLE +": information unavailable";
            }
        }

        return BUILD_MESSAGE;
    }

    public static long getBuildAsLong()
    {
        if (BUILD_NUMBER == 0)
        {
            getBuild();

            if (BUILD_NUMBER_STRING != null && !BUILD_NUMBER_STRING.equals(""))
            {
                try
                {
                    BUILD_NUMBER = Long.parseLong(BUILD_NUMBER_STRING);
                }
                catch (NumberFormatException nfe)
                {
                    // ignore, just return 0
                }
            }
        }

        return BUILD_NUMBER;
    }

    public static String getBuild()
    {
        if (BUILD_NUMBER_STRING == null)
        {
            Class classToUseForManifest;  
            
            try
            {
                classToUseForManifest = Class.forName(LCDS_CLASS);
            }
            catch (ClassNotFoundException e)
            {
                classToUseForManifest = VersionInfo.class;
            }
            
            try
            {
                BUILD_NUMBER_STRING = "";
                Package pack = classToUseForManifest.getPackage();
                BUILD_NUMBER_STRING = pack.getImplementationVersion();
                BUILD_TITLE = pack.getImplementationTitle();
            }
            catch (Throwable t)
            {
                // ignore, just return empty string
            }
        }

        return BUILD_NUMBER_STRING;
    }
}
