<%@ page language="java" import="cs5530.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>register</title>
</head>
<body>
<div class="jumbotron">
			<div align="center">	
				<h1>Register a new Account</h1>
			</div>
		</div>
<div class="container" style="padding-top: 100px;">
			<div class="row">
			<div align="center">
					<form action="register.jsp">
						type the username:
						<input class="form-control" type=text name="login" ><br/>
					    type your full name:
						<input class="form-control" type=text name="name"><br/>
						Password:
						<input  class="form-control"  type=password name="password"><br/>
						comfirm your password:
						<input  class="form-control"  type=password name="repassword"><br/>
						<div class="radio">
						<label>
  					 	
      						<input type="radio" name="type" id="user" 
         									value="user" checked> user
   								</label>
    						<label>
      						<input type="radio" name="type" id="driver" 
         							value="driver"> driver
   							</label>
						</div>
						Address
						<input  class="form-control"  type=text name="Address"><br/>
						city
						<input  class="form-control"  type=text name="city"><br/>
						state
						<input  class="form-control"  type=text name="state"><br/>
						Phone number
						<input class="form-control"  type=text name="phone" placeholder="1234567891"><br/>
						<input class="form-control"  class = "btn btn-primary btn-lg btn-block" type="submit" name="submit" value = "Register">
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
		String login = request.getParameter("login");
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String repassword = request.getParameter("repassword");
		String type = request.getParameter("type");
		String address = request.getParameter("Address");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		String phone = request.getParameter("phone");
		if(login!=null&&name!=null&&password!=null&&repassword!=null&&type!=null&&address!=null&&city!=null&&state!=null&&phone!=null){
			if(!password.equals(repassword)){
				out.println("<div align='center'>password not set up right, check and retry</div>");
				return;
			}else{
				try{
					DBApi api=new DBApi();
					if(api.isDriverUser(conn.stmt, login) || api.isUberUser(conn.stmt, login)){
						out.println("<div align='center'>That username is taken</div>");
						return;
					}
					
					if(api.enrollUser(conn.stmt,login,name,address,city,state,phone,password,type)) {
						out.println("<div align='center'>successful registeration</div>");
						response.sendRedirect("login.jsp");
						return;
					}else{
						out.println("<div align='center'>register not successful, please check your information</div>");
						return;
					}
				}catch(Exception e){
					out.println("<div align='center'>register not successful, please check your information</div>");
					return;
				}
			}
		
			
		}
		
		
		
		
		
		
		%>
</body>
</html>