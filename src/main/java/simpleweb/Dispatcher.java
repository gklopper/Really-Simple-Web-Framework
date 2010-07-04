package simpleweb;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dispatcher {

    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class);

    private ParamMapper mapper = new ParamMapper();

    private String httpMethod;
    private String path;
    private Controller controller;
    private Method method;
    private Pattern pathPattern;
    private List<String> urlParameters = new ArrayList<String>();

    public Dispatcher(String httpMethod, String path, Controller controller, String methodName){
       this.httpMethod = httpMethod;
        this.path = path;
        this.controller = controller;
        this.method = toMethod(methodName);
        this.pathPattern  = setupPathRegex(path);
        extractUrlParameterNames(path);
    }

    public Dispatcher withConverter(Class clazz, SimpleConverter converter) {
        mapper.addConverter(clazz, converter);
        return this;
    }

    boolean matches(String httpMethod, String path) {
        LOGGER.info("Http method: " + httpMethod + " Path: " + path);
        return httpMethod.equals(this.httpMethod) && pathPattern.matcher(path).matches();
    }

    void dispatch(HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("Handling: " + toString());

        try {

            controller.initForThread(request, response);

            Map urlParamMap = new HashMap();

            if (hasUrlParams()) {
                parseUrlParams(request, urlParamMap);
            }

            Object[] params = mapper.mapParams(request, urlParamMap, method);

            String view = (String) method.invoke(controller, params);

            Map<String, Object> model = controller.getModel();

            for (String key : model.keySet()) {
                request.setAttribute(key, model.get(key));
            }

            processView(request, response, view);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            controller.destroyForThread();
        }
    }

    private void parseUrlParams(HttpServletRequest request, Map paramMap) {
        Matcher pathMatcher = pathPattern.matcher(request.getRequestURI());
        pathMatcher.find();
        for (int x = 1; x <= pathMatcher.groupCount(); x++) {
            paramMap.put(urlParameters.get(x - 1), pathMatcher.group(x));
        }
    }

    private void processView(HttpServletRequest request, HttpServletResponse response, String view) throws IOException, ServletException {
        if (view.startsWith("redirect:")) {
            // e.g.  redirect:/admin/list
            response.sendRedirect(view.replace("redirect:", ""));
        } else if (view.startsWith("forward:")) {
            // e.g.  forward:/admin/list
            request.getRequestDispatcher(view.replace("forward:", "")).forward(request, response);
        } else if (view.startsWith("error:")) {
            // e.g.  error:404
            // e.g.  error:404:No data found
            processErrorMessage(response, view);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/" + view).forward(request, response);
        }
    }

    private void processErrorMessage(HttpServletResponse response, String view) throws IOException {
        String errorString = view.replace("error:", "");

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

    private Method toMethod(String methodName) {

        Method method = null;
        for (Method m : this.controller.getClass().getMethods()){

            //TODO think about overloaded methods

            if (m.getName().equals(methodName) && m.getReturnType().equals(String.class)) {
                method = m;
            }
        }

        if (method == null) {
            throw new IllegalStateException("Method " + methodName + " (MUST return a String) not found in class " + this.controller.getClass().getName());
        }

        return method;
    }

    private Pattern setupPathRegex(String path) {
        String regex = path.replace("*", ".*").replaceAll("\\{[a-zA-Z0-9]*\\}", "(.*)");
        return Pattern.compile(regex);
    }

    private void extractUrlParameterNames(String path) {
        Matcher pathMatcher = pathPattern.matcher(path);
        pathMatcher.find();
        for (int x = 1; x <= pathMatcher.groupCount(); x++) {
            urlParameters.add(pathMatcher.group(x).replace("{", "").replace("}", ""));
        }
    }

    boolean hasUrlParams() {
        return !urlParameters.isEmpty();
    }

    public List<String> getUrlParameters() {
        return urlParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dispatcher that = (Dispatcher) o;

        if (controller != null ? !controller.equals(that.controller) : that.controller != null) return false;
        if (httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null) return false;
        if (mapper != null ? !mapper.equals(that.mapper) : that.mapper != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mapper != null ? mapper.hashCode() : 0;
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Dispatcher{" +
                "httpMethod='" + httpMethod + '\'' +
                ", path='" + path + '\'' +
                ", method=" + method +
                ", pathPattern=" + pathPattern +
                ", urlParameters=" + urlParameters +
                ", mapper=" + mapper +
                ", controller=" + controller +
                '}';
    }
}
