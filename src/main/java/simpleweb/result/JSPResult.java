package simpleweb.result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JSPResult implements Result {
    @Override
    public boolean canHandle(String result) {
        return result.endsWith(".jsp");
    }

    @Override
    public void handle(String result, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/views/" + result).forward(request, response);
    }
}