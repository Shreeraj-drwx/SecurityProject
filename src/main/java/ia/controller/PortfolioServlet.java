package ia.controller;

import ia.model.ApiKey;
import ia.model.LoginDAO;
import ia.model.UserBean;
import ia.spm.EncryptionUtil;
import ia.spm.MysqlConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/PortfolioServlet")
public class PortfolioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = getUserIdFromSession(request); // Get user ID from session

        // Get API keys for the user and forward to JSP for rendering
        List<ApiKey> apiKeys = getApiKeys(userId);
        request.setAttribute("apiKeys", apiKeys);

        // Forward the request to the JSP page for rendering
        RequestDispatcher dispatcher = request.getRequestDispatcher("portfolio.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int userId = getUserIdFromSession(request);

        if (action != null) {
            switch (action) {
                case "addApiKey":
                    try {
                        addApiKey(request, response, userId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "deleteApiKey":
                    try {
                        deleteApiKey(request, response, userId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "fetchRecommendations":
                    List<String> recommendations = fetchSecurityRecommendations(userId);
                    System.out.println("Security Recommendations:");
                    for (String recommendation : recommendations) {
                        System.out.println(recommendation);
                    }
                    request.setAttribute("recommendations", recommendations);
                    doGet(request, response); // Refresh page with recommendations
                    break;
                default:
                    response.sendRedirect("PortfolioServlet");
            }
        } else {
            doGet(request, response);
        }
    }

    // New method to fetch security recommendations for a user's instances
    private List<String> fetchSecurityRecommendations(int userId) {
        List<String> recommendations = new ArrayList<>();
        List<ApiKey> apiKeys = getApiKeys(userId); // Get all API keys for the user

        // Fetch security recommendations for each API key
        for (ApiKey apiKey : apiKeys) {
            String provider = apiKey.getProvider();
            String key = apiKey.getKey();
            String subscriptionId = apiKey.getSubscriptionId();

            // Example fetching security recommendations for Azure using HTTP request
            if ("Azure".equalsIgnoreCase(provider)) {
                String url = "https://management.azure.com/subscriptions/" + subscriptionId +
                        "/resourceGroups/test1_group/providers/Microsoft.Compute/virtualMachines?api-version=2024-07-01";
                String token = key; // Assuming the API key is the token for Azure

                try {
                    // Make an HTTP GET request to fetch recommendations
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Read the response
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Parse the JSON response
                        formatJsonResponse(response.toString(), recommendations);
                    } else {
                        // Handle errors
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                        recommendations.add("Failed to fetch recommendations for " + provider + ". Response code: " + responseCode + ". Error: " + errorResponse.toString());
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    recommendations.add("Error fetching recommendations: " + e.getMessage());
                }
            }
        }
        return recommendations;
    }

    // New method to parse and format the JSON response
    private void formatJsonResponse(String jsonResponse, List<String> recommendations) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray valueArray = jsonObject.optJSONArray("value");

            if (valueArray != null) {
                for (int i = 0; i < Math.min(5, valueArray.length()); i++) {
                    JSONObject vmObject = valueArray.getJSONObject(i);

                    // Extract top-level attributes
                    String name = vmObject.optString("name", "N/A");
                    String id = vmObject.optString("id", "N/A");
                    String type = vmObject.optString("type", "N/A");
                    String location = vmObject.optString("location", "N/A");

                    // Extract nested attributes
                    JSONObject properties = vmObject.optJSONObject("properties");
                    String vmSize = properties != null && properties.optJSONObject("hardwareProfile") != null
                            ? properties.getJSONObject("hardwareProfile").optString("vmSize", "N/A") : "N/A"; // New attribute

                    String osType = properties != null && properties.optJSONObject("storageProfile") != null
                            ? properties.getJSONObject("storageProfile").optJSONObject("osDisk").optString("osType", "N/A") : "N/A"; // New attribute

                    String offer = properties != null && properties.optJSONObject("storageProfile") != null
                            ? properties.getJSONObject("storageProfile").optJSONObject("imageReference").optString("offer", "N/A") : "N/A"; // New attribute

                    // Format the output with each attribute on a new line
                    String formatted = String.format("Name: %s\nID: %s\nType: %s\nLocation: %s\nVM Size: %s\nOS Type: %s\nOffer: %s\n",
                            name, id, type, location, vmSize, osType, offer);
                    recommendations.add(formatted);
                }
            } else {
                recommendations.add("No virtual machines found.");
            }
        } catch (Exception e) {
            recommendations.add("Error parsing JSON response: " + e.getMessage());
        }
    }

    private void addApiKey(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {
        String apiProvider = request.getParameter("apiProvider");
        String apiKey = request.getParameter("apiKey");
        String subscriptionId = request.getParameter("subscription_id");

        String secretKey = "1234123412341234"; // Secret key for encryption
        String encryptedApiKey = EncryptionUtil.encrypt(apiKey, secretKey);

        Connection connection = MysqlConnection.openConnection();
        String sql = "INSERT INTO api_keys (user_id, api_key, api_provider, subscription_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, encryptedApiKey);
            ps.setString(3, apiProvider);
            ps.setString(4, subscriptionId);
            ps.executeUpdate();
        } finally {
            connection.close();
        }

        response.sendRedirect("PortfolioServlet");
    }

    private void deleteApiKey(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {
        String apiKeyToDelete = request.getParameter("apiKeyToDelete");
        String secretKey = "1234123412341234"; // Same secret key used for encryption
        String encryptedApiKey = EncryptionUtil.encrypt(apiKeyToDelete, secretKey);

        Connection connection = MysqlConnection.openConnection();
        String sql = "DELETE FROM api_keys WHERE user_id = ? AND api_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, encryptedApiKey);
            ps.executeUpdate();
        } finally {
            connection.close();
        }

        response.sendRedirect("PortfolioServlet");
    }

    private List<ApiKey> getApiKeys(int userId) {
        List<ApiKey> apiKeys = new ArrayList<>();
        try (Connection connection = MysqlConnection.openConnection()) {
            String sql = "SELECT api_provider, api_key, subscription_id FROM api_keys WHERE user_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String provider = rs.getString("api_provider");
                        String encryptedKey = rs.getString("api_key");
                        String subscriptionId = rs.getString("subscription_id");
                        String secretKey = "1234123412341234"; // Secret key used for encryption
                        String decryptedKey = EncryptionUtil.decrypt(encryptedKey, secretKey);
                        apiKeys.add(new ApiKey(provider, decryptedKey, subscriptionId));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return apiKeys;
    }

    private int getUserIdFromSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String email = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    email = cookie.getValue();
                    break;
                }
            }
        }

        UserBean user;
        try {
            user = LoginDAO.identifier(email);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return user.getId();
    }
}

