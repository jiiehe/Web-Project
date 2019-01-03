<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Browse Uber Cars</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>Browse Uber Cars</h1>
				
			</div>
		</div>
		<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
				
					<form action="browseucs.jsp">
					
						Category (required) <br/>
						<input class="form-control" type=text name="category"  ><br/>
					  	State <br/>
						<input class="form-control" type=text name="state"><br/>
						City  <br/>
						<input class="form-control" type=text name="city"  ><br/>
					  	Model <br/>
						<input class="form-control" type=text name="model"><br/>
						Enter "1" to sort cars based only on feedback that is trusted by you <br/>
						<input class="form-control" type=text name="trusted"><br/>
					  					
						<br/>
						<br/>
						
						<input class="form-control"  class = "btn btn-primary btn-lg btn-block" type="submit" name="submit" value = "Search">
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
		
		String category = request.getParameter("category");
		String state = request.getParameter("state");
		String city = request.getParameter("city");
		String model = request.getParameter("model");
		String trusted = request.getParameter("trusted");
		List<String> categories = Arrays.asList("SUV", "Sedan", "Compact", "Truck");
		
		if(category != null){
			if(!categories.contains(category)){
				out.println("<div align='center'>Category must be SUV, Sedan, Compact, or Truck</div>");
				return;
			}
			boolean onlyTrustedFeedback = false;
			if (trusted != null){
				if(trusted.equals("1")){
					onlyTrustedFeedback = true;
				}
			}
			
			DBApi api = new DBApi();
			ResultSet result = null;
			
			result = api.browseUCs(conn.stmt, (String)session.getAttribute("username"), category, state, city, model, onlyTrustedFeedback);

			if(result != null) {
				out.println("<div align='center'>Uber cars matching " + category + " " + state + " " + city + " " + model + "---------------</div>");
				try {
					while (result.next()) {
						out.println("<div align='center'>" + result.getString("name")
								+ " " + result.getString("city")
								+ " " + result.getString("state")
								+ " " + result.getString("category")
								+ " " + result.getString("make")
								+ " " + result.getString("model")
								+ " " + result.getString("avg")
								+ "</div>");
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("<div align='center'>Failed to print results</div>");
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