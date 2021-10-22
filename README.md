# CHEOPSim_web

This package provides a web interface which can be used to generate configuration files for the CHEOPS simulator [CHEOPSim](https://github.com/davefutyan/CHEOPSim).

The interface can be previewed via the [index.html file](https://htmlpreview.github.io/?https://github.com/davefutyan/CHEOPSim_web/blob/main/index.html). Using it to generate a configuration file requires an installation of Apache Tomcat.

<h3>Requirements</h3>

 * Apache Tomcat: https://tomcat.apache.org/
 * Java SE Runtime Environment (JRE) version compatible with the Tomcat version

<h3>Installation</h3>

A [WAR file](https://github.com/davefutyan/CHEOPSim_web/releases/download/V1.0/cheopsim.war) of the web application is provided with the release of this package. The WAR file can be deployed via the Tomcat Web Application Manager, after which it can be accessed via http://localhost:8080/cheopsim.

If the source code is modified, a new WAR file can be generated using [Apache Ant](https://ant.apache.org/), using the command <i>ant clean</i> followed by the command <i>ant</i> executed from the top directory of this package. The WAR file will appear in a newly created directory <i>dist</i>. Before running ant, the value of <i>basedir</i> in build.xml must be updated to correspond to the path to the top directory of this package.
