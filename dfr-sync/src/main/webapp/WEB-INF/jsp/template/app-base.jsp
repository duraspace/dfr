<?xml version="1.0" encoding="ISO-8859-1"?>
<%@include file="../include/libraries.jsp"%>
<div>
  <div
   id="header">
    <ul class="tabs">
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
  </div>

  <div
   id="content">
    <tiles:insertAttribute
     name="content" />
  </div>

  <div
   id="footer">
    <ul>
      <li>
        <a
         href="http://www.duraspace.org">DuraSpace</a>
      </li>

      <li>
        <a
         href="http://www.duracloud.org">DuraCloud</a>
      </li>

      <li>
        <a
         href="http://www.duraspace.org/dfr">DfR</a>
      </li>
    </ul>
  </div>
</div>

