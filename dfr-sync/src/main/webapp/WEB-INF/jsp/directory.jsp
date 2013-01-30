<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>
<%-- Author: Daniel Bernstein --%>

<%@include file="./include/libraries.jsp"%>
<tiles:insertDefinition
  name="basic-panel">
  <tiles:putAttribute
   name="panelTitle">Add Directory</tiles:putAttribute>


  <tiles:putAttribute
   name="panelContent">
    <jsp:include page="/WEB-INF/jsp/include/directoryForm.jsp" />
  </tiles:putAttribute>
</tiles:insertDefinition>


