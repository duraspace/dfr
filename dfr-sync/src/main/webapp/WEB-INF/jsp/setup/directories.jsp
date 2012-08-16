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
          <c:choose>
            <c:when test="${not empty directoryConfigs}">
              <c:forEach
                items="${directoryConfigs}"
                var="dc">
                <tr>
                  <td>${dc.directoryPath}</td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <p>There are no configured directories at this time.</p>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
      <fieldset class="button-bar">
        <button
          id="next"
          type="submit"
          name="_eventId_save">
          <spring:message code="next" />
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
