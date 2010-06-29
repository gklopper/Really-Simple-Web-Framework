package simpleweb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static simpleweb.Converter.*;

public class ParamMapper {

    private final Map<Class, Converter> converters = new HashMap<Class, Converter>();
    
    public ParamMapper() {
        addConverter(String.class, STRING_CONVERTER);
        addConverter(Integer.class, INTEGER_CONVERTER);
        addConverter(Long.class, LONG_CONVERTER);
        addConverter(Float.class, FLOAT_CONVERTER);
        addConverter(Double.class, DOUBLE_CONVERTER);
    }


    Object[] mapParams(Map requestParams, Method method) {
        List<Object> params = new ArrayList<Object>();

        Class[] types = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < types.length; i++) {
            Class type = types[i];
            Param paramAnnotation = getAnnotation(annotations[i]);
            String stringValue = (String) requestParams.get(paramAnnotation.value());
            Converter converter = converters.get(type);
            params.add(converter.convert(stringValue));
        }

        return params.toArray();
    }

    private Param getAnnotation(Annotation[] annotations) {

        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                return (Param) annotation;
            }
        }

        return null;
    }

    public void addConverter(Class clazz, Converter converter) {
        converters.put(clazz, converter);
    }
}
