<%@ page language="java" import="cs5530.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>login as a membership</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>Welcome to Login!</h1>
				<h2>we never forget who keep our business</h2>
			</div>
		</div>
		
		
		
	<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
					<form action="login.jsp">
						username:
						<input class="form-control" type=text name="username" ><br/>
					    Password: 
						<input class="form-control" type=text name="password"><br/>
						<input class="form-control" type="submit" name="submit"
						value="login">
					</form>
					</div>
			</div><!--row-->
		</div><!--container-->
	
	
	<%
	Connector conn=(Connector)session.getAttribute("conn");
	
	if(conn == null){
		try{
			conn=new Connector();
			session.setAttribute("conn", conn);
			//System.out.println("welcome to Uber system");
		}catch(Exception e){
			out.println("<div align='center'>reset your network, and try again</div>");
			return;
		}
	}
	String username=request.getParameter("username");
	String password=request.getParameter("password");
	if(username!=null&&password!=null){
	DBApi api=new DBApi();
	if(api.checkUser(username,password,conn.stmt)==true){
		out.println("<div align='center'>login in successful!</div>");
		boolean check=api.isUberUser(conn.stmt, username);
		if(check==true){
			session.setAttribute("user", "user");
		}else{
			session.setAttribute("user","driver");
		}
		session.setAttribute("username", username);
		session.setAttribute("password", password);
		response.sendRedirect("menu.jsp");
		
	}else{
		out.println("<div align='center'>input information incorrect</div>");
		
	}
	}
	
	
	%>
 
</body>
</html>