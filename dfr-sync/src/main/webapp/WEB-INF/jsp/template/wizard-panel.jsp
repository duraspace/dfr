<%@include file="../include/libraries.jsp"%>
<div style="text-align: center">
  <div class="wizard-panel section">
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
