package ia.controller;
import ia.model.DatabaseUtility;
import ia.model.LoginDAO;
import ia.model.UserBean;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/SaveAzureCredentialsServlet")
public class SaveAzureCredentialsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = getUserIdFromSession(request); // Get user ID from session
        System.out.println(userId + "JUST TESTING ID");

        String clientId = request.getParameter("clientId");
        String clientSecret = request.getParameter("clientSecret");
        String tenantId = request.getParameter("tenantId");

        // Save the credentials to the database

        try {
            DatabaseUtility.saveAzureCredentials(userId, clientId, clientSecret, tenantId);
            Cookie[] cookies = request.getCookies(); // Retrieve all cookies from the request
            String email = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("username")) { // Check for the cookie named "user"
                        email = cookie.getValue(); // Retrieve the value of the cookie
                        // Use the username as needed
                        System.out.println("Username from cookie: " + email);
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
            request.setAttribute("UserBean", user);

            RequestDispatcher dispatcher = request.getRequestDispatcher("profile.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


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
