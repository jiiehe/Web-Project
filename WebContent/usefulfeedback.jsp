<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" import="java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Most Useful Feedbacks</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>Get a List of the Most Useful Feedbacks About an Uber Driver</h1>
				
			</div>
		</div>
		<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
				
					<form action="usefulfeedback.jsp">
					
						Uber Driver username: <br/>
						<input class="form-control" type=text name="driver"  ><br/>
					  	Max number of feedbacks desired: <br/>
						<input class="form-control" type=text name="maxFeedbackString"><br/>
					  					
						<br/>
						<br/>
						
						<input class="form-control"  class = "btn btn-primary btn-lg btn-block" type="submit" name="submit" value = "add">
					</form>
					</div>
			</div><!--row-->
		</div><!--container-->
		<div align="center">
         			<h1><li><a href="menu.jsp">return back menu</a></li></h1>
				  
		</div>
		
	<%
	if(session.getAttribute("user")!="user"){
		System.out.println("hello");
		session.setAttribute("user", "user");
		response.sendRedirect("checkuser.jsp");
		
	}
	
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
	
	String driverUser = request.getParameter("driver");
	String numFeedbacksString = request.getParameter("maxFeedbackString");
	
	if(driverUser != null && numFeedbacksString != null){
		int numFeedbacks = 10;
		DBApi api = new DBApi();
		ResultSet result = null;
		
		try {
			numFeedbacks = Integer.parseInt(numFeedbacksString);
		}catch(NumberFormatException e){
			out.println("<div align='center'>Max number of feedbacks must be a number</div>");
			return;
		}
		
		result = api.usefulFeedbacks(conn.stmt, driverUser, numFeedbacks);

		if(result != null) {
			System.out.println("<div align='center'>feedbacks about " + driverUser + " ----------------------</div>");
			try {
				while (result.next()) {
					out.println("<div align='center'>Name: " + result.getString("name")
							+ ", Feedback ID: " + result.getString("fid")
							+ ", Score: " + result.getString("score")
							+ ", Text: " + result.getString("text")
							+ ", Date: " + result.getString("fbdate")
							+ ", Usefulness Rating " + result.getString("usefullnessRating")
							+ "</div>");
				}
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("<div align='center'>Failed to print results></div>");
			}
		}
		else {
			System.out.println("<div align='center'>No results matched your criteria.</div>");
			return;
		}
	}
	
	
	
	
	
	
	%>
	
	
	</body>
	</html>	