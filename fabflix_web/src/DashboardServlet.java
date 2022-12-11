import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.sql.*;
import javax.sql.DataSource;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/_dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 11L;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //get metadata
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet rs = databaseMetaData.getTables("moviedb",null,null,new String[]{"TABLE"});
            JsonArray jsonArray = new JsonArray();
            while(rs.next()) {
                String table_name = rs.getString("TABLE_NAME");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table_name", table_name);

                String query = "SHOW COLUMNS FROM " + table_name;
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet columnRs = statement.executeQuery();
                JsonArray columnsArray = new JsonArray();
                while(columnRs.next()) {
                    JsonObject columnsObj = new JsonObject();
                    columnsObj.addProperty("Field", columnRs.getString("Field"));
                    columnsObj.addProperty("Type", columnRs.getString("Type"));
                    columnsArray.add(columnsObj);
                }
                jsonObject.add("attributes", columnsArray);
                statement.close();
                columnRs.close();
                jsonArray.add(jsonObject);
            }
            rs.close();
            out.write(jsonArray.toString());
            response.setStatus(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            request.getServletContext().log("Error:", e);
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            out.close();
        }
    }
}