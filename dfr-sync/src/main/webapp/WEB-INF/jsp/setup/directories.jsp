<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%>
<%-- Status Page: displays configuration information for the synchronization process. --%>
<%-- Author: Daniel Bernstein --%>

<%@include file="../include/libraries.jsp"%>

<tiles:insertDefinition
  name="setup-wizard"
  flush="true">
  <tiles:putAttribute name="title">Setup</tiles:putAttribute>
  <tiles:putAttribute
    name="panelTitle"
    cascade="true">
      Directories
    </tiles:putAttribute>
  <tiles:putAttribute
    name="panelMessage"
    cascade="true">
      [panel message / info here]
    </tiles:putAttribute>

  <tiles:putAttribute
    name="panelContent"
    cascade="true">

    <form method="POST">
      <table>
        <thead>
          <button
            id="add"
            type="submit"
            name="_eventId_add">
            <spring:message code="add" />
          </button>
        </thead>
        <tbody>
          <tr>
            <td>Directory Config List goes here.
          </tr>
        </tbody>
      </table>
      <fieldset class="button-bar">
        <button
          id="next"
          type="submit"
          name="_eventId_save">
          <spring:message code="save" />
        </button>
        <button
          id="cancel"
          type="submit"
          name="_eventId_cancel">
          <spring:message code="cancel" />
        </button>
      </fieldset>
    </form>
  </tiles:putAttribute>
</tiles:insertDefinition>
