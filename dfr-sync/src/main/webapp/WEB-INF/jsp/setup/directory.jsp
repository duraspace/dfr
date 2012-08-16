<?xml version="1.0" encoding="ISO-8859-1"?>
<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%><%-- Status
Page: displays configuration information for the synchronization process.
--%><%-- Author: Daniel Bernstein --%><%@include
file="../include/libraries.jsp"%>
<tiles:insertDefinition
 name="setup-wizard"
 flush="true">
  <tiles:putAttribute
   name="title">Setup</tiles:putAttribute>

  <tiles:putAttribute
   name="panelTitle"
   cascade="true">Add Directory</tiles:putAttribute>

  <tiles:putAttribute
   name="panelMessage"
   cascade="true">Select a directory by clicking on the file tree below and click 'Add'</tiles:putAttribute>

  <tiles:putAttribute
   name="panelContent"
   cascade="true">

            <label>Select Path</label>

            <div
             id="filetree"
             style="height: 200px; width: 400px; overflow: auto; background-color: #FFF">
            <!--  -->
            </div>

    <form:form
     method="POST"
     modelAttribute="directoryConfigForm">
      <form:hidden id="directoryPath"
       path="directoryPath" />

      <fieldset
       class="button-bar">
        <button
         id="add"
         type="submit"
         name="_eventId_add">
          <spring:message
           code="add" />
        </button>

        <button
         id="cancel"
         type="submit"
         name="_eventId_cancel">
          <spring:message
           code="cancel" />
        </button>
      </fieldset>
    </form:form>

    <script xml:space="preserve">
      $(function() {

          $('#filetree').fileTree({
              root : '',
              script : '${pageContext.request.contextPath}/ajax/jqueryFileTree'
          }, function(file) {
              $("#directoryPathText").html(file);
              $("#directoryPath").val(file);
          });
      });
    </script>
  </tiles:putAttribute>
</tiles:insertDefinition>

