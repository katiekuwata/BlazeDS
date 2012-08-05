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
package flex.messaging.services.http;

import flex.management.runtime.messaging.services.http.SOAPProxyAdapterControl;
import flex.messaging.messages.Message;
import flex.messaging.messages.SOAPMessage;
import flex.messaging.messages.HTTPMessage;
import flex.messaging.services.http.proxy.ProxyContext;
import flex.messaging.Destination;
import flex.messaging.MessageException;

/**
 * A Soap specific subclass of HttpProxyAdapter to
 * allow for future web services features.
 *
 * @author Peter Farland
 */
public class SOAPProxyAdapter extends HTTPProxyAdapter
{
    private SOAPProxyAdapterControl controller;
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructs an unmanaged <code>SOAPProxyAdapter</code> instance.
     */
    public SOAPProxyAdapter()
    {
        this(false);
    }
    
    /**
     * Constructs a <code>SOAPProxyAdapter</code> instance.
     * 
     * @param enableManagement <code>true</code> if the <code>SOAPProxyAdapter</code> has a
     * corresponding MBean control for management; otherwise <code>false</code>.
     */
    public SOAPProxyAdapter(boolean enableManagement)
    {
        super(enableManagement);
    }
    
    //--------------------------------------------------------------------------
    //
    // Other Public APIs
    //                 
    //-------------------------------------------------------------------------- 

    /** {@inheritDoc} */    
    public Object invoke(Message msg)
    {
        HTTPMessage message = (HTTPMessage)msg;
        ProxyContext context = new ProxyContext();

        if (message instanceof SOAPMessage)
        {
            context.setSoapRequest(true);
        }

        setupContext(context, message);

        try
        {
            filterChain.invoke(context);
            return context.getResponse();
        }
        catch (MessageException ex)
        {
            throw ex;
        }
        catch (Throwable t)
        {
            // this should never happen- ErrorFilter should catch everything
            t.printStackTrace();
            throw new MessageException(t.toString());
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Protected/private APIs
    //             
    //--------------------------------------------------------------------------
    
    /**
     * Invoked automatically to allow the <code>SOAPProxyAdapter</code> to setup its corresponding
     * MBean control.
     * 
     * @param broker The <code>Destination</code> that manages this <code>SOAPProxyAdapter</code>.
     */
    protected void setupAdapterControl(Destination destination)
    {
        controller = new SOAPProxyAdapterControl(this, destination.getControl());        
        controller.register();
        setControl(controller);
    }
}
