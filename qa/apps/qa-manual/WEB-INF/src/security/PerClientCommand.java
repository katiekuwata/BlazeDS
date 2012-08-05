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
