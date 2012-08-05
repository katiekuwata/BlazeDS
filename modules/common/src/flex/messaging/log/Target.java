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

import java.util.List;

import flex.messaging.config.ConfigMap;

/**
 * All logger target implementations within the logging framework must
 * implement this interface. <code>Target</code> implementations receive log events
 * and output information from these events to the appropriate output
 * destination which may be a console, log file or some other custom
 * destination.
 */
public interface Target
{
    /**
     * Initializes the target with id and properties.
     *
     * @param id id for the target.
     * @param properties ConfigMap of properties for the target.
     */
    void initialize(String id, ConfigMap properties);

    /**
     * Returns the category filters defined for the <code>Target</code>.
     *
     * @return The category filters defined for the <code>Target</code>.
     */
    List getFilters();

    /**
     * Sets the category filters that the <code>Target</code> will process
     * log events for.
     *
     * @param value The category filters that the <code>Target</code> will process
     */
    void setFilters(List value);

    /**
     * Adds the category filteer that the <code>Target</code> will process
     * log events for.
     *
     * @param value The new category filter to add to the <code>Target</code>'s list of filters.
     */
    void addFilter(String value);

    /**
     * Removes a category filter from the list of filters the <code>Target</code> will
     * process log events for.
     *
     * @param value The category filter to remove from the <code>Target</code>'s list of filters.
     */
    void removeFilter(String value);

    /**
     * Returns the log level that the <code>Target</code> will process log
     * events for. Log events at this level, or at a higher priority level
     * will be processed.
     *
     * @return The log level that the <code>Target</code> will process log events for.
     */
    short getLevel();

    /**
     * Sets the log level that the <code>Target</code> will process log events
     * for. Log events at this level, or at a higher priority level will be
     * processed.
     *
     * @param value The log level that the <code>Target</code> will process log events for.
     */
    void setLevel(short value);

    /**
     * Adds a <code>Logger</code> whose category matches the filters list for
     * the <code>Target</code>. The <code>Logger</code> will dispatch log events
     * to this <code>Target</code> to be output.
     *
     * @param logger The <code>Logger</code> to add.
     */
    void addLogger(Logger logger);

    /**
     * Removes a <code>Logger</code> from the <code>Target</code>.
     *
     * @param logger The <code>Logger</code> to remove.
     */
    void removeLogger(Logger logger);

    /**
     * Logs a log event out to the <code>Target</code>s output destination,
     * which may be the console or a log file.
     *
     * @param event The <code>LogEvent</code> containing the information to output.
     */
    void logEvent(LogEvent event);
}
