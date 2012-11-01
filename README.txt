DuraCloud for Research
----------------------

Please consult https://wiki.duraspace.org/x/KSb7AQ to find out how to build and
install DfR software from source for a more complete document.

To build and install locally, skipping all tests:

  mvn clean install -DskipTests=true

To run unit tests only:

  mvn clean verify

To run all tests (unit and integration*):

  mvn clean verify -DskipITs=false

To generate maven site with unit and integration* test reports, unit test
coverage, findbugs, cpd, and javadocs reports:

  mvn clean verify site-deploy -DskipITs=false
  
  (output goes to /tmp/dfr-site or C:\tmp\dfr-site)

----------------------------------------------------------------------
*NOTE: To run integration tests successfully, you'll need to have the
       duracloud.password and the fedora.password system property defined.
       This allows the tests to successfully connect to the hosts. You may
       also need to set the URL of the DuraCloud and Fedora hosts. There are
       several ways you can do this, including passing the property
       via -D on the maven command line or putting something like the
       following in your maven settings.xml file:
--------------------------------------------------------------------

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <localRepository/>
  <interactiveMode/>
  <usePluginRegistry/>
  <offline/>
  <pluginGroups/>
  <servers/>
  <mirrors/>
  <proxies/>
  <profiles>
    <profile>
      <id>ocs-integration-test</id>
      <activation>
        <activeByDefault>false</activeByDefault>
      </activation>
      <properties>
        <fedora.baseurl>http://dfr.duracloud.org:8080/fedora</fedora.baseurl>
        <fedora.password>dgirocks</fedora.password>
        <duracloud.password>research!</duracloud.password>
      </properties>
    </profile>
  </profiles>
  <activeProfiles/>
</settings>

--------------------------------------------------------------------
To get the actual passwords, contact a DfR developer.
--------------------------------------------------------------------

Latest maven site (master branch, built automatically after a push to github):
  https://bamboo.duraspace.org/browse/DFR-TEST/latest/artifact/JOB1/Maven-Site/index.html

For more information about this project, see the wiki:
  https://wiki.duraspace.org/display/DFR
