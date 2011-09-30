<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<jsp:include page="/WEB-INF/jsp/header.jsp" />
<body>
  <jsp:include page="/WEB-INF/jsp/page-header.jsp" />

  <h2 align="center">Login to Sink</h2>

	<jsp:include page="/WEB-INF/jsp/msg.jsp" />
	
	<form action="<%= request.getContextPath() %>/j_spring_security_check" method="post">
		<table align="center" style="margin: auto; border: none;">
			<tr>
				<td>Username:</td>
				<td> <input type="text" name="j_username" /> </td>
			</tr>
			<tr>
				<td>Password:</td>
				<td> <input type="passord" name="j_password" /> </td>
			</tr>
			<tr>
				<td><input type="checkbox" name="_spring_security_remember_me"></td>
				<td>Don't ask for my password for two weeks</td>
			</tr>
 
			<tr>
				<td style="border: none;" colspan="2" align="center"> <input type="submit" value="Login"/> </td>
			</tr>
		</table>
	</form>
	
  
  <jsp:include page="/WEB-INF/jsp/footer.jsp" />
	
</body>
</html>
