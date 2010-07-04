package simpleweb;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static simpleweb.DefaultConverters.*;

public class ParamMapper {

    private final Map<Class, SimpleConverter> converters = new HashMap<Class, SimpleConverter>();

    public ParamMapper() {
        addConverter(String.class, STRING_CONVERTER);
        addConverter(Integer.class, INTEGER_CONVERTER);
        addConverter(int.class, INTEGER_CONVERTER);
        addConverter(Long.class, LONG_CONVERTER);
        addConverter(long.class, LONG_CONVERTER);
        addConverter(Float.class, FLOAT_CONVERTER);
        addConverter(float.class, FLOAT_CONVERTER);
        addConverter(Double.class, DOUBLE_CONVERTER);
        addConverter(double.class, DOUBLE_CONVERTER);
    }


    Object[] mapParams(HttpServletRequest request, Map<String, String> urlParams, Method method) {
        List<Object> params = new ArrayList<Object>();

        Class[] types = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < types.length; i++) {
            Class type = types[i];
            Param paramAnnotation = getAnnotation(annotations[i]);

            if (paramAnnotation == null) {
                params.add(getDefaultValue(type));
            } else {
                Object value = null;
                //TODO arrays
                Object converter = getConverter(type);

                if (converter instanceof SimpleConverter) {
                   value = ((SimpleConverter)converter).convert(getSimpleValue(paramAnnotation.value(), request, urlParams));    
                } else {
                    //TODO complex types
                }

                if (value == null && isRawNumber(type)) {
                    params.add(getDefaultValue(type));
                } else {
                    params.add(value);    
                }
            }
        }

        return params.toArray();
    }

    private String getSimpleValue(String name, HttpServletRequest request, Map<String, String> urlParams) {
        String value = urlParams.get(name);
        if (value == null) {
            value = request.getParameter(name);
        }
        return value;
    }

    private SimpleConverter getConverter(Class type) {
        return converters.get(type);
    }

    private Object getDefaultValue(Class paramClass) {

        if(isRawNumber(paramClass)) {
            return (short)0;
        }

        return null;
    }

    private boolean isRawNumber(Class paramClass) {
        return paramClass.equals(int.class)
                || paramClass.equals(long.class)
                || paramClass.equals(float.class)
                || paramClass.equals(double.class)
                || paramClass.equals(short.class);
    }

    private Param getAnnotation(Annotation[] annotations) {

        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                return (Param) annotation;
            }
        }

        return null;
    }

    public void addConverter(Class clazz, SimpleConverter converter) {
        converters.put(clazz, converter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParamMapper that = (ParamMapper) o;

        if (converters != null ? !converters.equals(that.converters) : that.converters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return converters != null ? converters.hashCode() : 0;
    }
}
