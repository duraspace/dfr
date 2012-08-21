<%@include file="../include/libraries.jsp"%>
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

  <li>
    <a
     href="#${pageContext.request.contextPath}/log">
      <spring:message
       code="log" />
    </a>
  </li>
</ul>

