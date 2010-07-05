package simpleweb;

import simpleweb.converter.ComplexConverter;
import simpleweb.converter.SimpleConverter;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static simpleweb.converter.DefaultConverters.*;

public class ParamMapper {

    private final Map<Class, Object> converters = new HashMap<Class, Object>();

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
                Object converter = converters.get(type);

                if (converter instanceof SimpleConverter) {
                   value = ((SimpleConverter)converter).convert(getSimpleValue(paramAnnotation.value(), request, urlParams));
                } else if (converter instanceof ComplexConverter){
                   value = ((ComplexConverter)converter).convert(paramAnnotation.value(), request, urlParams);
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

    public void addConverter(Class clazz, ComplexConverter converter) {
        converters.put(clazz, converter);
    }
}
