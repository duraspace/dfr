<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>
<tiles:importAttribute  name="primaryTab"   />
<ul
 class="tabs primary">
  <li class='<c:if test="${primaryTab == 'status'}">selected</c:if>'>
    <a
     href="${pageContext.request.contextPath}/status">
      <spring:message
       code="status" />
    </a>
  </li>

  <li class='<c:if test="${primaryTab == 'configuration'}">selected</c:if>'>
    <a
     href="${pageContext.request.contextPath}/configuration">
      <spring:message
       code="configuration" />
    </a>
  </li>


  <li class='<c:if test="${primaryTab == 'log'}">selected</c:if>'>
    <a
     href="${pageContext.request.contextPath}/log">
      <spring:message
       code="log" />
    </a>
  </li>


</ul>

