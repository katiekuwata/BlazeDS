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


/**
 * @exclude
 * Called when a node leaves the cluster.  Note that for JGroups at least, this
 * callback should not execute any "long running" operations.  This is indirectly
 * called from the MembershipListener interface in JGroups.
 */
public interface RemoveNodeListener
{
    /**
     * Callback that the clustering subsystem uses to notify that a
     * node has been removed from the cluster.
     *
     * @address The node that was removed from the cluster.
     */
    void removeClusterNode(Object address);
}
