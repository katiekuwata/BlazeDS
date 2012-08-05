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

import flex.messaging.validators.DeserializationValidator;

/**
 * Settings class for validators.
 */
public class ValidatorSettings extends PropertiesSettings
{
    private String className;
    private String type = DeserializationValidator.class.getName();

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
     * @param className The class name.
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * Returns the type of the validator.
     *
     * @return The type of the validator.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the type of the validator.
     *
     * @param type The type of the validator.
     */
    public void setType(String type)
    {
        this.type = type;
    }
}