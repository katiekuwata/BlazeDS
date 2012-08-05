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

package flex.messaging.io.amf.validators;

import flex.messaging.config.ConfigMap;
import flex.messaging.io.MessageDeserializer;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfMessageDeserializer;
import flex.messaging.io.amf.AmfTrace;
import flex.messaging.io.amf.MessageGenerator;
import flex.messaging.validators.DeserializationValidator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import macromedia.util.UnitTrace;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

/**
 * This test creates a sample AMF request from an xml file and then pipes the
 * output back into the AMF MessageDeserializer. The test does not really verify
 * anything but it rather lets someone to compare how long deserialization takes
 * with and without a deserialization validator in picture.
 */
public class AmfDeserializationValidatorTest extends TestCase
{
    public static Test suite()
    {
        return new TestSuite(AmfDeserializationValidatorTest.class);
    }

    public void testDeserializationValidator()
    {
        // Turns out that the first time the AMF request is read from the XML file,
        // it takes much longer, so this dummy call to deserializeRequest is meant
        // to account for that initial hit.
        deserializateRequest(null);

        long duration1 = 0, duration2 = 0;
        DeserializationValidator validator = new TestDeserializationValidator();
        int n = 100; // Number of times to run the tests, use an even number.

        // Deserialize the message first without and then with a validator.
        for (int i = 0; i < n/2; i++)
        {
            long start1 = System.currentTimeMillis();
            deserializateRequest(null);
            duration1 += (System.currentTimeMillis() - start1);

            long start2 = System.currentTimeMillis();
            deserializateRequest(validator);
            duration2 += (System.currentTimeMillis() - start2);
        }

        // Now, deserialize the message first with and then without a validator.
        for (int i = 0; i < n/2; i++)
        {
            long start2 = System.currentTimeMillis();
            deserializateRequest(validator);
            duration2 += (System.currentTimeMillis() - start2);

            long start1 = System.currentTimeMillis();
            deserializateRequest(null);
            duration1 += (System.currentTimeMillis() - start1);
        }

        if (UnitTrace.debug) // Print trace output.
        {
            System.out.println("AMF Deserialization Time w/o validator: " + duration1 + "ms");
            System.out.println("AMF Deserialization Time w validator: " + duration2 + "ms");
        }
    }

    private void deserializateRequest(DeserializationValidator validator)
    {
        try
        {
            // Find sample AMF data, or read the default file.
            String sample = System.getProperty("AMF_SAMPLE_FILE");
            if (sample == null || sample.length() < 1)
                sample = "amf_request.xml";

            URL resource = ClassLoader.getSystemResource(sample);
            File testData = new File(resource.getFile());
            String testDataLocation = testData.getCanonicalPath();

            // Generate sample AMF request from the data file.
            PipedOutputStream pout = new PipedOutputStream();
            DataOutputStream dout = new DataOutputStream(pout);

            DataInputStream din = new DataInputStream(new PipedInputStream(pout));

            AmfTrace trace = new AmfTrace();
            trace.startResponse("Serializing AMF/HTTP response");

            MessageGenerator gen = new MessageGenerator();
            gen.setDebugTrace(trace);
            gen.setOutputStream(dout);
            gen.parse(testDataLocation);
            trace.endMessage();
            trace.newLine();

            // Create a deserializer for sample AMF request.
            ActionContext context = new ActionContext();
            ActionMessage message = new ActionMessage();
            context.setRequestMessage(message);

            SerializationContext dsContext = SerializationContext.getSerializationContext();
            if (validator != null)
                dsContext.setDeserializationValidator(validator);
            MessageDeserializer deserializer = new AmfMessageDeserializer();
            deserializer.initialize(dsContext, din, trace);
            deserializer.readMessage(message, context);
            trace.endMessage();
            trace.newLine();

            //if (UnitTrace.debug) // Print trace output.
            //    System.out.print(trace.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    // A simple deserialization validator that simply returns true for all creation
    // and assignments validations.
    class TestDeserializationValidator implements DeserializationValidator
    {
        public boolean validateAssignment(Object instance, int index, Object value)
        {
            //System.out.println("validateAssign: [" + (instance == null? "null" : instance.getClass().getName()) + "," + index + "," + value + "]");
            return true;
        }

        public boolean validateAssignment(Object instance, String propertyName, Object value)
        {
            //System.out.println("validateAssign: [" + (instance == null? "null" : instance.getClass().getName()) + "," + propertyName + "," + value + "]");
            return true;
        }

        public boolean validateCreation(Class<?> c)
        {
            //System.out.println("validateCreate: " + (c == null? "null" : c.getName()));
            return true;
        }

        public void initialize(String id, ConfigMap configMap)
        {
            // No-op
        }
    }
}
