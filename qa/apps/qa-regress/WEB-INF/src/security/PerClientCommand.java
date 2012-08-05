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

package security;

import java.security.Principal;
import java.util.List;

import javax.servlet.ServletConfig;

import flex.messaging.security.LoginCommand;

public class PerClientCommand implements LoginCommand {

	public Principal doAuthentication(String username, Object credentials) {
		PerClientPrincipal principal = new PerClientPrincipal(username);
		return principal;
	}

	public boolean doAuthorization(Principal principal, List roles) {
		return true;
	}

	public boolean logout(Principal principal) {

		return false;
	}

	public void start(ServletConfig config) {

	}

	public void stop() {
		
	}

}
