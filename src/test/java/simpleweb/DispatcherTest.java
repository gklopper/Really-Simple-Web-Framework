package simpleweb;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DispatcherTest {

    @Test
    public void shouldNotHaveUrlParams() {

        Dispatcher dispatcher = new Dispatcher("GET", "/admin/new", new TestController(), "save");

        Assert.assertFalse(dispatcher.hasUrlParams());

    }

    @Test
    public void shouldHaveUrlParams() {

        Dispatcher dispatcher = new Dispatcher("GET", "/admin/{id}/{age}/new", new TestController(), "save");

        Assert.assertTrue(dispatcher.hasUrlParams());

    }

    @Test
    public void shouldExtractParameterNames() {
        Dispatcher dispatcher = new Dispatcher("GET", "/admin/{id}/{age}/new", new TestController(), "save");

        List<String> params = dispatcher.getUrlParameters();

        Assert.assertEquals(2, params.size());
        Assert.assertEquals("id", params.get(0));
        Assert.assertEquals("age", params.get(1));
    }

    @Test
    public void shouldMatchUrlWithWildcardAtEnd() {
        Dispatcher dispatcher = new Dispatcher("GET", "/admin/*", new TestController(), "save");

        Assert.assertTrue(dispatcher.matches("GET", "/admin/1234"));
        Assert.assertFalse(dispatcher.matches("GET", "/admin"));
    }
    
    @Test
    public void shouldMatchUrlWithParamsInIt() {
        Dispatcher dispatcher = new Dispatcher("GET", "/admin/{id}/{age}/new", new TestController(), "save");

        Assert.assertTrue(dispatcher.matches("GET", "/admin/1234/foobar/new"));
        Assert.assertFalse(dispatcher.matches("GET", "/admin/1234/new"));
    }

    private class TestController extends Controller {

        public String save() {
            return "save.jsp";
        }

    }




}
