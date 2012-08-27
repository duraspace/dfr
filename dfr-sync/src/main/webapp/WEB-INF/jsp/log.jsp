<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%>
<%-- Log Page: displays log of the synchronization process. --%>
<%-- Author: Daniel Bernstein --%>

<%@include file="./include/libraries.jsp"%>
<tiles:insertDefinition
  name="app-base"
  flush="true">

  <tiles:putAttribute name="primaryTab" value="log" cascade="true"/>
  
  <tiles:putAttribute
    name="content"
    cascade="true">
    <div id="log">Log Content Goes Here</div>
  </tiles:putAttribute>
</tiles:insertDefinition>

