import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.HttpURLConnection;

@WebServlet(name = "ChangeCartQuantityServlet", urlPatterns = "/api/changeQuantity")
public class ChangeCartQuantityServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String productId = request.getParameter("id");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String currCart = session.getAttribute("cart") == null ? "{}" : (String) session.getAttribute("cart");
        JsonParser parser = new JsonParser();
        JsonObject cart = parser.parse(currCart).getAsJsonObject();
        cart.addProperty(productId, quantity);
        session.setAttribute("cart", cart.toString());
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("cart", cart.toString());
        response.getWriter().write(responseJsonObject.toString());
        response.getWriter().close();
        response.setStatus(HttpURLConnection.HTTP_OK);
    }
}