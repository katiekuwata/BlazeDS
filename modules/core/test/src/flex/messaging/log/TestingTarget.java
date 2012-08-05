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
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

package flex.messaging.log;

public class TestingTarget extends AbstractTarget
{
    public TestingTarget()
    {
        super();
    }

    public void addLogger(Logger logger)
    {
        super.addLogger(logger);
        addLoggerCalled = true;
    }

    public void removeLogger(Logger logger)
    {
        super.removeLogger(logger);
        removeLoggerCalled = true;
    }

    public void logEvent(LogEvent event)
    {
        lastEvent = event;
    }

    public LogEvent lastEvent;
    public boolean addLoggerCalled;
    public boolean removeLoggerCalled;
}
