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
import flex.messaging.config.ServiceSettings;
import flex.messaging.LocalizedException;

public class Confirm1a extends ConfigurationConfirmation
{
    private MessagingConfiguration EXPECTED_VALUE;

    public Confirm1a()
    {
        MessagingConfiguration config = new MessagingConfiguration();

        ServiceSettings service = new ServiceSettings("foo-service");
        service.setClassName("flex.messaging.services.FooService");
        config.addServiceSettings(service);

        service = new ServiceSettings("bar-service");
        service.setClassName("flex.messaging.services.BarService");
        config.addServiceSettings(service);

        EXPECTED_VALUE = config;
    }

    public MessagingConfiguration getExpectedConfiguration()
    {
        return EXPECTED_VALUE;
    }

    public LocalizedException getExpectedException()
    {
        return null;
    }
}

