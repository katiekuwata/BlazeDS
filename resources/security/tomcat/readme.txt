You need to perform the following configuration steps to use custom authentication with 
BlazeDS on Tomcat:

1. Put flex-tomcat-common.jar in tomcat/lib/blazeds.
2. Place flex-tomcat-server.jar in tomcat/lib/blazeds.
3. Edit the catalina.properties file which can be found in the tomcat/conf directory. Find the common.loader property and add the following path to the end of the list: ${catalina.home}/lib/blazeds/*.jar
4. Add <Valve className="flex.messaging.security.TomcatValve"/> tag to the Context Descriptors.
5. Restart Tomcat.

You will now be authenticated against the current Tomcat realm. Usually, for this authentication the user information is in conf/tomcat-users.xml. See the Tomcat documentation for more information on realms. See the Flex documentation for more information on BlazeDS custom authentication.
