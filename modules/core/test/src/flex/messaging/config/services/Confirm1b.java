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
import flex.messaging.LocalizedException;

public class Confirm1b extends ConfigurationConfirmation
{
    private MessagingConfiguration EXPECTED_VALUE;

    public Confirm1b()
    {
        MessagingConfiguration config = new MessagingConfiguration();

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

        AdapterSettings fooAdapter = new AdapterSettings("foo");
        fooAdapter.setClassName("flex.messaging.services.foo.FooAdapter");
        fooService.addAdapterSettings(fooAdapter);

        fooService.addDefaultChannel(fooChannel);


        ServiceSettings barService = new ServiceSettings("bar-service");
        barService.setClassName("flex.messaging.services.BarService");
        config.addServiceSettings(barService);

        AdapterSettings bar1Adapter = new AdapterSettings("bar1");
        bar1Adapter.setClassName("flex.messaging.services.bar.BarAdapter");
        barService.addAdapterSettings(bar1Adapter);

        AdapterSettings bar2Adapter = new AdapterSettings("bar2");
        bar2Adapter.setClassName("flex.messaging.services.bar.BarAdapter");
        barService.addAdapterSettings(bar2Adapter);

        barService.addDefaultChannel(barChannel);
        barService.addDefaultChannel(fooChannel);

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

