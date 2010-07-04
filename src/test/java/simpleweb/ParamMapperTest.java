package simpleweb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class ParamMapperTest {
    
    @Mock private HttpServletRequest request;

    @Before public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnParamsInCorrectOrder() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodOne", String.class, Integer.class, int.class);

        when(request.getParameter("age")).thenReturn("38");

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("name", "Grant");
        urlParams.put("id", "5");

        Object[] paramValues = new ParamMapper().mapParams(request, urlParams, stubMethod);

        Assert.assertEquals(3, paramValues.length);
        Assert.assertEquals("Grant", paramValues[0]);
        Assert.assertEquals(38, paramValues[1]);
        Assert.assertEquals(5, paramValues[2]);
    }

    @Test
    public void shouldSetNullIfNoParameterAvailable() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodTwo", String.class, Integer.class, int.class, Integer.class);

        when(request.getParameter("age")).thenReturn(null);

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("name", "Grant");
        urlParams.put("id", "5");

        Object[] paramValues = new ParamMapper().mapParams(request, urlParams, stubMethod);

        Assert.assertEquals(4, paramValues.length);
        Assert.assertEquals("Grant", paramValues[0]);
        Assert.assertNull(paramValues[1]);
        Assert.assertEquals(5, paramValues[2]);
        Assert.assertNull(paramValues[3]);
    }
    
    @Test
    public void shouldSetDefaultForRawTypeIfParameterIsNotAnnotated() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodFour", int.class);

        Map<String, String> urlParams = new HashMap<String, String>();

        Object[] paramValues = new ParamMapper().mapParams(request, urlParams, stubMethod);

        Assert.assertEquals(1, paramValues.length);
        Assert.assertEquals((short)0, paramValues[0]);
    }

    @Test
    public void shouldSetDefaultForRawTypeIfValueIsNull() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodFive", int.class);

        Map<String, String> urlParams = new HashMap<String, String>();

        Object[] paramValues = new ParamMapper().mapParams(request, urlParams, stubMethod);

        Assert.assertEquals(1, paramValues.length);
        Assert.assertEquals((short)0, paramValues[0]);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    private @interface TestAnnotation{}

    private class TestClass {

        public String methodOne(@Param("name")String name,
                                @Param("age") Integer age,
                                 @TestAnnotation @Param("id") int id) {
            return null;
        }

        public String methodTwo(@Param("name")String name,
                                 @Param("age") Integer age,
                                 @TestAnnotation @Param("id") int id,
                                 @Param("unknown") Integer unknown) {
            return null;
        }

        public String methodThree(@Param("name")String name, String id) {
            return null;
        }

        public String methodFour(int id) {
            return null;
        }

        public String methodFive(@Param("id") int id) {
            return null;
        }

    }

}
