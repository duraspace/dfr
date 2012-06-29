<?xml version="1.0" encoding="ISO-8859-1"?>
<%@include file="../include/libraries.jsp"%>
<ul
 class="tabs">
  <li>
    <a
     href="${pageContext.request.contextPath}/status">
      <spring:message
       code="status" />
    </a>
  </li>

  <li>
    <a
     href="${pageContext.request.contextPath}/errors">
      <spring:message
       code="errors" />
    </a>
  </li>

  <li>
    <a
     href="${pageContext.request.contextPath}/configuration">
      <spring:message
       code="configuration" />
    </a>
  </li>

  <li>
    <a
     href="${pageContext.request.contextPath}/log">
      <spring:message
       code="log" />
    </a>
  </li>
</ul>

