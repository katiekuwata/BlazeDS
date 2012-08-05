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
package flex.messaging.log;

import flex.messaging.config.ConfigMap;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @exclude
 */
public class LineFormattedTarget extends AbstractTarget
{
    /**
     * Indicates if the date should be added to the trace.
     */
    protected boolean includeDate;

    /**
     * Indicates if the time should be added to the trace.
     */
    protected boolean includeTime;

    /**
     * Indicates if the level for the event should added to the trace.
     */
    protected boolean includeLevel;

    /**
     * Indicates if the category for this target should added to the trace.
     */
    protected boolean includeCategory;

    /**
     * A prefix to prepend onto each logged message.
     */
    protected String prefix = null;

    /**
     * The formatter to write the date as part of the logging statement.
     * Defaults to MM/dd/yyyy format.
     */
    protected DateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * The formatter to write the time as part of the logging statement.
     * Defaults to HH:mm:ss.SSS format.
     */
    protected DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Default constructor.
     */
    public LineFormattedTarget()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    //--------------------------------------------------------------------------

    /**
     * Initializes the target with id and properties. Subclasses can overwrite.
     *
     * @param id id for the target which is ignored.
     * @param properties ConfigMap of properties for the target.
     */
    public void initialize(String id, ConfigMap properties)
    {
        super.initialize(id, properties);

        includeTime = properties.getPropertyAsBoolean("includeTime", false);
        includeDate = properties.getPropertyAsBoolean("includeDate", false);
        includeCategory = properties.getPropertyAsBoolean("includeCategory", false);
        includeLevel = properties.getPropertyAsBoolean("includeLevel", false);
        prefix = properties.getPropertyAsString("prefix", null);
    }

    //--------------------------------------------------------------------------
    //
    // Public Getters and Setters for AbstractService properties
    //
    //--------------------------------------------------------------------------

    /**
     * Returns includeCategory property.
     *
     * @return <code>true</code> if category is included; <code>false</code> otherwise.
     */
    public boolean isIncludeCategory()
    {
        return includeCategory;
    }

    /**
     * Sets includeCategory property.
     *
     * @param includeCategory
     */
    public void setIncludeCategory(boolean includeCategory)
    {
        this.includeCategory = includeCategory;
    }

    /**
     * Returns includeDate property.
     *
     * @return <code>true</code> if date is included; <code>false</code> otherwise.
     */
    public boolean isIncludeDate()
    {
        return includeDate;
    }

    /**
     * Sets includeDate property.
     *
     * @param includeDate
     */
    public void setIncludeDate(boolean includeDate)
    {
        this.includeDate = includeDate;
    }

    /**
     * Returns includeLevel property.
     *
     * @return <code>true</code> if level is included; <code>false</code> otherwise.
     */
    public boolean isIncludeLevel()
    {
        return includeLevel;
    }

    /**
     * Sets includeLevel property.
     *
     * @param includeLevel
     */
    public void setIncludeLevel(boolean includeLevel)
    {
        this.includeLevel = includeLevel;
    }

    /**
     * Returns includeTime property.
     *
     * @return <code>true</code> if time is included; <code>false</code> otherwise.
     */
    public boolean isIncludeTime()
    {
        return includeTime;
    }

    /**
     * Sets includeTime property.
     *
     * @param includeTime
     */
    public void setIncludeTime(boolean includeTime)
    {
        this.includeTime = includeTime;
    }

    /**
     * Returns prefix property.
     *
     * @return The prefix for log messages.
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Sets prefix property.
     *
     * @param prefix
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * This method handles a <code>LogEvent</code> from an associated logger.
     * A target uses this method to translate the event into the appropriate
     * format for transmission, storage, or display.
     * This method will be called only if the event's level is in range of the
     * target's level.
     */
    public void logEvent(LogEvent event)
    {
        String pre = "";
        if (prefix != null)
        {
            pre = prefix; // Comes with its own space?
        }

        String date = "";
        if (includeDate || includeTime)
        {
            StringBuffer buffer = new StringBuffer();
            Date d = new Date();
            if (includeDate)
            {
                buffer.append(dateFormater.format(d));
                buffer.append(" ");
            }
            if (includeTime)
            {
                buffer.append(timeFormatter.format(d));
                buffer.append(" ");
            }
            date = buffer.toString();
        }

        String cat = includeCategory ?
                           ("[" + event.logger.getCategory() + "] ") : "";
        String level = "";
        if (includeLevel)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("[");
            buffer.append(LogEvent.getLevelString(event.level));
            buffer.append("]");
            buffer.append(" ");
            level = buffer.toString();
        }
        StringBuffer result = new StringBuffer(pre);
        result.append(date).append(level).append(cat).append(event.message);

        if (event.throwable != null)
            result.append(StringUtils.NEWLINE).append(ExceptionUtil.toString(event.throwable));

        internalLog(result.toString());
    }

    //--------------------------------------------------------------------------
    //
    // Protected/private methods.
    //
    //--------------------------------------------------------------------------

    /**
     * Descendants of this class should override this method to direct the
     * specified message to the desired output.
     *
     * @param  message String containing preprocessed log message which may
     * include time, date, category, etc. based on property settings,
     * such as <code>includeDate</code>, <code>includeCategory</code>,
     * etc.
     */
    protected void internalLog(String message)
    {
        // override this method to perform the redirection to the desired output
    }
}
