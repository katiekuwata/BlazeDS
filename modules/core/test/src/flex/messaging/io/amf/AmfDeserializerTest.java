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

package flex.messaging.io.amf;

import flex.messaging.io.MessageDeserializer;
import flex.messaging.io.SerializationContext;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import macromedia.qa.metrics.MetricsManager;
import macromedia.qa.metrics.Value;
import macromedia.util.UnitTrace;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

/**
 * A simple test that creates a sample AMF request from an xml file
 * and then pipes the output back into the AMF MessageDeserializer.
 * <p/>
 * A metric for the current build is recorded as "AMF Deserialization Time"
 * in milliseconds.
 * </p>
 *
 * @author Peter Farland
 */
public class AmfDeserializerTest extends TestCase
{
    private MetricsManager metricsManager;

    public AmfDeserializerTest(String name)
    {
        super(name);
    }


    protected void setUp()
    {
        try
        {
            String project = System.getProperty("project.name");
            String build = System.getProperty("build.number");
            String props = System.getProperty("metrics.properties");

            if (project != null && build != null
                    && !build.trim().toUpperCase().equals("N/A") && props != null)
            {

                File file = new File(props);

                if (file.exists())
                {
                    metricsManager = new MetricsManager(project, build, file);

                    metricsManager.newRun();
                    metricsManager.newMetric("AMF Deserialization Time", "ms");
                }
            }
        }
        catch (Throwable t)
        {
            if (UnitTrace.errors)
                t.printStackTrace();
        }
    }


    public static Test suite()
    {
        return new TestSuite(AmfDeserializerTest.class);
    }

    /**
     * Test creating an AMF message and then sending it through an AMFEndpoint
     */
    public void testDeserializeMessage()
    {
        try
        {
            /**
             * FIND AMF SAMPLE DATA FILE
             */
            String sample = System.getProperty("AMF_SAMPLE_FILE");
            if (sample == null || sample.length() < 1)
            {
                sample = "amf_request.xml";
            }

            URL resource = ClassLoader.getSystemResource(sample);
            File testData = new File(resource.getFile());
            String testDataLocation = testData.getCanonicalPath();

            /**
             * GENERATE SAMPLE AMF REQUEST FROM DATA FILE
             */
            PipedOutputStream pout = new PipedOutputStream();
            DataOutputStream dout = new DataOutputStream(pout);
            PipedInputStream pin = new PipedInputStream(pout);
            DataInputStream din = new DataInputStream(pin);

            AmfTrace trace = new AmfTrace();
            trace.startResponse("Serializing AMF/HTTP response");

            MessageGenerator gen = new MessageGenerator();
            gen.setDebugTrace(trace);
            gen.setOutputStream(dout);
            gen.parse(testDataLocation);
            trace.endMessage();
            trace.newLine();

            /**
             * CREATE A DESERIALIZER FOR SAMPLE AMF REQUEST
             */
            ActionContext context = new ActionContext();
            ActionMessage message = new ActionMessage();
            context.setRequestMessage(message);
            trace.startRequest("Deserializing AMF/HTTP request");
            SerializationContext dsContext = SerializationContext.getSerializationContext();
            MessageDeserializer deserializer = new AmfMessageDeserializer();
            deserializer.initialize(dsContext, din, trace);

            /**
             * RECORD TIME TO DESERIALIZE THE SAMPLE MESSAGE
             * AS OUR TEST METRIC...
             */
            long start = System.currentTimeMillis();

            deserializer.readMessage(message, context);

            long finish = System.currentTimeMillis();
            trace.endMessage();

            try
            {
                if (metricsManager != null)
                {
                    long duration = finish - start;
                    Value v2 = metricsManager.createValue(duration);
                    metricsManager.saveValue(v2);
                    trace.newLine();

                    if (UnitTrace.debug)
                        System.out.print("AMF Deserialization Time: " + duration + "ms");
                }
            }
            catch (Throwable t)
            {
                if (UnitTrace.errors)
                    t.printStackTrace();
            }

            /**
             * PRINT TRACE OUTPUT
             */
            if (UnitTrace.debug)
                System.out.print(trace.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
