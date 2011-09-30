<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<jsp:include page="/WEB-INF/jsp/header.jsp" />
<body>
  <jsp:include page="/WEB-INF/jsp/page-header.jsp" />
  <%
  	Long time = System.currentTimeMillis();
    try {
    	time = Long.parseLong( request.getParameter( "time" ) );
    } catch (Exception e) {
    	time = System.currentTimeMillis();
    }
  %>


  <div style="float: right;">
  	<form action="<%= request.getContextPath() %>/search.jsp" method="post">
  		<input type="text" style="vertical-align: top;" name="q" /> &nbsp; <input type="image" value="search" src="<%= request.getContextPath() %>/images/search.png" />
  	</form>
  </div>



	<div style="clear: both; margin-top: 10px;"></div>
	<div class="noteActions">
		<div class="noteAction">
			<img src="<%= request.getContextPath() %>/images/document-history.png" />
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
		<div class="noteAction">
			<a href="<%= request.getContextPath() %>/notes/<%= request.getAttribute( "key" ) %>.edit?time=<%= time %>"><img src="<%=request.getContextPath() %>/images/document-edit.png"/></a>
		</div>
	</div>



<%	if( request.getParameter( "edit" ) != null || "true".equals( request.getAttribute( "edit" ) ) ){ %>
	<div align="center" style="margin: auto;">
		<form action="<%= request.getContextPath() %>/notes/<%= request.getAttribute( "key" ) %>" method="post">
		<table style="width: 80%;">
			<tr>
				<td>
				   <input type="image" src="<%=request.getContextPath() %>/images/document-save.png" value="Save!" />
				   <a href="<%= request.getContextPath() %>/notes/<%= request.getAttribute( "key" ) %>?time=<%= time %>">
				   		<img src="<%= request.getContextPath() %>/images/cancel.png" />
				   </a>
				</td>
			</tr>
			<tr>
				<td>
				   <textarea id="markdownEdit" style="width: 100%;" rows="20" cols="60" name="text"><%= request.getAttribute( "MARKDOWN_RAW" ) %></textarea>
				</td>
			</tr>		
		</table>
		</form>
	</div>
<%  } %>

		<div style="margin: 40px;" class="markdownHtml">
			<%= request.getAttribute( "MARKDOWN_HTML" ) %>
		</div>		

  <jsp:include page="/WEB-INF/jsp/footer.jsp" />

</body>		
</html>
