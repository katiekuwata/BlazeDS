BlazeDS Build readme file

BlazeDS build script requires that the following products are installed.
    - ANT 1.7.0
    - ANT-CONTRIB-1.0b2
    - Sun JDK 5
    - JUnit (required in ANT_HOME/lib for "unit" target)

BlazeDS build script requires that the following environment variable are set properly.
    - JAVA_HOME
    - ANT_HOME 
    - JAVA_HOME\bin and ANT_HOME\bin must be on the path.

The build.xml at the top level of the installed BlazeDS will build the product.
The following steps will create a new version of BlazeDS and the sample applications.
    - ant clean
    - ant main
    - ant checkintests

The ant checkintests will verify if the build was successful. This step runs all unit tests developed.

© 2004-2008 Adobe Systems Incorporated. All rights reserved. 
