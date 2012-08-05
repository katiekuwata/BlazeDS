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
package flex.messaging.services.http.proxy;


/**
 * @exclude
 * Base filter definition that defines the filter contract.
 * Filters perform pre- and post-processing duties on the ProxyContext
 *
 * @author Brian Deitte
 */
public abstract class ProxyFilter
{
    protected ProxyFilter next;

    public ProxyFilter()
    {
    }

    public ProxyFilter getNext()
    {
        return next;
    }

    public void setNext(ProxyFilter next)
    {
        this.next = next;
    }

    /**
     * The core business method.
     */
    public abstract void invoke(ProxyContext context);
}
