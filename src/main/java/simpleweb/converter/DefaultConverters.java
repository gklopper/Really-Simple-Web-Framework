package simpleweb.converter;

public class DefaultConverters {

    private DefaultConverters(){}

    public static final SimpleConverter STRING_CONVERTER = new NullSafeDelegatingConverter(new SimpleConverter(){
        public Object convert(String value) {
            return value;
        }
    });

    public static final SimpleConverter INTEGER_CONVERTER = new NullSafeDelegatingConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Integer.parseInt(value);
        }
    });

    public static final SimpleConverter LONG_CONVERTER = new NullSafeDelegatingConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Long.parseLong(value);
        }
    });

    public static final SimpleConverter FLOAT_CONVERTER = new NullSafeDelegatingConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Float.parseFloat(value);
        }
    });

    public static final SimpleConverter DOUBLE_CONVERTER = new NullSafeDelegatingConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Double.parseDouble(value);
        }
    });

    private static class NullSafeDelegatingConverter implements SimpleConverter {
        private SimpleConverter delegate;

        private NullSafeDelegatingConverter(SimpleConverter delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object convert(String value) {
            if (value == null) {
                return null;
            }
            return delegate.convert(value);
        }
    }

}
