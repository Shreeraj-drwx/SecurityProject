<%@ page import="ia.spm.UserBean" %>
<%@ page import="ia.spm.UploadHistory" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="styles/styles2.css">
  <title>Profile</title>
</head>
<body>
<%
  String userName = null;
  String sessionID = null;
  Cookie[] cookies = request.getCookies();
  if(cookies !=null){
    for(Cookie cookie : cookies){
      if(cookie.getName().equals("username")) userName = cookie.getValue();
      if(cookie.getName().equals("JSESSIONID")) sessionID = cookie.getValue();
    }
  }

  UserBean user = (UserBean) request.getAttribute("UserBean");
%>
<div class="header">
  <h1><%=user.getFirstName() + "'s"%> Profile</h1>
</div>
<br>

<div class="section" >
<p>First Name: <%=user.getFirstName()%></p>
<p>Last Name: <%=user.getLastName()%></p>
<p>Email: <%=user.getEmail()%></p>
</div>

<div class="button-container">
  <div class="top-right-buttons">
    <form action="PortfolioServlet" method="post">
      <button type="submit" >Back to Portfolio</button>
    </form>
    <br>
    <br>
    <form action="LogoutServlet" method="post">
      <button type="submit" >Logout</button>
    </form>
  </div>
  <div class="button-container2">
    <p> Enter your new assets here (CSV Files only)</p>
    <form method="post" action="fileuploadservlet" enctype="multipart/form-data">
      <input type="file" name="file" id="file" />
      <input type="submit" value="Upload" />
    </form>
  </div>
</div>

<%
  List<UploadHistory> uploadList = UploadHistory.userUploadHistory(user.getId());
%>
<div class="section section1">

  <h2>Your Upload History:</h2>
  <table>
    <thead>
    <tr>
      <th>Type</th>
      <th>Company Name</th>
    </tr>
    </thead>
    <tbody>
    <%

      if ( uploadList != null) {
        for (UploadHistory entry : uploadList) {
    %>
    <tr>
      <td><%= entry.getUploadDate() %></td>
      <td><%= entry.getFileName() %></td>
    </tr>
    <%
        }
      }
    %>
    </tbody>
  </table>
</div>

</body>
</html>
