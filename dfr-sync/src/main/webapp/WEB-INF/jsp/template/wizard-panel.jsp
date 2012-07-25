<%@include file="../include/libraries.jsp"%>
<div class="wizard-panel">
  <h2>
    <tiles:insertAttribute name="panelTitle" />
  </h2>
  <p class="notice">
    <tiles:insertAttribute name="panelMessage" />
  </p>
  <tiles:insertAttribute name="panelContent" />
</div>
