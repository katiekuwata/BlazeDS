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
package flex.messaging.config;

/**
 * @exclude
 */
public class FlexClientSettings extends PropertiesSettings
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs a FlexClientSettings instance.
     */
    public FlexClientSettings()
    {
        // Empty for now.
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    private long timeoutMinutes = -1;

    /**
     * Returns the number of minutes before an idle FlexClient is timed out.
     *
     * @return The number of minutes before an idle FlexClient is timed out.
     */
    public long getTimeoutMinutes()
    {
        return timeoutMinutes;
    }

    /**
     * Sets the number of minutes before an idle FlexClient is timed out.
     *
     * @param value The number of minutes before an idle FlexClient is timed out.
     */
    public void setTimeoutMinutes(long value)
    {
        timeoutMinutes = value;
    }

    private String flexClientOutboundQueueProcessorClassName;

    /**
     * Returns the name of the default <code>FlexClientOutboundQueueProcessorClass</code>.
     *
     * @return The the name of the  default <code>FlexClientOutboundQueueProcessorClass</code>.
     */
    public String getFlexClientOutboundQueueProcessorClassName()
    {
        return flexClientOutboundQueueProcessorClassName;
    }

    /**
     * Sets the name of the default <code>FlexClientOutboundQueueProcessor</code>.
     *
     * @param flexClientOutboundQueueProcessorClassName The name of the default <code>FlexClientOutboundQueueProcessor</code>.
     */
    public void setFlexClientOutboundQueueProcessorClassName(String flexClientOutboundQueueProcessorClassName)
    {
        this.flexClientOutboundQueueProcessorClassName = flexClientOutboundQueueProcessorClassName;
    }

    private ConfigMap flexClientOutboundQueueProcessorProperties;

    /**
     * Returns the properties for the default <code>FlexClientOutboundQueueProcessor</code>.
     *
     * @return The properties for the default <code>FlexClientOutboundQueueProcessor</code>.
     */
    public ConfigMap getFlexClientOutboundQueueProcessorProperties()
    {
        return flexClientOutboundQueueProcessorProperties;
    }

    /**
     * Sets the properties for the default <code>FlexClientOutboundQueueProcessor</code>.
     *
     * @param flexClientOutboundQueueProcessorProperties
     */
    public void setFlexClientOutboundQueueProcessorProperties(ConfigMap flexClientOutboundQueueProcessorProperties)
    {
        this.flexClientOutboundQueueProcessorProperties = flexClientOutboundQueueProcessorProperties;
    }
    
    private int reliableReconnectDurationMillis;
    
    public int getReliableReconnectDurationMillis()
    {
        return reliableReconnectDurationMillis;    
    }
    
    public void setReliableReconnectDurationMillis(int value)
    {
        reliableReconnectDurationMillis = value;
    }
    
    private int heartbeatIntervalMillis;
    
    public int getHeartbeatIntervalMillis()
    {
        return heartbeatIntervalMillis;
    }
    
    public void setHeartbeatIntervalMillis(int value)
    {
        heartbeatIntervalMillis = value;
    }
}
