<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Feedback</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>Add Feedback</h1>
				
			</div>
		</div>
	<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
				
					<form action="feedback.jsp">
					
						please type a vin number of the car that you want to give feedback for:  <br/>
						<input class="form-control" type=text name="vin"  ><br/>
						Please give a score between 0 and 10 to the car:  <br/>
						<input class="form-control" type=text name="score"  ><br/>
						If you want to give an explanation you may give one here:  <br/>
						<textarea class="form-control" type=text cols="40" rows="5" name="text"  ></textarea><br/>
					  					
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
	String vinString = request.getParameter("vin");
	String scoreString = request.getParameter("score");
	String text = request.getParameter("text");
	
	if (vinString != null && scoreString != null){
		Integer vin = 0;
		Integer score = 0;
		DBApi api = new DBApi();
		try{
			vin = Integer.parseInt(vinString);
		}catch(NumberFormatException e){
			out.println("<div align='center'>VIN must be a number</div>");
			return;
		}
		try{
			score = Integer.parseInt(scoreString);
		}catch(NumberFormatException e){
			out.println("<div align='center'>Score must be a number</div>");
			return;
		}
		if(score > -1 && score < 11){
			
			if(!api.uberCarExisits(conn.stmt, vin)){
				out.println("<div align='center'>That car does not exist in the system</div>");
				return;
			}
			
			if(api.hasGivenFeedbackToCar(conn.stmt, (String)session.getAttribute("username"), vin)){
				out.println("<div align='center'>You have already given feedback on that car</div>");
				return;
			}
			
			if(api.addFeedback(text,vin, (String)session.getAttribute("username"), score, conn.stmt)){
				out.println("<div align='center'>Added new feedback</div>");
				return;
			}else{
				out.println("<div align='center'>Failed to add new feedback</div>");
				return;
			}
			
		}
		else{
			out.println("<div align='center'>Score must be between 0 and 10</div>");
			return;
		}
	}
	
	%>




</body>
</html>