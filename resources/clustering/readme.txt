Clustering readme file

Clustering support for data services is provided by JGroups 2.7.0.GA.  The source code
for JGroups 2.7.0 is available in the BlazeDS SVN repository or from
http://www.jgroups.org

Copy jgroups.jar to your data services web application's WEB-INF/lib directory.
Either one or both of the properties files (jgroups-tcp.xml and
jgroups-udp.xml) in {install_root}/resources/clustering directory should be
copied over to WEB-INF/flex directory. Please refer to the JGroups
documentation for more information regarding these properties files. For most
data services deployments, you should use jgroups-tcp.xml as TCP provides for an
easier deployment in networks that do not support UDP.  The TCP protocol also
may perform better in a typical data services cluster since each server will be
sending messages to every other server.  In this situation, UDP may encounter
packet collisions that require messages to be re-sent which can reduce overall
throughput in the cluster.

For usage information, see the Using Software Clustering topic in the  developer guide.

© 2004-2009 Adobe Systems Incorporated. All rights reserved.
