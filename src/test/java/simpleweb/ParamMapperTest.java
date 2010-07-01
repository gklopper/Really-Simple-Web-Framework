package simpleweb;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ParamMapperTest {

    @Test
    public void shouldReturnParamsInCorrectOrder() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodOne", String.class, int.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Grant");
        params.put("id", "5");

        Object[] paramValues = new ParamMapper().mapParams(params, stubMethod);

        Assert.assertEquals(2, paramValues.length);
        Assert.assertEquals("Grant", paramValues[0]);
        Assert.assertEquals(5, paramValues[1]);
    }

    @Test
    public void shouldSetNullIfNoParameterAvailable() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodTwo", String.class, int.class, Integer.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Grant");
        params.put("id", "5");

        Object[] paramValues = new ParamMapper().mapParams(params, stubMethod);

        Assert.assertEquals(3, paramValues.length);
        Assert.assertEquals("Grant", paramValues[0]);
        Assert.assertEquals(5, paramValues[1]);
        Assert.assertNull(paramValues[2]);
    }
    
    @Test
    public void shouldSetDefaultForRawTypeIfParameterIsNotAnnotated() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodFour", int.class);

        Map<String, String> params = new HashMap<String, String>();

        Object[] paramValues = new ParamMapper().mapParams(params, stubMethod);

        Assert.assertEquals(1, paramValues.length);
        Assert.assertEquals((short)0, paramValues[0]);
    }

    @Test
    public void shouldSetDefaultForRawTypeIfValueIsNull() throws NoSuchMethodException {
        Method stubMethod = TestClass.class.getMethod("methodFive", int.class);

        Map<String, String> params = new HashMap<String, String>();

        Object[] paramValues = new ParamMapper().mapParams(params, stubMethod);

        Assert.assertEquals(1, paramValues.length);
        Assert.assertEquals((short)0, paramValues[0]);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    private @interface TestAnnotation{}

    private class TestClass {

        public String methodOne(@Param("name")String name,
                                 @TestAnnotation @Param("id") int id) {
            return null;
        }

        public String methodTwo(@Param("name")String name,
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
