<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>    
	
<div style="margin-top: 0px; height: 28px; padding: 4px;">
	
	<div class="nodeActions" style="float: right; ">
	<% if( request.getAttribute( "edit" ) != null ){ %>
	   	<input type="image" src="<%=request.getContextPath() %>/images/document-export.png" value="Save!" />
		<a href="<%= request.getContextPath() %>/notes/<%= request.getAttribute( "key" ) %>?time=<%= request.getParameter("time") %>">
			<img src="<%= request.getContextPath() %>/images/cancel.png" />
		</a>
	<% } %>
	<% if( request.isUserInRole( "ROLE_USER" ) ){ %>
		<div class="noteAction" style="float: left;">
			<img src="<%= request.getContextPath() %>/images/document-history2.png" />
			<div class="noteActionItem">
			<% 
				List<Date> dates = (List<Date>) request.getAttribute( "history" );
			    if( dates != null ){
				for( Date date : dates ){ %>
					<div>
						<a href="<%=request.getContextPath() %>/notes/<%= request.getAttribute( "key" ) %>?time=<%= date.getTime() %>"><%= date %></a><br/>
					</div>
			<% 	}
			    }
			%>
			</div>
		</div>
		<% } %>
		<% if( request.isUserInRole( "EDIT" ) ){ %>
		<div class="noteAction" style="float: left;">
			<a href="<%= request.getContextPath() %>/notes/<%= request.getAttribute( "key" ) %>.edit?time=<%= request.getParameter("time") %>"><img src="<%=request.getContextPath() %>/images/document-edit.png"/></a>
		</div>
		<% } %>
	</div>

</div>