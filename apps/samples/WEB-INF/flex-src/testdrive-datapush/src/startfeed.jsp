<%@page import="flex.samples.feed.Feed"%>
<%
	try {
		Feed feed = new Feed();
		feed.start();
		out.println("Feed Started");
	} catch (Exception e) {
		out.println("A problem occured while starting the feed: "+e.getMessage());
	}
%>