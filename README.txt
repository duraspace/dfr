DuraCloud for Research
----------------------
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
       duracloud.password system property defined. This allows the
       tests to successfully connect to the dfrtest host. There are
       several ways you can do this, including passing the property
       via -D on the maven command line or putting something like the
       following in your maven settings.xml file:
--------------------------------------------------------------------

  ..
  <profiles>
    <profile>
      <id>dfr-test</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <duracloud.password>ACTUAL-PASSWORD</duracloud.password>
      </properties>
    </profile>
  </profiles>
</settings>

--------------------------------------------------------------------
To get the actual password for dfrtest, contact a DfR developer.
--------------------------------------------------------------------

Latest maven site (master branch, built automatically after a push to github):
  https://bamboo.duraspace.org/browse/DFR-TEST/latest/artifact/JOB1/Maven-Site/index.html

For more information about this project, see the wiki:
  https://wiki.duraspace.org/display/DFR
