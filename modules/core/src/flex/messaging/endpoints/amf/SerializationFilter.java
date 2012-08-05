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
package flex.messaging.endpoints.amf;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import flex.messaging.FlexContext;
import flex.messaging.MessageException;
import flex.messaging.io.MessageDeserializer;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.MessageSerializer;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.SerializationException;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfTrace;
import flex.messaging.io.amf.MessageBody;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.log.Logger;
import flex.messaging.messages.ErrorMessage;
import flex.messaging.messages.Message;
import flex.messaging.messages.MessagePerformanceInfo;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.StringUtils;


/**
 * Filter for serializing and deserializing action messages.
 *
 * @author PS Neville
 * @author Sim Simeonov
 * @author Peter Farland
 */
public class SerializationFilter extends AMFFilter
{
    //--------------------------------------------------------------------------
    //
    // Private Static Constants
    //
    //--------------------------------------------------------------------------

    // Error codes.
    private static final int UNHANDLED_ERROR = 10306;
    private static final int REQUEST_ERROR = 10307;
    private static final int RESPONSE_ERROR = 10308;

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs a <tt>SerializationFilter</tt>.
     * 
     * @param logCategory Log category to use in logging, if null defaults to Endpoint.General. 
     */
    public SerializationFilter(String logCategory)
    {
        if (logCategory == null)
            logCategory = LogCategories.ENDPOINT_GENERAL;
        logger = Log.getLogger(logCategory);
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Used to log serialization/deserialization messages.
     */
    private Logger logger;

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    @Override
    public void invoke(final ActionContext context) throws IOException
    {
        boolean success = false;

        // Additional AMF packet tracing is enabled only at the debug logging level
        // and only if there's a target listening for it.
        AmfTrace debugTrace = Log.isDebug() && logger.hasTarget()? new AmfTrace() : null;

        // Create an empty ActionMessage object to hold our response
        context.setResponseMessage(new ActionMessage());
        SerializationContext sc = SerializationContext.getSerializationContext();

        try
        {
            // Deserialize the input stream into an "ActionMessage" object.
            MessageDeserializer deserializer = sc.newMessageDeserializer();

            // Set up the deserialization context
            InputStream in = FlexContext.getHttpRequest().getInputStream();
            deserializer.initialize(sc, in, debugTrace);

            // record the length of the input stream for performance metrics
            int reqLen = FlexContext.getHttpRequest().getContentLength();
            context.setDeserializedBytes(reqLen);

            // set up the incoming MPI info if it is enabled
            if(context.isMPIenabled())
            {
                MessagePerformanceInfo mpi = new MessagePerformanceInfo();
                mpi.recordMessageSizes = context.isRecordMessageSizes();
                mpi.recordMessageTimes = context.isRecordMessageTimes();
                if(context.isRecordMessageTimes())
                    mpi.receiveTime = System.currentTimeMillis();
                if(context.isRecordMessageSizes())
                    mpi.messageSize =reqLen;

                context.setMPII(mpi);
            }

            ActionMessage m = new ActionMessage();
            context.setRequestMessage(m);
            deserializer.readMessage(m, context);
            success = true;
        }
        catch (Throwable t)
        {
            handleDeserializationException(context, t, logger);
        }
        finally
        {
            // Use the same ActionMessage version for the response
            ActionMessage respMsg = context.getResponseMessage();
            respMsg.setVersion(context.getVersion());

            if (debugTrace != null)
                logger.debug(debugTrace.toString());
        }

        try
        {
            if (success)
            {
                next.invoke(context);
            }
        }
        catch (Throwable t)
        {
            unhandledError(context, t);
        }
        finally
        {
            // serialize output
            if (context.getStatus() != MessageIOConstants.STATUS_NOTAMF)
            {
                ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
                ActionMessage respMesg = context.getResponseMessage();

                // Additional AMF packet tracing is enabled only at the debug logging level
                // and only if there's a target listening for it.
                debugTrace = Log.isDebug() && logger.hasTarget()? new AmfTrace() : null;

                try
                {
                    // overhead calculation is only necessary when MPI is enabled
                    long serializationOverhead=0;
                    if(context.isRecordMessageTimes())
                    {
                        // set server send time
                        context.getMPIO().sendTime = System.currentTimeMillis();
                        if(context.isRecordMessageSizes())
                            serializationOverhead = System.currentTimeMillis();
                    }
                    MessageSerializer serializer = sc.newMessageSerializer();
                    serializer.initialize(sc, outBuffer, debugTrace);
                    serializer.writeMessage(respMesg);

                    // keep track of serializes bytes for performance metrics
                    context.setSerializedBytes(outBuffer.size());

                    // serialized message again after adding info if mpio with sizing is enabled
                    if(context.isRecordMessageSizes())
                    {
                        try
                        {
                            context.getMPIO().messageSize = outBuffer.size();

                            // reset server send time
                            if(context.isRecordMessageTimes())
                            {
                                serializationOverhead = System.currentTimeMillis() - serializationOverhead;
                                context.getMPIO().addToOverhead(serializationOverhead);
                                context.getMPIO().sendTime = System.currentTimeMillis();
                            }

                            // reserialize the message now that info has been added
                            outBuffer = new ByteArrayOutputStream();
                            respMesg = context.getResponseMessage();
                            serializer.initialize(sc, outBuffer, debugTrace);
                            serializer.writeMessage(respMesg);
                        }
                        catch(Exception e)
                        {
                            if (Log.isDebug())
                                logger.debug("MPI set up error: " + e.toString());
                        }
                    }
                    context.setResponseOutput(outBuffer);
                }
                catch (Exception e)
                {
                    handleSerializationException(sc, context, e, logger);
                }
                finally
                {
                    if (debugTrace != null)
                        logger.debug(debugTrace.toString());
                }
            }
        }
    }

    /**
     * This static method provides a common way for deserialization errors to be
     * handled. It attempts to provide the client with useful information about
     * deserialization failure.
     * 
     * @param actionContext The action context.
     * @param t The throwable that needs to be handled.
     * @param logger The logger to log messages to.
     * @throws IOException
     */
    public static void handleDeserializationException(ActionContext actionContext, Throwable t, Logger logger) throws IOException
    {
        if (t instanceof EOFException)
        {
            actionContext.setStatus(MessageIOConstants.STATUS_NOTAMF);
        }
        else if (t instanceof IOException)
        {
            if (Log.isDebug())
                logger.debug("IOException reading message - client closed socket before sending the message?");

            throw (IOException)t;
        }
        else
        {
            actionContext.setStatus(MessageIOConstants.STATUS_ERR);

            // Create a single message body to hold the error
            MessageBody responseBody = new MessageBody();
            if (actionContext.getMessageNumber() < actionContext.getRequestMessage().getBodyCount())
                responseBody.setTargetURI(actionContext.getRequestMessageBody().getResponseURI());

            // If the message couldn't be deserialized enough to know the version, set the current version here
            if (actionContext.getVersion() == 0)
                actionContext.setVersion(ActionMessage.CURRENT_VERSION);

            // append the response body to the output message
            actionContext.getResponseMessage().addBody(responseBody);

            String message;
            MessageException methodResult;
            if (t instanceof MessageException)
            {
                methodResult = (MessageException)t;
                message = methodResult.getMessage();
            }
            else
            {
                //Error deserializing client message.
                methodResult = new SerializationException();
                methodResult.setMessage(REQUEST_ERROR);
                methodResult.setRootCause(t);
                message = methodResult.getMessage();
            }
            responseBody.setData(methodResult.createErrorMessage());
            responseBody.setReplyMethod(MessageIOConstants.STATUS_METHOD);

            if (Log.isError())
                logger.error(message + StringUtils.NEWLINE + ExceptionUtil.toString(t));
        }

    }

    /**
     * This static method provides a common way for serialization errors to be
     * handled. It attempts to provide the client with useful information about 
     * the serialization failure. When there is a serialization failure, there is
     * no way to tell which response failed serialization, so it adds a new response
     * with the serialization failure for each of the corresponding requests.
     * 
     * @param serializer The serializer that generated the error.
     * @param serializationContext The serialization context.
     * @param actionContext The action context.
     * @param t The throwable that needs to be handled.
     * @param logger The logger to log error messages to.
     */
    public static void handleSerializationException(SerializationContext serializationContext, 
            ActionContext actionContext, Throwable t, Logger logger)
    {
        ActionMessage responseMessage = new ActionMessage();
        actionContext.setResponseMessage(responseMessage);

        int bodyCount = actionContext.getRequestMessage().getBodyCount();
        for (actionContext.setMessageNumber(0); actionContext.getMessageNumber() < bodyCount; actionContext.incrementMessageNumber())
        {
            MessageBody responseBody = new MessageBody();
            responseBody.setTargetURI(actionContext.getRequestMessageBody().getResponseURI());
            actionContext.getResponseMessage().addBody(responseBody);

            Object methodResult;

            if (t instanceof MessageException)
            {
                methodResult = ((MessageException)t).createErrorMessage();
            }
            else
            {
                String message = "An error occurred while serializing server response(s).";
                if (t.getMessage() != null)
                {
                    message = t.getMessage();
                    if (message == null)
                        message = t.toString();
                }

                methodResult = new MessageException(message, t).createErrorMessage();
            }

            if (actionContext.isLegacy())
            {
                if (methodResult instanceof ErrorMessage)
                {
                    ErrorMessage error = (ErrorMessage)methodResult;
                    ASObject aso = new ASObject();
                    aso.put("message", error.faultString);
                    aso.put("code", error.faultCode);
                    aso.put("details", error.faultDetail);
                    aso.put("rootCause", error.rootCause);
                    methodResult = aso;
                }
                else if (methodResult instanceof Message)
                {
                    methodResult = ((Message)methodResult).getBody();
                }
            }
            else
            {
                try
                {
                    Message inMessage = actionContext.getRequestMessageBody().getDataAsMessage();
                    if (inMessage.getClientId() != null)
                        ((ErrorMessage)methodResult).setClientId(inMessage.getClientId().toString());

                    if (inMessage.getMessageId() != null)
                    {
                        ((ErrorMessage)methodResult).setCorrelationId(inMessage.getMessageId());
                        ((ErrorMessage)methodResult).setDestination(inMessage.getDestination());
                    }
                }
                catch (MessageException ignore){}
            }

            responseBody.setData(methodResult);
            responseBody.setReplyMethod(MessageIOConstants.STATUS_METHOD);
        }

        if (Log.isError() && logger != null)
            logger.error("Exception occurred during serialization: " + ExceptionUtil.toString(t));

        // Serialize the error messages
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        AmfTrace debugTrace = Log.isDebug() && logger.hasTarget()? new AmfTrace() : null;
        MessageSerializer serializer = serializationContext.newMessageSerializer();
        serializer.initialize(serializationContext, outBuffer, debugTrace);

        try
        {
            serializer.writeMessage(actionContext.getResponseMessage());
            actionContext.setResponseOutput(outBuffer);
        }
        catch (IOException e)
        {
            //Error serializing response
            MessageException ex = new MessageException();
            ex.setMessage(RESPONSE_ERROR);
            ex.setRootCause(e);
            throw ex;
        }
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * An unhandled error happened somewhere between the SerializationFilter and the
     * ErrorFilter... we ignore all request bodies and attempt to send back a single response
     * body to the client. It will not make it back to a custom responder, but the NetConnection
     * Debugger will show the event.
     */
    private void unhandledError(ActionContext context, Throwable t)
    {
        ActionMessage responseMessage = new ActionMessage();
        context.setResponseMessage(responseMessage);

        MessageBody responseBody = new MessageBody();
        responseBody.setTargetURI(context.getRequestMessageBody().getResponseURI());

        context.getResponseMessage().addBody(responseBody);

        MessageException methodResult;

        if (t instanceof MessageException)
        {
            methodResult = (MessageException)t;
        }
        else
        {
            // An unhandled error occurred while processing client request(s).
            methodResult = new SerializationException();
            methodResult.setMessage(UNHANDLED_ERROR);
            methodResult.setRootCause(t);
        }

        responseBody.setData(methodResult);
        responseBody.setReplyMethod(MessageIOConstants.STATUS_METHOD);

        logger.info(t.getMessage());
    }
}
