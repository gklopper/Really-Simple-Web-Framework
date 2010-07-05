package simpleweb;

import org.apache.log4j.Logger;
import simpleweb.converter.ComplexConverter;
import simpleweb.converter.SimpleConverter;
import simpleweb.result.*;

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
    private List<Result> results = new ResultListWithDefaults();

    public Dispatcher(String httpMethod, String path, Controller controller, String methodName){
       this.httpMethod = httpMethod;
        this.path = path;
        this.controller = controller;
        this.method = toMethod(methodName);
        this.pathPattern  = setupPathRegex(path);
        extractUrlParameterNames(path);
    }

    public Dispatcher withResult(Result result) {
        results.add(result);
        return this;
    }

    public Dispatcher withConverter(Class clazz, SimpleConverter converter) {
        mapper.addConverter(clazz, converter);
        return this;
    }

    public Dispatcher withConverter(Class clazz, ComplexConverter converter) {
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
            Map urlParamMap = parseUrlParams(request);
            Object[] params = mapper.mapParams(request, urlParamMap, method);
            String view = (String) method.invoke(controller, params);
            mapModelToRequest(request);
            processResult(request, response, view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            controller.destroyForThread();
        }
    }

    private Map parseUrlParams(HttpServletRequest request) {
        Map urlParamMap = new HashMap();

        if (hasUrlParams()) {
            Matcher pathMatcher = pathPattern.matcher(request.getRequestURI());
            pathMatcher.find();
            for (int x = 1; x <= pathMatcher.groupCount(); x++) {
                urlParamMap.put(urlParameters.get(x - 1), pathMatcher.group(x));
            }
        }
        return urlParamMap;
    }

    private void mapModelToRequest(HttpServletRequest request) {
        Map<String, Object> model = controller.getModel();
        for (String key : model.keySet()) {
            request.setAttribute(key, model.get(key));
        }
    }

    private void processResult(HttpServletRequest request, HttpServletResponse response, String view) throws IOException, ServletException {
        for (Result result : results) {
            if (result.canHandle(view)) {
                result.handle(view, request, response);
                return;
            }
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
        LOGGER.info("Method: " + method);
        return method;
    }

    private Pattern setupPathRegex(String path) {
        String regex = path.replace("*", ".*").replaceAll("\\{[a-zA-Z0-9]*\\}", "(.*)");
        LOGGER.info("Path: " + path + " = Regex: " + regex);
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

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    private class ResultListWithDefaults extends ArrayList<Result>{
        private ResultListWithDefaults() {
            add(new RedirectResult());
            add(new ForwardResult());
            add(new ErrorResult());
            add(new JSPResult());
        }
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
