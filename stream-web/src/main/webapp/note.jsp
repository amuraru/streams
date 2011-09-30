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



<form action="<%= request.getContextPath() %>/notes/<%= request.getAttribute( "key" ) %>" method="post">

<!-- 
	<div style="clear: both; margin-top: 0px;"></div>
 -->
<jsp:include page="/WEB-INF/jsp/noteActions.jsp" />

<div style="clear: both;"></div>

<%	if( request.getParameter( "edit" ) != null || "true".equals( request.getAttribute( "edit" ) ) ){ %>
	<div align="center" style="margin: auto;">
	   <textarea id="markdownEdit" style="width: 80%;" rows="20" cols="60" name="text"><%= request.getAttribute( "MARKDOWN_RAW" ) %></textarea>
	</div>
<%  } %>

</form>

		<div style="margin: 40px; margin-top: 10px;" class="markdownHtml">
			<%= request.getAttribute( "MARKDOWN_HTML" ) %>
		</div>		

  <jsp:include page="/WEB-INF/jsp/footer.jsp" />

</body>		
</html>
