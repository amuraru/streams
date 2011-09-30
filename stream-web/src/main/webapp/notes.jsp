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


	<jsp:include page="/WEB-INF/jsp/search.jsp" />


	<div style="clear: both; margin-top: 10px;"></div>
	
<div style="margin-top: 20px; height: 32px;">
</div>

		<div style="margin: 40px;" class="markdownHtml">

<p>
	The following notes are currently stored in the database:
</p>

<table>
	<tr>
		<th>Note</th>
	</tr>
	
	<%
		stream.web.services.NoteService noteService = new stream.web.services.NoteServiceImpl();
		Set<stream.web.NoteRef> list = noteService.search( "" );
		for( stream.web.NoteRef ref : list ){
	%>
	
	<tr>
		<td>
			<a href="<%= request.getContextPath() %>/notes/<%= ref.getKey() %>"><%= ref.getKey() %></a>
		</td>
	</tr>
	
	<% } %>
	

</table>

		</div>		

  <jsp:include page="/WEB-INF/jsp/footer.jsp" />

</body>		
</html>
