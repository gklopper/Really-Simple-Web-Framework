package simpleweb;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public abstract class DispatchFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(DispatchFilter.class);

    private final List<Dispatcher> dispatchers = new ArrayList<Dispatcher>();

    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Initialising config from : " + getClass().getName());
        dispatchers.clear();
        configueControllers();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();
        LOGGER.debug("Matching path: " + path);
        for (Dispatcher dispatcher : dispatchers) {
            if (dispatcher.matches(request.getMethod(), path)) {
                dispatcher.dispatch(request, response);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        //no implementation
    }

    public abstract void configueControllers();

    protected DispatcherBuilder GET(String path) {
        return new DispatcherBuilder("GET", path, this);
    }

    protected DispatcherBuilder POST(String path) {
        return new DispatcherBuilder("POST", path, this);
    }

    public List<Dispatcher> getDispatchers() {
        return Collections.unmodifiableList(dispatchers);
    }

    void addDispatcher(Dispatcher dispatcher) {
        LOGGER.info("Adding dispatcher: " + dispatcher.toString());
        dispatchers.add(dispatcher);    
    }

    protected class DispatcherBuilder {

        private String httpMethod;
        private String path;
        private String method;


        DispatcherBuilder(String httpMethod, String path, DispatchFilter dispatchFilter) {
            this.httpMethod = httpMethod;
            this.path = path;
        }

        public DispatcherBuilder invokes(String method) {
            this.method = method;
            return this;
        }


        public Dispatcher on(Controller controller) {
            Dispatcher dispatcher = new Dispatcher(httpMethod, path, controller, method);
            dispatchers.add(dispatcher);
            return dispatcher;
        }
    }
}
