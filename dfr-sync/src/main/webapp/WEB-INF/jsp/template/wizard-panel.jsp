<%-- 
  The contents of this file are subject to the license and copyright
  detailed in the LICENSE and NOTICE files at the root of the source
  tree and available online at
 
      http://duracloud.org/license/
--%>

<%@include file="../include/libraries.jsp"%>
<div class="wizard-panel" >
  <div class="section">
    <div class="header">
      <span> <tiles:insertAttribute name="panelTitle" />
      </span>

    </div>
    <div class="body">
      <p class="notice">
        <tiles:insertAttribute name="panelMessage" />
      </p>

      <tiles:insertAttribute name="panelContent" />
      
    </div>
  </div>
</div>
