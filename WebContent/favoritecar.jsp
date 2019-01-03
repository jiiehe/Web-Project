<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>add your favorite car</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>add favorite car</h1>
				
			</div>
		</div>
	<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
				
					<form action="favoritecar.jsp">
					
						please type a vin number of the car that you want to favorite:  <br/>
						<input class="form-control" type=text name="vin"  ><br/>
					  					
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
	String vin = request.getParameter("vin");


	if(vin!=null){
		
		DBApi api=new DBApi();
		try {
			int temp=Integer.parseInt(vin);
			
			if(api.isFavorite(conn.stmt, (String)session.getAttribute("username"), temp)){
				out.println("<div align='center'>You have already favorited this car</div>");
				return;
			}
			if(api.addFavorite(temp, (String)session.getAttribute("username"),conn.stmt)) {
				out.println("<script>alert('successful to add');</script>");
				
			}else{
				out.println("<div align='center'>failed to add a favorite car, vin does not exist</div>");
				
			}
		}catch(Exception e){
			
			out.println("<div align='center'>this is not a valid vin number, type again!</div>");
				
				
		}
		
		

		
		
		
	}
	
	
	%>




</body>
</html>