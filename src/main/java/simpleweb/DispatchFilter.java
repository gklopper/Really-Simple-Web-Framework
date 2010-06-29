package simpleweb;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public abstract class DispatchFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(DispatchFilter.class.getName());

    private final List<Dispatcher> dispatchers = new ArrayList<Dispatcher>();

    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.severe("init");
        dispatchers.clear();
        configueControllers();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getServletPath();
        LOGGER.severe("Path: " + path);
        for (Dispatcher dispatcher : dispatchers) {
            if (dispatcher.matches(request.getMethod(), path)) {
                LOGGER.severe("matched");
                dispatcher.dispatch(request, response);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }

    public abstract void configueControllers();

    void addDispatcher(Dispatcher dispatcher) {
        dispatchers.add(dispatcher);    
    }

    protected DispatcherChainBuilder GET(String path) {
        return new DispatcherChainBuilder("GET", path, this);
    }

    protected DispatcherChainBuilder POST(String path) {
        return new DispatcherChainBuilder("POST", path, this);
    }
}
