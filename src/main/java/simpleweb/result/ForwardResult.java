package simpleweb.result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ForwardResult implements Result {
    @Override
    public boolean canHandle(String result) {
        return result.startsWith("forward:");
    }

    @Override
    public void handle(String result, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher(result.replace("forward:", "")).forward(request, response);
    }
}