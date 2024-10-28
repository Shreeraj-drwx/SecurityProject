<%@ page import="java.util.List" %>
<%@ page import="ia.model.ApiKey" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="styles/styles2.css">
    <title>Portfolio</title>
</head>
<body>
<%
    // Allow access only if session exists
    String user = null;
    if(session.getAttribute("user") == null){
        response.sendRedirect("index.jsp");
    }else user = (String) session.getAttribute("user");
    String userName = null;
    String sessionID = null;
    Cookie[] cookies = request.getCookies();
    if(cookies != null){
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("user")) userName = cookie.getValue();
            if(cookie.getName().equals("JSESSIONID")) sessionID = cookie.getValue();
        }
    }
%>
<div class="header"><h1>Your API view</h1></div>
<div class="header">

    <h2>Manage Your API Keys</h2>

    <!-- Form to add a new API key -->
    <form action="PortfolioServlet" method="POST">
        <label for="apiProvider">API Provider:</label>
        <select name="apiProvider" id="apiProvider" required>
            <option value="AWS">AWS</option>
            <option value="Azure">Azure</option>
            <option value="GoogleCloud">Google Cloud</option>
        </select>

        <!-- Remove the API Key field for Azure or make it optional -->
        <div id="apiKeyDiv">
            <label for="apiKey">API Key:</label>
            <input type="text" name="apiKey" id="apiKey">
        </div>

        <label for="subscription_id">Subscription ID:</label>
        <input type="text" name="subscription_id" id="subscription_id" required>
        <input type="hidden" name="action" value="addApiKey">
        <button type="submit">Add API Key</button>
    </form>

    <script>
        // Optionally, add JavaScript to disable the API Key field for Azure
        document.getElementById('apiProvider').addEventListener('change', function () {
            const apiKeyDiv = document.getElementById('apiKeyDiv');
            if (this.value === 'Azure') {
                apiKeyDiv.style.display = 'none';
            } else {
                apiKeyDiv.style.display = 'block';
            }
        });
    </script>


    <h3>Your Current API Keys</h3>

    <!-- Form to delete an API key -->
    <form action="PortfolioServlet" method="POST">
        <label for="deleteApiKey">API Key to Delete:</label>
        <select name="apiKeyToDelete" id="deleteApiKey" required>
            <%
                List<ApiKey> apiKeys = (List<ApiKey>) request.getAttribute("apiKeys");
                if (apiKeys != null && !apiKeys.isEmpty()) {
                    for (ApiKey apiKey : apiKeys) {
                        String key = apiKey.getKey();
                        String provider = apiKey.getProvider();
            %>
            <option value="<%= key %>"> (<%= provider %>)</option>
            <%
                }
            } else {
            %>
            <option value="">No API keys available</option>
            <%
                }
            %>
        </select>

        <input type="hidden" name="action" value="deleteApiKey">
        <button type="submit">Delete API Key</button>
    </form>
</div>

<div class="header">
    <h3>Your Security Report</h3>
    <%
        List<String> recommendations = (List<String>) request.getAttribute("recommendations");
        if (recommendations != null && !recommendations.isEmpty()) {
            for (String recommendation : recommendations) {
                // Split the recommendation by line breaks to print each part on a new line
                String[] lines = recommendation.split("\n");
    %>
    <div class="recommendation" style="text-align: left;">
        <%
            for (String line : lines) {
        %>
        <p><%= line %></p>
        <%
            }
        %>
    </div>
    <hr> <!-- Divider between recommendations -->
    <%
        }
    } else {
    %>
    <p>No security recommendations available.</p>
    <%
        }
    %>
    <!-- Form to generate a security report -->
    <form action="PortfolioServlet" method="POST">
        <input type="hidden" name="action" value="fetchRecommendations">
        <button type="submit">Generate Report</button>
    </form>
</div>

<div>
    <div class="top-right-buttons">
        <form action="ProfileServlet" method="get">
            <input type="image" src="images/user.png" alt="Profile" class="submit-image">
        </form>
        <form action="LogoutServlet" method="post">
            <button type="submit">Logout</button>
        </form>
    </div>
    <br>
    <br>
</div>
</body>
</html>
