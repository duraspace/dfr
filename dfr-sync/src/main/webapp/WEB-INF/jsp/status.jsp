<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%>
<%-- Status Page: displays state and progress information for the synchronization process. --%>
<%-- Author: Daniel Bernstein --%>

<%@include file="./include/libraries.jsp"%>
<tiles:insertDefinition
  name="app-base"
  flush="true">
  <tiles:putAttribute
    name="content"
    cascade="true">
    <div class="yui3-g">
      <div
        id="status-overview"
        class="yui3-u-1-2 ">
        <div class="content">
          <div class="section top">
            <div class="header">
              <form
                method="POST"
                action="${pageContext.request.contextPath}/status">

                <span> <spring:message code="overview" />
                </span>

                <ul class="button-bar">
                  <!-- 
                  <li>
                    <button
                      id="cleanStart"
                      <c:if test="${syncProcessState != 'STOPPED' }">
                        disabled="disabled"
                      </c:if>>
                      <spring:message code="cleanStart" />
                    </button>
                  </li>
                  -->

                  <li>
                    <button
                      id="start"
                      type="submit"
                      name="start"
                      <c:if test="${syncProcessState != 'STOPPED' }">
                        disabled="disabled"
                      </c:if>>
                      <spring:message code="start" />
                    </button>

                  </li>
                  <li>
                    <button
                      id="stop"
                      name="stop"
                      <c:if test="${syncProcessState != 'RUNNING' }">
                        disabled="disabled"
                      </c:if>>
                      <spring:message code="stop" />
                    </button>
                  </li>
                </ul>
              </form>

            </div>
            <div class="body">
              <fmt:formatNumber
                var="queueSize"
                value="${syncProcessStats.queueSize}" />

              <div
                id="status-indicator"
                class="yui3-g">

                <div class="yui3-u-1-2">${syncProcessState }</div>
                <div class="yui3-u-1-2">[state message text]</div>
              </div>
              <table>
                <tbody>
                  <tr>
                    <td><spring:message code="startDate" /></td>
                    <td>${syncProcessStats.startDate}</td>
                  </tr>
                  <tr>
                    <td><spring:message code="estimatedCompletionDate" /></td>
                    <td>${syncProcessStats.estimatedCompletionDate}</td>
                  </tr>
                  <tr>
                    <td><spring:message code="queueSize" /></td>
                    <td>${queueSize}</td>
                  </tr>

                  <tr>
                    <td><spring:message code="errorCount" /></td>
                    <td>${syncProcessStats.errorCount}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      <div class="yui3-u-1-2">
        <div class="content">
          <div id="active-syncs" class="section top">
            <div class="header">Active Syncs</div>
            <div class="body">
              <c:choose>
                <c:when test="${not empty monitoredFiles}">
                  <table>
                    <c:forEach
                      items="${monitoredFiles}"
                      var="file">
                      <tr>
                        <td>
                        <progress
                          max="100"
                          value="${100*file.streamBytesRead/file.length()}">
                        </progress>
                        </td>
                        <td>
                          <fmt:formatNumber value="${file.length()/(1024*1000)}" maxFractionDigits="2"/> MBs
                        </td>
                        <td>
                          ${file.name}
                        </td>
                      </tr>
                    </c:forEach>
                  </table>
                  
                </c:when>
                <c:otherwise>
                  <p>There are no active uploads at this time.</p>
                </c:otherwise>
              </c:choose>
              <h4>Recent Activity</h4>
              <div id="recent-activity-graph"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="tabs yui3-u-1">
        <div class="content">
          <div
            id="status-tabs"
            class="section">
            <div class="header">
              <ul class="tabs">
                <li class="selected">Queued for Synchronization
                  (${queueSize})</li>
                <li>Errors</li>
              </ul>
            </div>
            <div class="body">
              <c:choose>
                <c:when test="${not empty queuedFiles}">
                  <table>
                    <thead>
                      <tr>
                        <th>File Path</th>
                        <th>Size</th>
                        <th>Last Modified Date</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach
                        items="${queuedFiles}"
                        var="file">
                        <jsp:useBean
                          id="lastModifiedDate"
                          class="java.util.Date" />
                        <jsp:setProperty
                          name="lastModifiedDate"
                          property="time"
                          value="${file.lastModified()}" />


                        <tr>
                          <td>${file.absolutePath}</td>
                          <td><fmt:formatNumber value="${file.length()}" /></td>
                          <td><fmt:formatDate
                              value="${lastModifiedDate}"
                              pattern="MM/dd/yyyy HH:mm a z" /></td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>

                </c:when>
                <c:otherwise>
                  <p>There are no files currently queued for
                    synchronization.
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </div>
    </div>
  </tiles:putAttribute>
</tiles:insertDefinition>
