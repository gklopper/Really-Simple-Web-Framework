package simpleweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller {

    private final ThreadLocal<Map<String, Object>> model = new ThreadLocal<Map<String, Object>>();
    private final ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
    private final ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();

    final void initForThread(HttpServletRequest request, HttpServletResponse response) {
        model.set(new HashMap<String, Object>());
        this.request.set(request);
        this.response.set(response);
    }

    final void destroyForThread() {
        model.remove();
        request.remove();
        response.remove();
    }

    Map<String, Object> getModel() {
        return model.get();
    }

    protected void cachePublicFor(int seconds) {
        getResponse().setHeader("Cache-Control", String.format("public, max-age=%s", seconds));
    }

    protected void addToModel(String name, Object value) {
        model.get().put(name, value);
    }

    protected HttpServletRequest getRequest() {
        return request.get();
    }

    protected HttpServletResponse getResponse() {
        return response.get();
    }

    protected String error(int errorCode) {
        return "error:" + errorCode;
    }

    protected String error(int errorCode, String message) {
        return error(errorCode) + ":" + message;
    }

    protected String redirect(String url) {
        return "redirect:" + url;
    }

    protected String forward(String url) {
        return "forward:" + url;
    }
}
