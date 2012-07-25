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
   cascade="true">Enter Duracloud Credentials</tiles:putAttribute>

  <tiles:putAttribute
   name="panelMessage"
   cascade="true">[panel message / info here]</tiles:putAttribute>

  <tiles:putAttribute
   name="panelContent"
   cascade="true">
    <form:form
     method="POST"
     modelAttribute="duracloudCredentialsForm">
      <fieldset>
        <ol>
          <li>
            <form:label
             cssErrorClass="error"
             path="username">
              <spring:message
               code="username" />
            </form:label>

            <form:input
             cssErrorClass="error"
             path="username"
             autofocus="true" />

            <form:errors
             path="username"
             cssClass="error"
             element="div" />
          </li>

          <li>
            <form:label
             cssErrorClass="error"
             path="password">
              <spring:message
               code="password" />
            </form:label>

            <form:input
             cssErrorClass="error"
             path="password" />

            <form:errors
             path="password"
             cssClass="error"
             element="div" />
          </li>

          <li>
            <form:label
             cssErrorClass="error"
             path="host">
              <spring:message
               code="host" />
            </form:label>

            <form:input
             cssErrorClass="error"
             path="host" />

            <form:errors
             path="host"
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

