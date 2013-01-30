<?xml version="1.0" encoding="ISO-8859-1"?>
<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>

<%@include file="../include/libraries.jsp"%>
<div>
  <div id="header">
    <div id="logo">UPSYNC</div>
    <tiles:insertAttribute name="subHeader" />
  </div>

  <div id="content">
    <tiles:insertAttribute name="content" />
  </div>

  <div id="footer">
    <ul>
      <li><a href="http://www.duraspace.org/dfr">Duracloud for Research</a>: v${project.version} ${buildNumber}</li>

      <li><a href="http://www.duracloud.org">DuraCloud</a></li>

      <li><a href="http://www.duraspace.org">DuraSpace</a></li>

    </ul>
  </div>
</div>

