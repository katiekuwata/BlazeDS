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
package flex.messaging.util.concurrent;

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.RejectedExecutionException;

/**
 * Implements {@link Executor} and extends the <code>ThreadPoolExecutor</code> provided by the
 * Java 1.4.x-friendly backport of the <code>java.util.concurrent</code> API.
 * 
 * @exclude
 */
public class DefaultThreadPoolExecutor extends ThreadPoolExecutor implements Executor
{
    //--------------------------------------------------------------------------
    //
    // Constructors
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs a <code>DefaultThreadPoolExecutor</code> with configuration that
     * matches the return from {@link java.util.concurrent.Executors#newCachedThreadPool()}.
     */
    public DefaultThreadPoolExecutor()
    {
        super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue());
    }   
    
    /**
     * @see java.util.concurrent.ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue)
     */
    public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    
    /**
     * @see java.util.concurrent.ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue, java.util.concurrent.RejectedExecutionHandler)
     */
    public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, RejectedExecutionHandler handler)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);        
    }
    
    /**
     * @see java.util.concurrent.ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue, java.util.concurrent.ThreadFactory)
     */
    public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, ThreadFactory threadFactory)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);        
    }
    
    /**
     * @see java.util.concurrent.ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit, java.util.concurrent.BlockingQueue, java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)
     */
    public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);        
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Instance-level lock.
     */
    private final Object lock = new Object();
    
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  failedExecutionHandler
    //----------------------------------

    private FailedExecutionHandler handler;
    
    /**
     * @see Executor#getFailedExecutionHandler()
     */
    public FailedExecutionHandler getFailedExecutionHandler()
    {
        synchronized (lock)
        {
            return handler;
        }
    }
    
    /**
     * @see Executor#setFailedExecutionHandler(FailedExecutionHandler)
     */
    public void setFailedExecutionHandler(FailedExecutionHandler value)
    {
        synchronized (lock)
        {
            handler = value;
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Overridden Public Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * @exclude
     * This implementation relies on the default <code>RejectedExecutionHandler</code>, {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy}, which
     * throws a <code>RejectedExecutionException</code> if the command cannot be queued for execution.
     */
    public void execute(Runnable command)
    {
        try
        {
            super.execute(command);
        }
        catch (RejectedExecutionException e)
        {
            FailedExecutionHandler handler = getFailedExecutionHandler();
            if (handler != null)
            {
                handler.failedExecution(command, this, e);
            }
            else if (Log.isError())
            {
                Log.getLogger(LogCategories.EXECUTOR).error("DefaultThreadPoolExecutor hit a RejectedExecutionException but no FailedExecutionHandler is registered to handle the error.", e);
            }
        }
    }
}
