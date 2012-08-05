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
import flex.messaging.config.AdapterSettings;
import flex.messaging.config.ChannelSettings;
import flex.messaging.config.SecuritySettings;
import flex.messaging.config.SecurityConstraint;
import flex.messaging.config.DestinationSettings;
import flex.messaging.LocalizedException;

public class Confirm1d extends ConfigurationConfirmation
{
    private MessagingConfiguration EXPECTED_VALUE;

    public Confirm1d()
    {
        MessagingConfiguration config = new MessagingConfiguration();

        // Security
        SecuritySettings security = config.getSecuritySettings();
        SecurityConstraint fooConstraint = new SecurityConstraint("foo-constraint");
        fooConstraint.setMethod(SecurityConstraint.CUSTOM_AUTH_METHOD);
        fooConstraint.addRole("foo-managers");
        security.addConstraint(fooConstraint);

        SecurityConstraint barConstraint = new SecurityConstraint("bar-constraint");
        barConstraint.setMethod(SecurityConstraint.CUSTOM_AUTH_METHOD);
        barConstraint.addRole("bar-managers");
        security.addConstraint(barConstraint);


        // Channels
        ChannelSettings fooChannel = new ChannelSettings("foo-channel");
        fooChannel.setUri("/foo");
        fooChannel.setEndpointType("flex.messaging.endpoints.FooEndpoint");
        config.addChannelSettings("foo-channel", fooChannel);

        ChannelSettings barChannel = new ChannelSettings("bar-channel");
        barChannel.setUri("/bar");
        barChannel.setEndpointType("flex.messaging.endpoints.BarEndpoint");
        config.addChannelSettings("bar-channel", barChannel);


        // Services
        ServiceSettings fooService = new ServiceSettings("foo-service");
        fooService.setClassName("flex.messaging.services.FooService");
        config.addServiceSettings(fooService);

        // Adapters
        AdapterSettings fooAdapter = new AdapterSettings("foo");
        fooAdapter.setClassName("flex.messaging.services.foo.FooAdapter");
        fooService.addAdapterSettings(fooAdapter);

        AdapterSettings barAdapter = new AdapterSettings("bar");
        barAdapter.setClassName("flex.messaging.services.bar.BarAdapter");
        barAdapter.setDefault(true);
        fooService.addAdapterSettings(barAdapter);

        // Default Channels
        fooService.addDefaultChannel(fooChannel);


        // Destination - foo-dest
        DestinationSettings fooDest = new DestinationSettings("foo-dest");
        fooDest.addChannelSettings(fooChannel);
        fooDest.setAdapterSettings(fooAdapter);
        fooDest.setConstraint(fooConstraint);
        fooDest.addProperty("fooString", "fooValue");
        fooService.addDestinationSettings(fooDest);

        // Destination - bar-dest
        DestinationSettings barDest = new DestinationSettings("bar-dest");
        barDest.addChannelSettings(barChannel);
        barDest.addChannelSettings(fooChannel);
        barDest.setAdapterSettings(barAdapter);
        barDest.setConstraint(barConstraint);
        barDest.addProperty("barString", "barValue");
        fooService.addDestinationSettings(barDest);

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

