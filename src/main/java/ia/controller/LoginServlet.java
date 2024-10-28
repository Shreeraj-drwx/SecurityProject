package ia.controller;

import ia.model.LoginDAO;
import ia.model.UserBean;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private LoginDAO loginDAO;

    public void init() {
        loginDAO = new LoginDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String hashedPassword = new LoginDAO().hashPassword(password);
        UserBean UserBean = new UserBean();
        UserBean.setEmail(username);
        UserBean.setPassword(hashedPassword);


        try {
            if (loginDAO.validate(UserBean)) {
                HttpSession session = request.getSession();
                session.setAttribute("user",username);
                session.setMaxInactiveInterval(30*60);
                Cookie loginCookie = new Cookie("username",username);
                loginCookie.setMaxAge(30*60);
                loginCookie.setSecure(true); // Ensures the cookie is sent over HTTPS only
                loginCookie.setHttpOnly(true); // Recommended to prevent JavaScript access
                response.addCookie(loginCookie);

                RequestDispatcher dispatcher = request.getRequestDispatcher("/PortfolioServlet");
                dispatcher.forward(request, response);
            } else {
                request.setAttribute("loginStatus", "Failed");
                RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                dispatcher.forward(request, response);
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

}