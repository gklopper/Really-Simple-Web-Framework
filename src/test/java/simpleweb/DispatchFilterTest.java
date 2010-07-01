package simpleweb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.*;

public class DispatchFilterTest {


    @Mock Dispatcher firstDispatcher;
    @Mock Dispatcher secondDispatcher;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain chain;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldDispatchIfThereIsAMatch() throws ServletException, IOException {

        DispatchFilter filter = new DispatchFilter() {
            @Override
            public void configueControllers() {
                addDispatcher(firstDispatcher);
                addDispatcher(secondDispatcher);
            }
        };
        filter.init(null);

        when(request.getServletPath()).thenReturn("/hello");
        when(request.getMethod()).thenReturn("GET");
        when(firstDispatcher.matches("GET", "/hello")).thenReturn(false);
        when(secondDispatcher.matches("GET", "/hello")).thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(secondDispatcher).dispatch(request, response);
        verify(chain, never()).doFilter(request, response);
    }
    
    @Test
    public void shouldProcessChainIfNoMatch() throws ServletException, IOException {

        DispatchFilter filter = new DispatchFilter() {
            @Override
            public void configueControllers() {
                addDispatcher(firstDispatcher);
                addDispatcher(secondDispatcher);
            }
        };
        filter.init(null);

        when(request.getServletPath()).thenReturn("/hello");
        when(request.getMethod()).thenReturn("GET");
        when(firstDispatcher.matches("GET", "/hello")).thenReturn(false);
        when(secondDispatcher.matches("GET", "/hello")).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void shouldAddAPostDispatcher() throws ServletException {

        final Controller testController = new TestController();

        DispatchFilter filter = new DispatchFilter() {
            @Override
            public void configueControllers() {
                POST("/admin/save").invokes("save").on(testController);
            }
        };


        filter.init(null);

        Dispatcher expected = new Dispatcher("POST", "/admin/save", testController, "save");

        Assert.assertEquals(1, filter.getDispatchers().size());
        Assert.assertEquals(expected, filter.getDispatchers().get(0));
        
    }

    @Test
    public void shouldAddAGetDispatcher() throws ServletException {

        final Controller testController = new TestController();

        DispatchFilter filter = new DispatchFilter() {
            @Override
            public void configueControllers() {
                GET("/admin/edit/{id}").invokes("save").on(testController);
            }
        };


        filter.init(null);

        Dispatcher expected = new Dispatcher("GET", "/admin/edit/{id}", testController, "save");

        Assert.assertEquals(1, filter.getDispatchers().size());
        Assert.assertEquals(expected, filter.getDispatchers().get(0));

    }

    public class TestController extends Controller {

        public String save(@Param("id") int id,
                           @Param("name") String name,
                           @Param("date") Date date) {

            return redirect("/admin/list");
        }

        public String list() {
            addToModel("items", Collections.emptyList());
            return "list.jsp";
        }

        public String edit(@Param("id") int id) {

            return "edit.jsp";
        }
    }
}
