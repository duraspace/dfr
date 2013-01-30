<?xml version="1.0" encoding="ISO-8859-1"?>
<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>
<%-- Author: Daniel Bernstein --%>
<%@include
file="../include/libraries.jsp"%>
<tiles:insertDefinition
 name="setup-wizard"
 flush="true">
  <tiles:putAttribute
   name="title">Setup</tiles:putAttribute>

  <tiles:putAttribute
   name="panelTitle"
   cascade="true">Welcome!</tiles:putAttribute>

  <tiles:putAttribute
   name="panelMessage"
   cascade="true">
   </tiles:putAttribute>

  <tiles:putAttribute
   name="panelContent"
   cascade="true">
    <div class="welcome">
      <h1> 
        Welcome to the <br/>
        Duracloud for Research<br/>
        Sync Tool
      </h1>
      <p>
      Once set up, this application will automatically backup your vital 
      digital files to multiple cloud storage providers, thus ensuring
      your data is always accessible and secure.
      </p>      
    </div>
    <form
     method="POST" action="${flowExecutionUrl}">
      <fieldset
       class="button-bar">
        <button
         id="next"
         type="submit"
         name="_eventId_next">
          <spring:message
           code="continue" />
        </button>
      </fieldset>
    </form>
  </tiles:putAttribute>
</tiles:insertDefinition>

