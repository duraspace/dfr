<?xml version="1.0" encoding="ISO-8859-1"?>
<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%><%-- Status
Page: displays configuration information for the synchronization process.
--%><%-- Author: Daniel Bernstein --%><%@include
file="../include/libraries.jsp"%>
<tiles:insertDefinition
 name="setup-wizard"
 flush="true">
  <tiles:putAttribute
   name="title">Setup</tiles:putAttribute>

  <tiles:putAttribute
   name="panelTitle"
   cascade="true">You're ready to start syncing.</tiles:putAttribute>

  <tiles:putAttribute
   name="panelMessage"
   cascade="true">
   Click the start button to begin syncing your content.
   </tiles:putAttribute>

  <tiles:putAttribute
   name="panelContent"
   cascade="true">
    <form
     method="POST">
      <fieldset
       class="button-bar">
        <button
         id="startNow"
         type="submit"
         name="_eventId_startNow">
          <spring:message
           code="startNow" />
        </button>
        <!-- 
        <button
         id="startLater"
         type="submit"
         name="_eventId_startLater">
          <spring:message
           code="startLater" />
        </button>
        -->
      </fieldset>
    </form>
  </tiles:putAttribute>
</tiles:insertDefinition>

