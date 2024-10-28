<%@ page import="ia.model.UserBean" %>
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
<div class="section">
  <h3>Azure Credentials</h3>
  <form action="SaveAzureCredentialsServlet" method="post">
    <label for="clientId">Client ID:</label>
    <input type="text" id="clientId" name="clientId" required><br>

    <label for="clientSecret">Client Secret:</label>
    <input type="password" id="clientSecret" name="clientSecret" required><br>

    <label for="tenantId">Tenant ID:</label>
    <input type="text" id="tenantId" name="tenantId" required><br>

    <button type="submit">Save Credentials</button>
  </form>
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
</div>



</body>
</html>
