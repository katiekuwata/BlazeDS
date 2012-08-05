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

package flex.messaging.config.services;

import flex.messaging.config.ConfigurationConfirmation;
import flex.messaging.config.MessagingConfiguration;
import flex.messaging.LocalizedException;
import flex.messaging.MessageException;

public class Confirm1g extends ConfigurationConfirmation
{
    public Confirm1g()
    {
    }

    public boolean isNegativeTest()
    {
        return true;
    }

    public MessagingConfiguration getExpectedConfiguration()
    {
        return null;
    }

    public LocalizedException getExpectedException()
    {
        return new MessageException("Invalid service id 'foo-service,'.");
    }
}

