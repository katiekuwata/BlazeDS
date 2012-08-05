<%@page import="flex.samples.marketdata.Feed"%>
<%
	try {
		Feed feed = new Feed();
		feed.stop();
		out.println("Feed Stopped");
	} catch (Exception e) {
		out.println("A problem occured while stopping the feed: "+e.getMessage());
	}
%>