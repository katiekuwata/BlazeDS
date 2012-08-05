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

package flex.messaging.config;

import flex.messaging.config.ThrottleSettings.Policy;
import flex.messaging.services.messaging.ThrottleManager;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ThrottleSettingsTest extends TestCase
{
    private static final String UNEXPECTED_EXCEPTION_STRING = "Unexpected exception: ";

    public ThrottleSettingsTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new ThrottleSettingsTest("testSetIncomingClientFrequencyHigherThanDestinationFrequency"));
        suite.addTest(new ThrottleSettingsTest("testSetIncomingDestinationFrequencyLowerThanClientFrequency"));
        suite.addTest(new ThrottleSettingsTest("testSetOutgoingClientFrequencyHigherThanDestinationFrequency"));
        suite.addTest(new ThrottleSettingsTest("testSetOutgoingDestinationFrequencyLowerThanClientFrequency"));
        suite.addTest(new ThrottleSettingsTest("testSetIncomingClientFrequencyWithZeroDestinationFrequency"));
        suite.addTest(new ThrottleSettingsTest("testSetIncomingDestinationFrequencyWithZeroClientFrequency"));
        suite.addTest(new ThrottleSettingsTest("testUnknownPolicy"));
        suite.addTest(new ThrottleSettingsTest("testOutgoingPolicyError"));
        return suite;
    }

    public void testSetIncomingClientFrequencyHigherThanDestinationFrequency()
    {
        ThrottleSettings ts = new ThrottleSettings();
        ts.setIncomingDestinationFrequency(10);
        try
        {
            ts.setIncomingClientFrequency(15);
            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            // Success.
        }
        catch (Exception e)
        {
            fail(UNEXPECTED_EXCEPTION_STRING + e);
        }
    }

    public void testSetIncomingDestinationFrequencyLowerThanClientFrequency()
    {
        ThrottleSettings ts = new ThrottleSettings();
        ts.setIncomingClientFrequency(15);
        try
        {
            ts.setIncomingDestinationFrequency(10);
            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            // Success.
        }
        catch (Exception e)
        {
            fail(UNEXPECTED_EXCEPTION_STRING + e);
        }
    }

    public void testSetOutgoingClientFrequencyHigherThanDestinationFrequency()
    {
        ThrottleSettings ts = new ThrottleSettings();
        ts.setOutgoingDestinationFrequency(10);
        try
        {
            ts.setOutgoingClientFrequency(15);
            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            // Success.
        }
        catch (Exception e)
        {
            fail(UNEXPECTED_EXCEPTION_STRING + e);
        }
    }

    public void testSetOutgoingDestinationFrequencyLowerThanClientFrequency()
    {
        ThrottleSettings ts = new ThrottleSettings();
        ts.setOutgoingClientFrequency(15);
        try
        {
            ts.setOutgoingDestinationFrequency(10);
            fail("ConfigurationException expected");
        }
        catch (ConfigurationException ce)
        {
            // Success.
        }
        catch (Exception e)
        {
            fail(UNEXPECTED_EXCEPTION_STRING + e);
        }
    }

    public void testSetIncomingClientFrequencyWithZeroDestinationFrequency()
    {
        ThrottleSettings ts = new ThrottleSettings();
        ts.setIncomingDestinationFrequency(0);
        ts.setIncomingClientFrequency(10);
        Assert.assertEquals(10, ts.getIncomingClientFrequency());
    }

    public void testSetIncomingDestinationFrequencyWithZeroClientFrequency()
    {
        ThrottleSettings ts = new ThrottleSettings();
        ts.setIncomingClientFrequency(0);
        ts.setIncomingDestinationFrequency(10);
        Assert.assertEquals(10, ts.getIncomingDestinationFrequency());
    }

    /**
     * Test that an UNKNOWN policy throws ConfigurationException.
     */
    public void testUnknownPolicy()
    {
        try
        {
            ThrottleSettings.parsePolicy("UNKNOWN");
        }
        catch (ConfigurationException ce)
        {
            // Success.
        }
        catch (Exception e)
        {
            fail(UNEXPECTED_EXCEPTION_STRING + e);
        }
    }

    /**
     * Test that outgoing policy of ERROR throws ConfigurationException.
     */
    public void testOutgoingPolicyError()
    {
        ThrottleManager manager = new ThrottleManager();
        ThrottleSettings ts = new ThrottleSettings();
        ts.setOutboundPolicy(Policy.ERROR);
        try
        {
            manager.setThrottleSettings(ts);
        }
        catch (ConfigurationException ce)
        {
            // Success.
        }
        catch (Exception e)
        {
            fail(UNEXPECTED_EXCEPTION_STRING + e);
        }
    }
}
