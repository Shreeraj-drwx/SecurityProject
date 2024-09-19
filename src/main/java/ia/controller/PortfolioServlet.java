package ia.controller;

import ia.model.ApiKey;
import ia.model.LoginDAO;
import ia.model.UserBean;
import ia.spm.EncryptionUtil;
import ia.spm.MysqlConnection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
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
        // Retrieve cookies and get the email from the cookie named "username"

        int userId = getUserIdFromSession(request); // Assuming this is the user ID

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
                    } catch (ClassNotFoundException | SQLException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "deleteApiKey":
                    try {
                        deleteApiKey(request, response, userId);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    response.sendRedirect("PortfolioServlet");
            }
        } else {
            // Handle display when no specific action is provided
            doGet(request, response);
        }
    }

    private void addApiKey(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {
        String apiProvider = request.getParameter("apiProvider");
        String apiKey = request.getParameter("apiKey");

        String secretKey = "1234123412341234"; // Use a securely generated secret key
        String encryptedApiKey = EncryptionUtil.encrypt(apiKey, secretKey);

        Connection connection = MysqlConnection.openConnection();
        String sql = "INSERT INTO api_keys (user_id, api_key, api_provider) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, encryptedApiKey);
            ps.setString(3, apiProvider);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            connection.close();
        }
        // After adding, redirect to PortfolioServlet to refresh API keys list
        response.sendRedirect("PortfolioServlet");
    }

    private void deleteApiKey(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {
        String apiKeyToDelete = request.getParameter("apiKeyToDelete");
        String secretKey = "1234123412341234"; // Use the same secret key as for encryption
        String encryptedApiKey = EncryptionUtil.encrypt(apiKeyToDelete, secretKey);
        Connection connection = MysqlConnection.openConnection();
        String sql = "DELETE FROM api_keys WHERE user_id = ? AND api_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, encryptedApiKey);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }

        // After deleting, redirect to PortfolioServlet to refresh API keys list
        response.sendRedirect("PortfolioServlet");
    }

    private List<ApiKey> getApiKeys(int userId) {
        List<ApiKey> apiKeys = new ArrayList<>();
        try (Connection connection = MysqlConnection.openConnection()) {
            String sql = "SELECT api_provider, api_key FROM api_keys WHERE user_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String provider = rs.getString("api_provider");
                        String encryptedKey = rs.getString("api_key");
                        String secretKey = "1234123412341234"; // Same secret key used for encryption
                        String decryptedKey = EncryptionUtil.decrypt(encryptedKey, secretKey);
                        apiKeys.add(new ApiKey(provider, decryptedKey));
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

    // Utility method to get the logged-in user's ID from session
    private int getUserIdFromSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies(); // Retrieve all cookies from the request
        String email = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username")) { // Check for the cookie named "user"
                    email = cookie.getValue(); // Retrieve the value of the cookie
                    System.out.println("Username from cookie @portfolio: " + email);
                    break; // Exit the loop after finding the cookie
                }
            }
        }
        UserBean user = null;
        try {
            user = LoginDAO.identifier(email);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return user.getId();
    }
}
