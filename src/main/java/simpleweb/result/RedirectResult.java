package simpleweb.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectResult implements Result {
    @Override
    public boolean canHandle(String result) {
        return result.startsWith("redirect:");
    }

    @Override
    public void handle(String result, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(result.replace("redirect:", ""));
    }
}
