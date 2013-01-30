<?xml version="1.0" encoding="ISO-8859-1"?>
<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>
<%-- Author: Daniel Bernstein --%><%@include
file="../include/libraries.jsp"%>
<tiles:insertDefinition
 name="setup-wizard"
 flush="true">
  <tiles:putAttribute
   name="title">Setup</tiles:putAttribute>

  <tiles:putAttribute
   name="panelTitle"
   cascade="true">Add Directory</tiles:putAttribute>

  <tiles:putAttribute
   name="panelMessage"
   cascade="true">
   
   </tiles:putAttribute>

  <tiles:putAttribute
   name="panelContent"
   cascade="true">
    <jsp:include page="/WEB-INF/jsp/include/directoryForm.jsp" />
  </tiles:putAttribute>
</tiles:insertDefinition>

