<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="styles/styles.css">
    <title>Login</title>
</head>
<body>
<div class="login-container">
    <form class="login-form" action="<%=request.getContextPath()%>/login" method="post">
        <h1> API Key Secuirty Platform </h1>
        <h3>All your keys in one secure location</h3>

        <% String loginStatus = (String) request.getAttribute("loginStatus");
           if (loginStatus != null && loginStatus.equals("Failed")) {   %>
               <p> Login Failed. Please retry!!</p>
        <% } %>

        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>

        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>

        <button type="submit">Login</button>
    </form>
</div>
</body>
</html>


