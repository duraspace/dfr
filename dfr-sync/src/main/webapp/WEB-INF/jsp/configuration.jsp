<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%>
<%-- Status Page: displays configuration information for the synchronization process. --%>
<%-- Author: Daniel Bernstein --%>

<%@include file="./include/libraries.jsp"%>
<tiles:insertDefinition
  name="app-base"
  flush="true">
  <tiles:putAttribute name="primaryTab" value="configuration" cascade="true"/>
  <tiles:putAttribute
    name="content"
    cascade="true">

    <div class="yui3-g">
      <div
        id="watched-directories"
        class="yui3-u-1-2 ">
        <div class="content">
          <div class="section top">
            <div class="header">
              <span> <spring:message code="watchedDirectories" />
              </span>
              <ul class="button-bar">
                <li>
                  <a id="add" class="button">Add</a>
                </li>
              </ul>
              
            </div>
            <div class="body">
              <table id="directories" >
                <tbody>
                  <c:choose>
                    <c:when test="${not empty directoryConfigs}">
                      <c:forEach
                        items="${directoryConfigs}"
                        var="dc">
                        <tr>
                          <td>${dc.directoryPath}</td>
                          <td>
                            <c:if test="${directoryConfigs.size() > 1}">
                              <form action="configuration/remove" method="post">
                                <input type="hidden" name="directoryPath" value = "${dc.directoryPath}"/>
                                <button class="trash" type="submit" title="remove">Remove</button>
                              </form>
                            </c:if>
                          </td>
                        </tr>
                      </c:forEach>
                    </c:when>
                    <c:otherwise>
                      <p>There are no configured directories at this time.</p>
                    </c:otherwise>
                  </c:choose>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      
            <div
        id="duracloud-configuration"
        class="yui3-u-1-2 ">
        <div class="content">
          <div class="section top">
            <div class="header">
              <span> <spring:message code="duracloudConfiguration" /></span>
              <ul class="button-bar">
                <li>
                  <a id="edit" class="button">
                    <spring:message code="edit"/>
                  </a>
                </li>
              </ul>
              <div id="add-dialog" class="dialog" style="display:none"></div>
              <div id="edit-dialog" class="dialog" style="display:none"></div>

              <script>
              	$(function(){
						
          	       var addCancelButtonHandler = function(dialog){
        	           $("#cancel",dialog).click(function(e){
          	               $(dialog).dialog("close");
          	               e.preventDefault();
          	               return false;
          	           });
          	       }

              	    //add handler to open add directory dialog
             	    $("#add").click(function(e){
              	        e.preventDefault();
              	        $("#add-dialog").dialog("open");
              	        return false;
              	    });
 
              	    //initialize add dialog
              	    $("#add-dialog").dialog({
              	       modal: true,
              	       open: function(){
              	           var dialog = this;
						   var addDirectoryUrl = "configuration/add";

						   //subroutine to handle replacing the dialog contents
						   var loadContent = function(jqxhr, dialog){
    						    
						       //replace the content
						       $(dialog)
                	                	.empty()
                	                	.append($(jqxhr.responseText));

						      //attach cancel button listener
    						  addCancelButtonHandler(dialog);

						      //attach add button listener
    						  $("#add", dialog).click(function(e){
                     	           alert("about to add!");
               	               var jqxhr = $.post(addDirectoryUrl, $("#directoryConfigForm").serialize())
               	                .done(function(){
   									if(jqxhr.responseText.indexOf("success") < 0){
   									    //recursively call load content on result if unsuccessful.
   									    loadContent(jqxhr, dialog);
   									}else{
   									    window.location.reload();
   									}
   									return false;
               	               });

               	               e.preventDefault();
               	               return false;
             	               });
						   };

              	           var jqxhr = $.get(addDirectoryUrl)
              	            .done(function(){
              	              loadContent(jqxhr, dialog);
              	            });
              	       },
              	       position:"top",
              	       autoOpen: false,
              	       closeText: "",
              	       width: "500px"
              	    	
              	    });
              	    

              	    $("#edit").click(function(e){
              	        e.preventDefault();
              	        $("#edit-dialog").dialog("open");
              	        return false;

              	    });

               	    $("#edit-dialog").dialog({
               	       modal: true,
               	       open: function(){
               	           var dialog = this;
              	           
               	           var loadContent = function(jqxhr, dialog){
            	                $(dialog).empty().append($(jqxhr.responseText).find(".section"));
            	                addCancelButtonHandler(dialog);
            	                
            	                //add next button handler
            	                $("#next",dialog).click(function(e){
            	                   var action = $("form",dialog).attr("action");
								   var data = $("form",dialog).serialize();
								   data+="&_eventId_" + $(e.target).attr("id");
          	    	               var jqxhr =  $.post(
 	    	                       			action, 
 	    	                       			data)
         	     	                .done(function(){
        	     	                    loadContent(jqxhr, dialog);
         	     	                });
            	                   
          	      	               e.preventDefault();
          	    	               //return false;
          	    	           });
               	           };
               	           
              	           var jqxhr = $.get("duracloud-config")
             	            .done(function(){
             	                loadContent(jqxhr, dialog);  
             	            });

               	           
               	       },
               	       position:"top",
               	       autoOpen: false,
               	       closeText: "",
               	       width: "500px"
               	    	
               	    });

              	});
              	
              
              	
              	
              </script>
              
            </div>
            <div class="body">
              <table>
                <tr>
                  <td><spring:message code="username"/></td>
                  <td>${duracloudConfiguration.username}</td>
                </tr>
                <tr>
                  <td><spring:message code="host"/></td>
                  <td>${duracloudConfiguration.host}</td>
                </tr>
                <c:if test="${!empty port}">
                  <tr>
                    <td><spring:message code="port"/></td>
                    <td>${duracloudConfiguration.port}</td>
                  </tr>
                </c:if>
                <tr>
                  <td><spring:message code="spaceId"/></td>
                  <td>${duracloudConfiguration.spaceId}</td>
                </tr>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

  </tiles:putAttribute>
</tiles:insertDefinition>

