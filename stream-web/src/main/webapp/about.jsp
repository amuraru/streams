
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<jsp:include page="/WEB-INF/jsp/header.jsp" />
<body>
  <jsp:include page="/WEB-INF/jsp/page-header.jsp" />
		

  <div style="margin: auto; padding: 30px;">
  	<%= stream.web.SciNotesContext.staticNote( "about" ) %>
  </div>		


  <jsp:include page="/WEB-INF/jsp/footer.jsp" />

</body>		
</html>