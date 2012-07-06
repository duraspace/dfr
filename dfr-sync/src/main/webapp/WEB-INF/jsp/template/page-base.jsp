<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%>
<%-- Template base for all pages --%>
<%-- Author: Daniel Bernstein --%>

<%@include file="../include/libraries.jsp"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<jsp:directive.page contentType="text/html; charset=utf-8" />
<head>
<title>DfR Sync</title>
<link
  rel="stylesheet"
  type="text/css"
  href="${pageContext.request.contextPath}/static/css/global.css" />
<link
  rel="stylesheet"
  type="text/css"
  href="http://yui.yahooapis.com/3.5.1/build/cssgrids/grids-min.css">
</head>

<body>
  <tiles:insertAttribute name="body" />
</body>
</html>
