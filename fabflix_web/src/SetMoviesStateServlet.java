import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.HttpURLConnection;

@WebServlet(name = "SetMoviesStateServlet", urlPatterns = "/api/moviesPageState")
public class SetMoviesStateServlet extends HttpServlet {
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String uri = request.getParameter("uri");
        session.setAttribute("moviesPageState", uri);
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("uri", uri);
        response.getWriter().write(responseJsonObject.toString());
        response.getWriter().close();
        response.setStatus(HttpURLConnection.HTTP_OK);
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String uri = session.getAttribute("moviesPageState") != null ?
                session.getAttribute("moviesPageState").toString() : "";
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("uri", uri);
        response.getWriter().write(responseJsonObject.toString());
        response.getWriter().close();
        response.setStatus(HttpURLConnection.HTTP_OK);
    }
}