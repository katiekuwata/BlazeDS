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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.MembershipListener;
import org.jgroups.View;

/**
 * @exclude
 * Clusters employ this Listener in order to respond to nodes which
 * join and abandon it. This class bridges the low-level protocol layer
 * to the more abstract logical cluster.
 *
 * @author neville
 */
class ClusterMembershipListener implements MembershipListener
{
    /**
     * The logical cluster implementation.
     */
    private JGroupsCluster cluster;

    /**
     * The list of current cluster members as we know it.
     */
    private List memberList;

    /**
     * The list of cluster members that are not currently active.
     */
    private List zombieList;

    /**
     * Our implementation of cluster membership listener.
     *
     * @param cluster The logical cluster implementation.
     */
    public ClusterMembershipListener(Cluster cluster)
    {
        this.cluster = (JGroupsCluster)cluster;
        this.memberList = new ArrayList();
        this.zombieList = new ArrayList();
    }

    /**
     * This method is invoked by the cluster infrastructure whenever
     * a member joins or abandons the cluster group.
     *
     * @param membershipView Snapshot of members of the cluster.
     */
    public void viewAccepted(View membershipView)
    {
        synchronized(this)
        {
            List currentMemberList = (List) membershipView.getMembers();

            // arriving members
            for (Iterator iter=currentMemberList.iterator(); iter.hasNext(); )
            {
                Address member = (Address) iter.next();
                if (!cluster.getLocalAddress().equals(member) && !memberList.contains(member))
                {
                    cluster.addClusterNode(member);
                }
            }

            // departed members
            for (Iterator iter=memberList.iterator(); iter.hasNext(); )
            {
                Address member = (Address) iter.next();
                if (!membershipView.containsMember(member))
                {
                    cluster.removeClusterNode(member);
                    zombieList.remove(member);
                }
            }
            memberList = currentMemberList;
        }
    }

    /**
     * This method is invoked by the cluster infrastructure whenever
     * a member appears to have left the cluster, but before it has
     * been removed from the active member list. The Cluster treats
     * these addresses as zombies and will not use their channel and
     * endpoint information.
     *
     * @param zombieAddress The address of the suspect node.
     */
    public void suspect(Address zombieAddress)
    {
        synchronized(this)
        {
            zombieList.add(zombieAddress);
        }
    }

    /**
     * This method from the core MembershipListener is a no-op for
     * the Flex destination Cluster.
     */
    public void block()
    {
        // no-op
    }

    /**
     * Allow the Cluster to determine whether a given physical address
     * is a zombie.
     *
     * @param address The node to check.
     * @return True, if the given address is a zombie.
     */
    public boolean isZombie(Address address)
    {
        if (zombieList.contains(address))
        {
            return true;
        }
        return false;
    }
}