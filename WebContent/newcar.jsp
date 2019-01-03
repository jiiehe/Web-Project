<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>new CAR</title>
</head>
<body>
	<div class="jumbotron">
			<div align="center">	
				<h1>add a new car</h1>
				
			</div>
		</div>
	
	
		<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
				
					<form action="newcar.jsp">
						<%
						
						if(session.getAttribute("user")!="driver"){
							System.out.println("hello");
							session.setAttribute("user", "driver");
							response.sendRedirect("checkuser.jsp");
							
						}
						%>
						please type the vin number of your new car:  <br/>
						<input class="form-control" type=text name="vin"  ><br/>
					  	please type the model of your new car: <br/>
						<input class="form-control" type=text name="model"><br/>
						please type the make of your new car:  <br/>
						<input  class="form-control"  type=text name="make" ><br/>
						please type the category of your new car<br/>
						<input  class="form-control"  type=text name="category" ><br/>				
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
Connector conn=null;


try{
	conn=new Connector();

}catch(Exception e){
	out.println("<div align='center'>reset your network, and try again</div>");
	return;
}


String vin = request.getParameter("vin");
String model = request.getParameter("model");
String make = request.getParameter("make");
String category = request.getParameter("category");

if(vin!=null&&model!=null&&make!=null&&category!=null){
	List<String> categories = Arrays.asList("SUV", "Sedan", "Compact", "Truck");
	
	if(!categories.contains(category)){
		out.println("<div align='center'>Category must be SUV, Sedan, Compact, or Truck</div>");
		return;
	}
	
	DBApi api=new DBApi();
	try {
		int temp=Integer.parseInt(vin);
		Integer temp2 = temp;
		System.out.println((String)session.getAttribute("username"));
		if(api.uberCarExisits(conn.stmt, temp2)){
			out.println("<div align='center'>That car already exists in the system</div>");
			return;
		}
		if(api.addNewCar(temp, (String)session.getAttribute("username"),category,make,model,conn.stmt)) {
			out.println("<script>alert('successfully added new car');</script>");
			
		}else{
			out.println("<div align='center'>Failed to add a new car</div>");
			
		}
	}catch(Exception e){
		
		out.println("<div align='center'>Invalid VIN</div>");
		return;
	}
}



%>>

</body>
</html>