<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%>
<%-- Status Page: displays errors for the synchronization process. --%>
<%-- Author: Daniel Bernstein --%>

<%@include file="./include/libraries.jsp"%>

<ul
  class="jqueryFileTree"
  style="display: none;">
  <c:forEach
    items="${children}"
    var="child">
    <c:if test="${child.directory}">
      <li class="directory collapsed"><a
        href="#"
        rel="${child.absolutePath}/"> ${child.name}</a></li>
    </c:if>
  </c:forEach>
  <c:forEach
    items="${children}"
    var="child">
    <c:if test="${!child.directory}">
    
      <c:set var="child" value="${child}" scope="request" />
      <%
          java.io.File child = (java.io.File)request.getAttribute("child"); 
          int dotIndex = child.getName().lastIndexOf('.');
          String ext =
              dotIndex > 0 ? child.getName().substring(dotIndex + 1) : "";
      %>

      <li class="file ext_${ext}"><a
        href="#"
        rel="${child.absolutePath}"> ${child.name}</a></li>
    </c:if>
  </c:forEach>
</ul>
