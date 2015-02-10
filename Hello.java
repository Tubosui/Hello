import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class Main extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    if (req.getRequestURI().endsWith("/db")) {
      showDatabase(req,resp);
    } else {
      showHome(req,resp);
    }
  }

  private void showHome(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    resp.getWriter().print("Hello from Java!");
    resp.getWriter().print(getPokemonName(127));
    resp.getWriter().print(poekmon.getget());
  }

  private void showDatabase(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Connection connection = null;
    try {
      connection = getConnection();

      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      String out = "Hello!\n";
      while (rs.next()) {
          out += "Read from DB: " + rs.getTimestamp("tick") + "\n";
      }

      resp.getWriter().print(out);
    } catch (Exception e) {
      resp.getWriter().print("There was an error: " + e.getMessage());
    } finally {
      if (connection != null) try{connection.close();} catch(SQLException e){}
    }
  }

  private Connection getConnection() throws URISyntaxException, SQLException {
    URI dbUri = new URI(System.getenv("DATABASE_URL"));

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    int port = dbUri.getPort();

    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + dbUri.getPath();

    return DriverManager.getConnection(dbUrl, username, password);
  }
  public String getPokemonName(int i){
    Connection cn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String ans = "";
    try {
      Class.forName("com.mysql.jdbc.Driver");
      cn = DriverManager.getConnection("jdbc:mysql://localhost/pokemon_DB","root","");
      stmt = cn.createStatement();
      rs =stmt.executeQuery("select * from kisodata where id = "+i);
      rs.next();
      ans = rs.getString("Name");
    } catch (Exception e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
      ans = "error";
    }finally{
      try {
        rs.close();
        stmt.close();
        cn.close();
      } catch (SQLException e) {
        // TODO 自動生成された catch ブロック
        e.printStackTrace();
      }
    }
    return ans;

  }

  public static void main(String[] args) throws Exception {
    Server server = new Server(Integer.valueOf(System.getenv("PORT")));
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);
    context.addServlet(new ServletHolder(new Main()),"/*");
    server.start();
    server.join();
  }
}
