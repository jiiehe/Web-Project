<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>membership menu</title>
</head>
<body>
	<div class="jumbotron">
			<div align="center">	
				<h1>VIP MENU</h1>
				<h2>your account is an Uber <%=session.getAttribute("user") %> account</h2>
			</div>
		</div>
	<div align="center">
	<h2><li><a href="reservation.jsp">make reservation</a></li></h2>
	<h2><li><a href="newcar.jsp">add new UC(driver user only)</a></li></h2>
	<h2><li><a href="favoritecar.jsp">declare your favorite car</a></li></h2>
	<h2><li><a href="feedback.jsp">provide feedback for a car</a></li></h2>
	<h2><li><a href="ratefeedback.jsp">rate a feedback</a></li></h2>
	<h2><li><a href="recordride.jsp">record a ride</a></li></h2>
	<h2><li><a href="trustworthy.jsp">declare another user as trustworthy</a></li></h2>
	<h2><li><a href="browseucs.jsp">browse UCs</a></li></h2>
	<h2><li><a href="usefulfeedback.jsp">see most useful feedback on any UD</a></li></h2>
	<h2><li><a href="index.html"> Logout</a></li></h2>
	</div>
	<%	
		String username=(String)session.getAttribute("username"); 
		String password=(String)session.getAttribute("password"); 
		//System.out.println(username);
		//System.out.println(password);
	%>
</body>
</html>