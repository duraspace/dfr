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
   cascade="true">Select Space</tiles:putAttribute>

  <tiles:putAttribute
   name="panelMessage"
   cascade="true">
    Select a destination space for your content.
   
   </tiles:putAttribute>

  <tiles:putAttribute
   name="panelContent"
   cascade="true">
    <form:form
     method="POST"
     modelAttribute="spaceForm">
      <fieldset>
        <ol>
          <li>
            <form:label
             cssErrorClass="error"
             path="spaceId">
              <spring:message
               code="spaceId" />
            </form:label>

            <form:select
             cssErrorClass="error"
             path="spaceId"
             items="${spaces}"
             autofocus="true"/>

            <form:errors
             path="spaceId"
             cssClass="error"
             element="div" />
          </li>
        </ol>
      </fieldset>

      <fieldset
       class="button-bar">
        <button
         id="next"
         type="submit"
         name="_eventId_next">
          <spring:message
           code="next" />
        </button>

        <button
         id="cancel"
         type="submit"
         name="_eventId_cancel">
          <spring:message
           code="cancel" />
        </button>
      </fieldset>
    </form:form>
  </tiles:putAttribute>
</tiles:insertDefinition>

