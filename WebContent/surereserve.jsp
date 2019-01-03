<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" import="java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Confirmation</title>
<script LANGUAGE="javascript">

function check(var1){
	
	var1=true;
	window.location.href="surereserve.jsp?msg=hello"
}

</script> 
</head>
<body>
	<div class="jumbotron">
			<div align="center">	
				<h1>confirm your reservation</h1>
				
			</div>
		</div>
		<div align="center">
		<font size="8" color="red">are you sure you want to make this reservation?</font>
		</div>
		
		
		
		<div align="center">
		<font size="4"><% out.println("<div align='center'>Reservation: Date: " + session.getAttribute("day") + " VIN: " + session.getAttribute("vin") + " Start: " + session.getAttribute("start") + " End: " + session.getAttribute("end") + " Cost: " + session.getAttribute("cost")); %></font>
		</div>
		<form action="surereserve.jsp">					
			<div align="center">			
				<input class="form-control"  class = "btn btn-primary btn-lg btn-block" type="button" name="button"  onclick="check(click);" value = "Yes">
			</div>
		
		</form>
		
		
		
			<div align="center">
         			<h1><li><a href="reservation.jsp">back to reservation</a></li></h1>
				  
		</div>
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
		boolean click=false;
		java.sql.Date sqlDate=(java.sql.Date)session.getAttribute("day"); 
		int vin=(int)session.getAttribute("vin"); 
		
		int start=(int)session.getAttribute("start"); 
		
		int end=(int)session.getAttribute("end");
		
		int cost=(int)session.getAttribute("cost"); 
		//out.println("<div align='center'>Reservation: Date: " + sqlDate + " VIN: " + vin + " Start: " + start + " End: " + end + " Cost: " + cost);
		//System.out.println(request.getParameter("msg"));
		if(request.getParameter("msg")!=null&&request.getParameter("msg").equals("hello")){
			
			DBApi api=new DBApi();
			CommandLineInterface commandLineInterface = new CommandLineInterface();
			if(api.carIsAvailable(start, end, sqlDate, vin, conn.stmt)){
				if(api.addReservationWorks(conn.stmt, (String)session.getAttribute("username"), cost, sqlDate, vin, start, end)){
					out.println("<div align='center'>succesfully added reservation</div>");
					out.println("<div align='center'>Other suggested cars you may like: </div>");
					ResultSet result = commandLineInterface.ucSuggestions(conn, (String)session.getAttribute("username"), vin);
					if (result != null) {
						try {
							int count = 0;
							
							while(result.next()) {
								count += 1;
								out.println("<div align='center'>" + result.getString("vin")
										+ " " + result.getString("category")
										+ " " + result.getString("make")
										+ " " + result.getString("model")
										+ " " + result.getString("popular")
										+ "</div>");
							}
							if (count == 0){
								out.println("<div align='center'>No suggestions found</div>");
							}
						} catch (SQLException e) {
							out.println("<div align='center'>Failed to show results</div>");
							return;
						}
					}
				
				}else{
					out.println("<div align='center'>Failed to add reservation</div>");
					return;
				}
			}
			else{
				out.println("<div align='center'>This car is not available during that time.</div>");
				return;
			}
			
			
		}
		
		%>
		
</body>
</html>