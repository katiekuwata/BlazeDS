package qa.messaging;

import flex.messaging.FlexContext;

public class SessionManager {
	public void resetSession() {
		FlexContext.getFlexSession().invalidate();
	}

}
