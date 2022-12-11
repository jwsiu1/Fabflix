import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getParameter("g-recaptcha-response") != null) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
        }
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM customers WHERE customers.email=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            JsonObject responseJsonObject = new JsonObject();
            String targetEmail = "";
            String targetPassword = "";
            if (rs.next()) {
                targetEmail = rs.getString("email");
                targetPassword = rs.getString(("password"));
                String uid = rs.getString("id");
//                if (email.equals(targetEmail) && password.equals(targetPassword)) {
                boolean success = new StrongPasswordEncryptor().checkPassword(password, targetPassword);
                if(email.equals(targetEmail) && success) {
                    session.setAttribute("user", email);
                    session.setAttribute("uid", uid);
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    responseJsonObject.addProperty("status", "fail");
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "Invalid password");
                }
            } else {
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "Sorry, we can't find an account with this " +
                        "email address. Please try again or create a new account.");
            }
            response.getWriter().write(responseJsonObject.toString());
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            response.getWriter().close();
        }
    }
}