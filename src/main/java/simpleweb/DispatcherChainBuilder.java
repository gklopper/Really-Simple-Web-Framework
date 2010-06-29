package simpleweb;

public class DispatcherChainBuilder {

    private String httpMethod;
    private String path;
    private DispatchFilter dispatchFilter;
    private String method;


    DispatcherChainBuilder(String httpMethod, String path, DispatchFilter dispatchFilter) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.dispatchFilter = dispatchFilter;
    }

    public ControllerPart invokes(String method) {
        this.method = method;
        return new ControllerPart();
    }


    public class ControllerPart {
        public Dispatcher on(Controller controller) {
            Dispatcher dispatcher = new Dispatcher(httpMethod, path, controller, method);
            dispatchFilter.addDispatcher(dispatcher);
            return dispatcher;
        }
    }

}
