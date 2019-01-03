<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Trustworthy Rating</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>Declare Another User as Trustworthy or Not </h1>
				
			</div>
		</div>
		<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
				
					<form action="trustworthy.jsp">
					
						User you want to declare:  <br/>
						<input class="form-control" type=text name="otherUser"  ><br/>
					  	Type 1 for trusted or 0 for not trusted: <br/>
						<input class="form-control" type=text name="isTrusted"><br/>
					  					
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
	
	String otherUser = request.getParameter("otherUser");
	String isTrustedString = request.getParameter("isTrusted");
	
	if(otherUser != null && isTrustedString != null){
		Integer trusted = 0;
		try {
			trusted = Integer.parseInt(isTrustedString);
		} catch (NumberFormatException e){
			out.println("<div align='center'>Trust Rating must be a number</div>");
			return;
		}
		
		if (trusted == 1 || trusted == 0){
			boolean isTrusted = false;
			if(trusted == 1){
				isTrusted = true;
			}
			try{
				DBApi api = new DBApi();
				
				if(!api.isUberUser(conn.stmt, otherUser)){
					out.println("<div align='center'>Not a valid user</div>");
					return;
				}
				
				if(api.isTrusted(conn.stmt, (String)session.getAttribute("username"), otherUser)){
					out.println("<div align='center'>You have already delclared this user as trusted or not trusted</div>");
					return;
				}
				
				if(api.addTrustedUser((String)session.getAttribute("username"), otherUser, isTrusted, conn.stmt)) {
					out.println("<div align='center'>Successfully added trust rating</div>");
				}
				else{
					out.println("<div align='center'>Failed to add trust rating");
				}
				
			}catch(Exception e){
				out.println("<div align='center'>Failed to add trust rating</div>");
			}
		}else {
			out.println("<div align='center'>Trust Rating must 0 for not trusted or 1 for trusted</div>");
		}
	}
		
	%>
	
	
	</body>
	</html>		
		
		
		
		