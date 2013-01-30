<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>
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
       To add one or more directories, click the 'Add' button below. When you are finished, click 'Next' 
       to continue with the setup. 
    </tiles:putAttribute>

  <tiles:putAttribute
    name="panelContent"
    cascade="true">

    <form method="POST">
      <table>
        <thead>
          <tr>
            <th>
              <ul class="button-bar">
                <li>
                    <button
                      id="add"
                      type="submit"
                      name="_eventId_add">
                      Add Directory or Files
                    </button>
                </li>
              </ul>
            </th>
          <tr>
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
