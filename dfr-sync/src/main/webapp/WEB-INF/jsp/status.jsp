<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>
<%-- Status Page: displays state and progress information for the synchronization process. --%>
<%-- Author: Daniel Bernstein --%>

<%@include file="./include/libraries.jsp"%>
<tiles:insertDefinition
  name="app-base"
  flush="true">

  <tiles:putAttribute name="primaryTab" value="status" cascade="true"/>

  <tiles:putAttribute
    name="head-extension"  >
    <script>
    	$(function(){
			var skipRefresh = false;
    	    var refresh = function(){
    			var x = $.get(window.location) 
				 .done(function(){
				     if(!skipRefresh){
					     var body = $(x.responseText, "body");
					     $(document.body).empty().append(body.children());
				     }
				 });			
    	    };
    	    
    	    setInterval(refresh, 5000);
			
    	    
    	    $("#recent-activity, #errors").live("mouseover",function(){
    	       skipRefresh = true; 
    	    }).live("mouseout", function(){
     	       skipRefresh = false; 
    	    });
    	    
			    	    
    	})
    </script>
  </tiles:putAttribute>
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
                  <li>
                    <button
                      id="stop"
                      name="stop"
                      <c:if test="${syncProcessState != 'RUNNING' && syncProcessState != 'PAUSED' }">
                        disabled="disabled"
                      </c:if>>
                      <spring:message code="stop" />
                    </button>
                  </li>

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
                      id="pause"
                      name="pause"
                      
                      <c:if test="${syncProcessState != 'RUNNING' }">
                        disabled="disabled"
                      </c:if>>
                      <spring:message code="pause" />
                    </button>
                  </li>

                  <li>
                    <button
                      id="resume"
                      name="resume"
                      <c:if test="${syncProcessState != 'PAUSED' }">
                        disabled="disabled"
                      </c:if>>
                      <spring:message code="resume" />
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
                class="yui3-g  ${fn:toLowerCase(syncProcessState)} <c:if test="${ currentError != null }">error</c:if>">

                <div class="yui3-u-1-2  state"></div>
                <div class="yui3-u-1-2">
                  <c:choose>
                    <c:when test="${currentError != null }">
                      ${currentError.detail}                    
                    </c:when>
                    <c:when test="${syncProcessState == 'RUNNING' && queueSize == 0 && empty monitoredFiles }">
                      <spring:message code="waiting" />
                    </c:when>
                    <c:otherwise>
                      <spring:message code="${fn:toLowerCase(syncProcessState)}"/>
                    </c:otherwise>
                  </c:choose>
                  
                   
                </div>
              </div>
              <table>
                <tbody>
                  <tr>
                    <td><spring:message code="startDate" /></td>
                    <td>
                      <c:choose>
                        <c:when test="${not empty syncProcessStats.startDate}">
                            ${syncProcessStats.startDate}</td>
                        
                        </c:when>
                        <c:otherwise>
                          --
                        </c:otherwise>
                      </c:choose>
                  </tr>
                  <!-- estimated completion date has not yet been implemented. 
                  <tr>
                    <td><spring:message code="estimatedCompletionDate" /></td>
                    <td>${syncProcessStats.estimatedCompletionDate}</td>
                  </tr>
                   -->
                  
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
                      <c:set var="percent" value="${100*file.streamBytesRead/file.length()}"/>
                      <tr>
                        <td>
                        <progress
                          max="100"
                          value="${percent}">
                        </progress>
                        </td>
                        <td>
                          <fmt:formatNumber value="${percent}" maxFractionDigits="0"/>%  of                     
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
              <div id="recent-activity">
                  <c:choose>
                      <c:when test="${not empty recentlyCompleted}">
                        <table>
                          <thead>
                            <tr>
                              <th>File (hover over name for full path)</th>
                              <th>Size</th>
                              <th>Duration</th>
                              <th>Completed</th>
                            </tr>
                          </thead>
                          <tbody>
                            <c:forEach
                              items="${recentlyCompleted}"
                              var="summary">
                              <tr>
                                <td title="${summary.file.absolutePath}">${summary.file.name}</td>
                                <td>${fileSizeFormatter.format(summary.file.length())}</td>
                                <td>${summary.durationAsString}</td>
                                <td><fmt:formatDate
                                    value="${summary.stop}"
                                    pattern="MM/dd/yyyy HH:mm a z" /></td>
                              </tr>
                            </c:forEach>
                          </tbody>
                        </table>
                      </c:when>
                      <c:otherwise>
                        <p></p>
                      </c:otherwise>
                    </c:choose>              
              </div>
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
                <li class='<c:if test="${statusTab == 'queued'}">selected</c:if>'><a href="?statusTab=queued">Queued for Synchronization</a>
                  (${queueSize})</li>
                <li id="errors-tab" class='<c:if test="${statusTab == 'errors'}">selected</c:if>'><a href="?statusTab=errors">Errors</a></li>
              </ul>
            </div>
            <div class="body">
              <c:choose>
                <c:when test="${statusTab == 'queued' }">
                  <%-- begin queued --%>
                  <div id="queued">
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
                                <td>${fileSizeFormatter.format(file.length())}</td>
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
                          synchronization.</p>
                      </c:otherwise>
                    </c:choose>
                  </div>
                  
                  <%-- end queued --%> 
                </c:when>
                <c:otherwise>
                  <%-- start errors --%>
                  <div id="errors">
                  <c:choose>
                    <c:when test="${not empty failures}">
                        <table>
                          <thead>
                            <tr>
                              <th>File Path</th>
                              <th>Message</th>
                            </tr>
                          </thead>
                          <tbody>
                            <c:forEach
                              items="${failures}"
                              var="summary">
                              <tr>
                                <td>${summary.file.absolutePath}</td>
                                <td>${summary.message}</td>
                              </tr>
                            </c:forEach>
                          </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p>There are no failures at this time.</p>
                       </c:otherwise>
                    </c:choose>
                    </div>                  
                  <%-- end errors --%>
                </c:otherwise>
              </c:choose>
              </div>
          </div>
        </div>
      </div>
    </div>
  </tiles:putAttribute>
</tiles:insertDefinition>

