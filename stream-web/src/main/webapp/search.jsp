<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<jsp:include page="/WEB-INF/jsp/header.jsp" />
<body>
  <jsp:include page="/WEB-INF/jsp/page-header.jsp" />



<div style="margin: 40px; margin-top: 10px;" class="markdownHtml">

<%
    String query = request.getParameter( "q" );
	if( query != null ){
%>
<p>
   The following notes/memos have been found for your search <b><%= query %></b>:
</p>
<%  } %>

<table>

	<tr>
		<th>Results</th>
	</tr>
<%
	if( query != null ){
		
		stream.web.services.NoteService service = new stream.web.services.NoteServiceImpl();
		java.util.Set<stream.web.NoteRef> results = service.search( query );
		
		for( stream.web.NoteRef ref : results ){
 %>
 		<tr>
 			<td>
 				<a href="<%= request.getContextPath() %>/notes/<%= ref.getKey() %>"><%= ref.getKey() %></a>
 			</td>
 		</tr>
 
 
<% 	    }
	}
%>
</table>

</div>

</body>		
</html>
