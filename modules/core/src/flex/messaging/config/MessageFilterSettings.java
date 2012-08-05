/*************************************************************************
  *
  * ADOBE CONFIDENTIAL
  * __________________
  *
  *  Copyright 2008 Adobe Systems Incorporated
  *  All Rights Reserved.
  *
  * NOTICE:  All information contained herein is, and remains
  * the property of Adobe Systems Incorporated and its suppliers,
  * if any.  The intellectual and technical concepts contained
  * herein are proprietary to Adobe Systems Incorporated and its
  * suppliers and may be covered by U.S. and Foreign Patents,
  * patents in process, and are protected by trade secret or copyright law.
  * Dissemination of this information or reproduction of this material
  * is strictly forbidden unless prior written permission is obtained
  * from Adobe Systems Incorporated.
  **************************************************************************/
package flex.messaging.config;

/**
 * Settings class for message filters.
 * 
 * @exclude
 */
public class MessageFilterSettings extends PropertiesSettings
{
    /**
     * Filters belong to one of two types; those that filter messages
     * asynchronously and those that filter messages synchronously.
     */
    public enum FilterType { ASYNC, SYNC };
    
    private String id;

    /**
     * Returns the id.
     *
     * @return The id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param value The id.
     */
    public void setId(String value)
    {
        id = value;
    }    
    
    private String className;

    /**
     * Returns the class name.
     *
     * @return The class name.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Sets the class name.
     *
     * @param value The class name.
     */
    public void setClassName(String value)
    {
        className = value;
    }
    
    private FilterType filterType;
    
    /**
     * Returns the filter type.
     * @see FilterType
     * 
     * @return The filter type.
     */
    public FilterType getFilterType()
    {
        return filterType;
    }
    
    /**
     * Sets the filter type.
     * @see FilterType
     * 
     * @param value The filter type.
     */
    public void setFilterType(FilterType value)
    {
        filterType = value;
    }
}
