package simpleweb.result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Result {

    public boolean canHandle(String result);
    public void handle(String result, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

}
