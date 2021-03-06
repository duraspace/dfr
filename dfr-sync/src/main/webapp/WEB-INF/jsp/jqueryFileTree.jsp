<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>
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
        rel="${child.getAbsolutePath()}/"> 
        <%--on windows, child.getName() will be empty when called on root directories --%>
        <c:choose>
         <c:when test="${not empty child.getName()}">
          ${child.getName()}
         </c:when>
         <c:otherwise>
            ${child.getAbsolutePath()}
         </c:otherwise>
         </c:choose>
       </a></li>
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
        rel="${child.getAbsolutePath()}"> ${child.getName()}</a></li>
    </c:if>
  </c:forEach>
</ul>
