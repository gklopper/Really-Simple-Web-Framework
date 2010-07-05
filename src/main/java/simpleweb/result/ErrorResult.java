package simpleweb.result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorResult implements Result {
    @Override
    public boolean canHandle(String result) {
        return result.startsWith("error:");
    }

    @Override
    public void handle(String result, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String errorString = result.replace("error:", "");
        if (errorString.contains(":")) {
            // e.g.  404:No data found
            int code = Integer.parseInt(errorString.substring(0, errorString.indexOf(":")));
            String message = errorString.substring(errorString.indexOf(":"));
            response.sendError(code, message);
        } else {
            // e.g.  404
            response.sendError(Integer.parseInt(errorString));
        }
    }
}