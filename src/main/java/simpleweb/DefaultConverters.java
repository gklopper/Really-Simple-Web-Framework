package simpleweb;

class DefaultConverters {

    private DefaultConverters(){}

    static final SimpleConverter STRING_CONVERTER = new NullSafeConverter(new SimpleConverter(){
        public Object convert(String value) {
            return value;
        }
    });

    static final SimpleConverter INTEGER_CONVERTER = new NullSafeConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Integer.parseInt(value);
        }
    });

    static final SimpleConverter LONG_CONVERTER = new NullSafeConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Long.parseLong(value);
        }
    });

    static final SimpleConverter FLOAT_CONVERTER = new NullSafeConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Float.parseFloat(value);
        }
    });

    static final SimpleConverter DOUBLE_CONVERTER = new NullSafeConverter(new SimpleConverter(){
        public Object convert(String value) {
            return Double.parseDouble(value);
        }
    });

    private static class NullSafeConverter implements SimpleConverter {
        private SimpleConverter delegate;

        private NullSafeConverter(SimpleConverter delegate) {
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
