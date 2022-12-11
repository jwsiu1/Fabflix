import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/addToCart")
public class AddToCartServlet extends HttpServlet {
    private static final long serialVersionUID = 10L;
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
        HttpSession session = request.getSession();
        String productId = request.getParameter("id");
//        String user = (String) session.getAttribute("user");
        String currCart = session.getAttribute("cart") == null ? "{}" : (String) session.getAttribute("cart");
        JsonParser parser = new JsonParser();
        JsonObject cart = parser.parse(currCart).getAsJsonObject();
        int currQuantity = cart.get(productId) == null ? 0 : cart.get(productId).getAsInt();
        cart.addProperty(productId, currQuantity+1);
        session.setAttribute("cart", cart.toString());
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("cart", cart.toString());
        response.getWriter().write(responseJsonObject.toString());
        response.getWriter().close();
        response.setStatus(HttpURLConnection.HTTP_OK);
    }
}