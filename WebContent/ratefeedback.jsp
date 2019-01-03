<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Usefulness Rating</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>Rate Another User's Feedback</h1>
				
			</div>
		</div>
		<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
				
					<form action="ratefeedback.jsp">
					
						Feedback ID of the feedback you want to rate:  <br/>
						<input class="form-control" type=text name="fid"  ><br/>
					  	Rating for the feedback 0, 1, or 2: <br/>
						<input class="form-control" type=text name="rating"><br/>
					  					
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
	String stringFid = request.getParameter("fid");
	String rating = request.getParameter("rating");
	
	if(stringFid != null && rating != null){
		Integer fid = 0;
		try {
			fid = Integer.parseInt(stringFid);
		} catch (NumberFormatException e){
			out.println("<div align='center'>Feedback ID must be a number</div>");
			return;
		}
		try{
			Integer rate = Integer.parseInt(rating);
			
			if (rate == 0 || rate == 1 || rate == 2)
			{
				DBApi api = new DBApi();
				try {
					if(api.userGaveTheFeedback(conn.stmt, (String)session.getAttribute("username"), fid))
					{
						out.println("<div align='center'>You can't rate your own feedback</div>");
						return;
					}
					else
					{
						if(api.addRate((String)session.getAttribute("username"), fid, rate, conn.stmt)){
							out.println("<div align='center'>Successfully added rating</div>");
						}
						else{
							out.println("<div align='center'>Failed to add rating</div>");
						}
					}
				}catch(Exception e){
					out.println("Failed to check user and fid");
				}
			}
			else
			{
				out.println("<div align='center'>Rating must be a 0, 1, or 2");
			}
			
		}catch(Exception e){
			out.println("<div align='center'>Invalid input, try again.");
			return;
		}
	}
	
	%>
		
		
</body>
</html>