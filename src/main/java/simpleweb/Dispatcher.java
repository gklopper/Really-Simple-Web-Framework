package simpleweb;

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

    private ParamMapper mapper = new ParamMapper();

    private String httpMethod;
    private String path;
    private Controller controller;
    private Method method;
    private Pattern pathPattern;
    private List<String> urlParameters = new ArrayList<String>();

    //private static final Pattern PARAM_PATTERN = Pattern.compile(".*(\\{.*\\}).*");

    public Dispatcher(String httpMethod, String path, Controller controller, String methodName){
        this.httpMethod = httpMethod;
        this.path = path;
        this.controller = controller;

        for (Method m : controller.getClass().getMethods()){

            //TODO think about overloaded methods

            if (m.getName().equals(methodName) && m.getReturnType().equals(String.class)) {
                this.method = m;
            }
        }

        if (this.method == null) {
            throw new IllegalStateException("Method " + methodName + " (MUST return a String) not found in class " + controller.getClass().getName());
        }

        String regex = path.replace("*", ".*").replaceAll("\\{[a-zA-Z0-9]*\\}", "(.*)");
        pathPattern = Pattern.compile(regex);

        Matcher pathMatcher = pathPattern.matcher(path);
        pathMatcher.find();
        for (int x = 1; x <= pathMatcher.groupCount(); x++) {
            urlParameters.add(pathMatcher.group(x).replace("{", "").replace("}", ""));
        }
    }

    public Dispatcher withConverter(Class clazz, Converter converter) {
        mapper.addConverter(clazz, converter);
        return this;
    }

    boolean matches(String httpMethod, String path) {
        return httpMethod.equals(this.httpMethod) && pathPattern.matcher(path).matches();
    }

    void dispatch(HttpServletRequest request, HttpServletResponse response) {
        try {

            controller.initForThread(request, response);

            Map paramMap = new HashMap(request.getParameterMap());

            if (hasUrlParams()) {
                Matcher pathMatcher = pathPattern.matcher(request.getServletPath());
                pathMatcher.find();
                for (int x = 1; x <= pathMatcher.groupCount(); x++) {
                    paramMap.put(urlParameters.get(x - 1), pathMatcher.group(x));
                }
            }

            Object[] params = mapper.mapParams(paramMap, method);

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
            response.sendError(Integer.parseInt(view.replace("error:", "")));
        }
    }

    boolean hasUrlParams() {
        return !urlParameters.isEmpty();
    }

    public List<String> getUrlParameters() {
        return urlParameters;
    }
}
