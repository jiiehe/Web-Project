<%@ page language="java" import="cs5530.*" import="java.util.concurrent.TimeUnit"
	import="phase2.*" import="java.text.*" import="java.util.Date"
	import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Uber Reservation</title>
</head>
<body>
	<div class="jumbotron">
			<div align="center">	
				<h1>Make reservation here</h1>
				
			</div>
		</div>

	<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
					<form action="reservation.jsp">
						
						please type a date you want to make the reservation: <br/>
						<input class="form-control" type=text name="day" placeholder="MM-DD-YYYY" ><br/>
					    please enter the vin of the car you would like:<br/>
						<input class="form-control" type=text name="vin"><br/>
						please type a start time for your trip: <br/>
						<input  class="form-control"  type=text name="start" placeholder="a number 0-24"><br/>
						please type an end time for your trip:<br/>
						<input  class="form-control"  type=text name="end" placeholder="a number 0-24"><br/>
					
						please type the cost that you will spend in this trip: <br/>
						<input  class="form-control"  type=text name="cost"><br/>
						
						<input class="form-control"  class = "btn btn-primary btn-lg btn-block" type="submit" name="submit" value = "Next">
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
	String day = request.getParameter("day");
	String vin = request.getParameter("vin");
	String start = request.getParameter("start");
	String end = request.getParameter("end");
	String cost = request.getParameter("cost");
	DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
	java.util.Date realDate;
	java.sql.Date sqlDate = null;
	Integer loopCount = 0;
	if(day!=null&&vin!=null&&start!=null&&end!=null&&cost!=null){
		String date;
		
	
		
		
		try {
			realDate = df.parse(day);
			sqlDate = new java.sql.Date(realDate.getTime());
			session.setAttribute("day", sqlDate);
				
		}catch(Exception e){
			
			out.println("<div align='center'>this is not a valid date, type again!</div>");
			return;
				
		}
		DBApi api=new DBApi();
		
		try{
			int temp=Integer.parseInt(vin);
			if(!api.uberCarExisits(conn.stmt, temp)){
				out.println("<div align='center'>That car does not exist</div>");
				return;
			}
			session.setAttribute("vin", temp);
		}
		catch(Exception e){
			out.println("<div align='center'>vin must be a number</div>");
			return;
		}
		int startInt = 0;
		try{
			
				startInt=Integer.parseInt(start);
				if(startInt>=0&&startInt<=24){
					
				}else{
					out.println("<div align='center'>start must be a number between 0 and 24</div>");
					return;
				}
				session.setAttribute("start", startInt);
			
			
		}catch(Exception e){
			out.println("<div align='center'>start must be a number</div>");
			return;
		}
		try{
			
			int temp=Integer.parseInt(end);
			if(temp>=0&&temp<=24 && temp > startInt){
				session.setAttribute("end", temp);
			}else{
				out.println("<div align='center'>end must be a number between 0 and 24 and greater than start</div>");
				return;
			}
			
		
		
		}catch(Exception e){
			out.println("<div align='center'>end must be a number</div>");
			return;
		}
		
		
		try{
			
			int temp=Integer.parseInt(cost);
			session.setAttribute("cost", temp);
		
		
		}catch(Exception e){
			out.println("<div align='center'>cost must be a number</div>");
			return;
		}
		
		
		response.sendRedirect("surereserve.jsp");	
		
		
	}
	
	
	%>>

</body>
</html>