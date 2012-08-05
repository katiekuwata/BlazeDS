You need to perform the following configuration steps on JBoss4 to use custom authentication with BlazeDS on JBoss:

   1. Copy flex-tomcat-common.jar and flex-tomcat-server.jar from {blazeds_install}/resources/security/tomcat folder to {jboss_root}/server/default/lib folder.
   2. Copy {blazeds_install}/resources/security/tomcat/context.xml in your web application under the WEB-INF directory (META-INF for Tomcat) (or adjust an existing context.xml to add the <Valve>).
   3. Restart JBoss. 

You will now be authenticated against the current JBoss realm. Usually, the default for this authentication stores user information in {jboss_root}/server/default/conf/users.properties and roles information in {jboss_root}/server/default/conf/roles.properties. 