<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div id="page-header" style="margin-bottom: 0px; margin-top: 0px; background-color: #f0f0f0; height: 64px; vertical-align: bottom;">

  <% if ( session.getAttribute( "LOGIN" ) != null ) { %>
  <div class="link">
	<a href="<%= request.getContextPath() %>/logout">Logout</a>
  </div>
  <% } %>
<!-- 
</div>
    
<div style="text-align: right; background-color: #e0e0e0;">
 -->
  	<form action="<%= request.getContextPath() %>/search.jsp" method="post">
 <div style="text-align: right; vertical-align: bottom; padding: 10px;">
  		<input type="text" style="vertical-align: top;" name="q" /> &nbsp; <input type="image" value="search" src="<%= request.getContextPath() %>/images/search.png" />
 </div>
  	</form>
</div>    
