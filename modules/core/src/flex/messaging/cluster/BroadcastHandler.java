/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging.cluster;

import java.util.List;

/**
 * @exclude
 * This interface represents a handler for a message broadcast by a Cluster.
 * Clusters broadcast messages across their physical nodes, and when they
 * receive those messages they locate a BroadcastHandler capable of handling
 * the broadcast.
 *
 * @author neville
 */
public interface BroadcastHandler
{
    /**
     * Handle the broadcast message.
     *
     * @param sender sender of the original message
     * @param params any parameters need to handle the message
     */
    void handleBroadcast(Object sender, List<Object> params);

    /**
     * Determine whether this Handler supports a particular operation by name.
     *
     * @return whether or not this handler supports the named operation
     * @param name name of the operation
     */
    boolean isSupportedOperation(String name);
}
