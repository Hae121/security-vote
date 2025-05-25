import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import com.google.gson.Gson;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Map<String, Integer> results = ResultAggregator.countVotes();

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        Gson gson = new Gson();
        String json = gson.toJson(results);
        out.print(json);
    }
}
