<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<% if( "show".equalsIgnoreCase( request.getSession().getAttribute( "headers" ) + "" ) ) { %>
<div style="margin-top: 40px;">
  <h4>Request Headers:</h4>

  <table>
  <tr>
    <th>Header</th>
    <th>Value</th>
  </tr>
  <%
   java.util.Enumeration en = request.getHeaderNames();
   while( en.hasMoreElements() ){
     String header = en.nextElement() + "";
     out.println( "<tr><td><nobr><code>" + header + "</code></nobr></td><td><code>" + request.getHeader( header ) + "</code></td></tr>" );
   }
  %>
  </table>
</div>

<% } %>
