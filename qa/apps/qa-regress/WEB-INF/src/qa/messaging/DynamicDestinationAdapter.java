/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package qa.messaging;

import flex.messaging.services.messaging.adapters.ActionScriptAdapter;
import flex.messaging.services.messaging.Subtopic;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;

import java.security.Principal;
/**
 * The <code>DynamicDestinationAdapter</code> tests adding custom logic to 
 * determine whether the client should be able to subscribe or send messages
 * to the specified subtopic.
 */
public class DynamicDestinationAdapter extends ActionScriptAdapter  {

    /**
     * Adds custom logic to determine whether the client should be able to subscribe.
     * Users guest, employee, supervisor and manager are allowed to subscribe.
     * 
     * @param subtopic The subtopic the client is attempting to send a message to.
     * @return true to allow the message to be sent, false to prevent it. 
     */
    public boolean allowSubscribe(Subtopic subtopic) {
        FlexSession session = FlexContext.getFlexSession();

        String separator = subtopic.getSeparator();

        System.out.println("DynamicDestinationAdapter.allowSubscribe()");
        System.out.println("  destination.id = " + getDestination().getId());
        System.out.println("  subtopic.getValue() = " + subtopic.getValue());
        System.out.println("  subtopic.containsSubtopicWildcard()? " + subtopic.containsSubtopicWildcard());
        System.out.println("  subtopic.isHierarchical()? " + subtopic.isHierarchical());
        System.out.println("  subtopic.getSeparator()? " + subtopic.getSeparator());

        getDestination().getId();

        Principal principal = session.getUserPrincipal();
        String principalName = principal == null? "" : principal.getName();

        // allow managers full access
        if (session.isUserInRole("managers")) {
            System.out.println("-> allowing manager access");
            return true;

        // allow supervisors to subscribe to anything but *hr*
        } else if (session.isUserInRole("supervisors") && subtopic.getValue().indexOf(separator + "hr") < 0 ) {
            System.out.println("-> allowing supervisor access to a topic that does not contain *hr*");
            return true;

        // allow supervisors and employees to subscribe to anything that does not contain wildcards or *hr*
        } else if (!subtopic.containsSubtopicWildcard() && subtopic.getValue().indexOf(separator + "hr") < 0
                && (principalName.equals("employee") || principalName.equals("supervisor"))) {
            System.out.println("-> allowing supervisor or employee access to a non-wildcarded topic that does not contain *hr*");
            return true;

        } if (!subtopic.containsSubtopicWildcard() && !subtopic.isHierarchical() && subtopic.getValue().indexOf(separator + "hr") < 0 && principalName.equals("guest") ) {
            System.out.println("-> allowing guest access to a non-wildcarded non-hierarchical topic that does not contain *hr*");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds custom logic to determine whether the client should be able to send to the 
     * specified subtopic. 
     * 
     * @param subtopic The subtopic the client is attempting to send a message to.
     * @return true to allow the message to be sent, false to prevent it. 
     */
    public boolean allowSend(Subtopic subtopic) {
        FlexSession session = FlexContext.getFlexSession();
        String separator = subtopic.getSeparator();

        Principal principal = session.getUserPrincipal();
        String principalName = principal == null? "" : principal.getName();

        // allow managers full access
        if (principalName.equals("manager")) {
            return true;

        // allow supervisor, employee to send messages to anything but *hr*
        } else if ((session.isUserInRole("employees") || session.isUserInRole("supervisors")) && subtopic.getValue().indexOf(separator + "hr") < 0 ) {
            return true;

        // allow guests to send messages to 'sandbox' only.
        } else if (session.isUserInRole("guests") && subtopic.matches(new Subtopic("sandbox", separator))) {
            return true;
        } else {
            return false;
        }
    }

}
