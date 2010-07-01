package simpleweb;

import java.util.Map;

class DefaultConverters {

    private DefaultConverters(){}

    static final Converter STRING_CONVERTER = new NullSafeConverter(new Converter(){
        public Object convert(String paramName, Map<String, String> parameters) {
            return parameters.get(paramName);
        }
    });

    static final Converter INTEGER_CONVERTER = new NullSafeConverter(new Converter(){
        public Object convert(String paramName, Map<String, String> parameters) {
            return Integer.parseInt(parameters.get(paramName));
        }
    });

    static final Converter LONG_CONVERTER = new NullSafeConverter(new Converter(){
        public Object convert(String paramName, Map<String, String> parameters) {
            return Long.parseLong(parameters.get(paramName));
        }
    });

    static final Converter FLOAT_CONVERTER = new NullSafeConverter(new Converter(){
        public Object convert(String paramName, Map<String, String> parameters) {
            return Float.parseFloat(parameters.get(paramName));
        }
    });

    static final Converter DOUBLE_CONVERTER = new NullSafeConverter(new Converter(){
        public Object convert(String paramName, Map<String, String> parameters) {
            return Double.parseDouble(parameters.get(paramName));
        }
    });

    private static class NullSafeConverter implements Converter {
        private Converter delegate;

        private NullSafeConverter(Converter delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object convert(String paramName, Map<String, String> parameters) {
            if (parameters.get(paramName) == null) {
                return null;
            }
            return delegate.convert(paramName, parameters);  
        }
    }

}
