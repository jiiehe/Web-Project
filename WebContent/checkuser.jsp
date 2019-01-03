<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>you can't do this operation</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
			
				<h1>Sorry, you are not an Uber <%=session.getAttribute("user") %> </h1>
				
				<%
				if(session.getAttribute("user")=="driver"){
					session.setAttribute("user", "user");
				}else{
					session.setAttribute("user", "driver");
				}
				%>
			</div>
		</div>
			<div align="center">
         			<h1><li><a href="menu.jsp">return back menu</a></li></h1>
				  
		</div>
</body>
</html>