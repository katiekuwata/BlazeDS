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

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ibm.websphere.asynchbeans.Work;
import com.ibm.websphere.asynchbeans.WorkEvent;
import com.ibm.websphere.asynchbeans.WorkException;
import com.ibm.websphere.asynchbeans.WorkListener;
import com.ibm.websphere.asynchbeans.WorkManager;

import flex.messaging.config.ConfigurationException;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;

/**
 * Implements {@link Executor} by delegating command execution to a WAS asynchbeans <code>WorkManager</code>.
 * For more information on the asynchbeans API, refer to the WAS Javadoc for 
 * <a href="http://publib.boulder.ibm.com/infocenter/wasinfo/v5r0/index.jsp?topic=/com.ibm.wasee.doc/info/ee/javadoc/ee/com/ibm/websphere/asynchbeans/WorkManager.html">WorkManager</a>.
 *
 * @exclude
 */
public class AsynchBeansWorkManagerExecutor implements Executor
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructs an <code>AsynchBeansWorkManagerExecutor</code> that will delegate command execution
     * to the specified <code>WorkManager</code> instance that is registered in JNDI.
     * 
     * @param workManagerJNDIName The JNDI resource ref name for the <code>WorkManager</code>.
     * @see com.ibm.websphere.asynchbeans.WorkManager
     */    
    public AsynchBeansWorkManagerExecutor(String workManagerJNDIName)
    {
        try
        {
            InitialContext ic = new InitialContext();
            workManager = (WorkManager)ic.lookup(workManagerJNDIName);
        }
        catch(NamingException ne)
        {
            ConfigurationException ce = new ConfigurationException();
            ce.setMessage(13600, new Object[] {workManagerJNDIName});
            ce.setRootCause(ne);
            throw ce;
        }
        
        workListener = new WorkListener() {
            public void workAccepted(WorkEvent event) 
            { 
                /* No-op */                 
            }
            public void workCompleted(WorkEvent event) 
            {
                // This only needs to be handled if execution of the Runnable failed.
                WorkException e = event.getException();
                if (e != null)
                {
                    if (Log.isDebug())
                        Log.getLogger(LogCategories.EXECUTOR).error("AsynchBeansWorkManager's WorkListener.workCompleted() callback invoked for failed execution.", e);
                    
                    handleFailedExecution(((WorkCommandWrapper)event.getWork()).command, e);
                }
            }
            public void workRejected(WorkEvent event) 
            {
                WorkException e = event.getException();
                if (Log.isDebug())
                    Log.getLogger(LogCategories.EXECUTOR).error("AsynchBeansWorkManager's WorkListener.workRejected() callback invoked. WorkException? " + e);
                
                handleFailedExecution(((WorkCommandWrapper)event.getWork()).command, e);
            }
            public void workStarted(WorkEvent event) 
            { 
                /* No-op */ 
            }
        };
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     * Instance level lock for thread-safe state changes.
     */
    private final Object lock = new Object();
    
    /**
     * Reference to the WorkManager instance configured in WAS that this executor instance delegates to.
     */
    private final WorkManager workManager;
    
    /**
     * Listener that monitors scheduled work for errors and notifies the FailedExecutionHandler if one has been set.
     */
    private final WorkListener workListener;
    
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  failedExecutionHandler
    //----------------------------------
    
    private FailedExecutionHandler failedExecutionHandler;
    
    /** {@inheritDoc} */
    public FailedExecutionHandler getFailedExecutionHandler()
    {
        synchronized (lock)
        {
            return failedExecutionHandler;            
        }
    }
    
    /** {@inheritDoc} */
    public void setFailedExecutionHandler(FailedExecutionHandler value)
    {
        synchronized (lock)
        {
            failedExecutionHandler = value;
        }
    }    
    
    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------
    
    /** {@inheritDoc} */
    public void execute(Runnable command)
    {
        try
        {
            // Register our listener to monitor each scheduled work, and set the start timeout for the work to indefinite (no queue timeout).
            workManager.startWork(new WorkCommandWrapper(command), WorkManager.INDEFINITE, workListener);
        }
        catch (WorkException e)
        {
            handleFailedExecution(command, e);
        }
    } 
    
    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * Handles command execution problems by notifying the FailedExecutionHandler if one has been set
     * and otherwise logging the failure.
     * 
     * @param command The command that failed to execute successfully.
     * @param e The exception generated by the failed command.
     */
    private void handleFailedExecution(Runnable command, Exception e)
    {
        FailedExecutionHandler handler = getFailedExecutionHandler();
        if (handler != null)
        {
            handler.failedExecution(command, this, e);
        }   
        else if (Log.isError())
        {
            Log.getLogger(LogCategories.EXECUTOR).error("AsynchBeansWorkManager hit an Exception but no FailedExecutionHandler is registered to handle the error.", e);
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Inner Classes
    //
    //--------------------------------------------------------------------------

    /**
     * Helper class that wraps Runnable commands in the WAS Work interface.
     */
    class WorkCommandWrapper implements Work
    {
        public WorkCommandWrapper(Runnable command)
        {
            this.command = command;
        }
        
        private final Runnable command;
        
        public void run()
        {
            command.run();
        }
        
        /**
         * This is invoked by WAS when the server is shutting down to signal long-running daemon threads spawned by the WorkManager
         * to exit from their run() method. Our works are all short lived so this is a no-op; in this case  WAS will force any 
         * works that are executing at server shutdown to terminate.
         */
        public void release()
        {
            // No-op.
        }
    }
}
