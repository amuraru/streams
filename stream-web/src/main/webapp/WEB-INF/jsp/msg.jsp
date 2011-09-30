<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
 <% if( request.getSession().getAttribute( "MSG" ) != null ){ %>
     <div class="msg">
   		<%= request.getSession().getAttribute( "MSG" ) %>
     </div>
     
  <% 
        request.getSession().removeAttribute( "MSG" );  
     } 
     
   %>