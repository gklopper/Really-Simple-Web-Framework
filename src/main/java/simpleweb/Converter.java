package simpleweb;

public interface Converter {

    public Object convert(String param);

    static final Converter STRING_CONVERTER = new Converter(){
        public Object convert(String param) {
            return param;
        }
    };

    static final Converter INTEGER_CONVERTER = new Converter(){
        public Object convert(String param) {
            return Integer.parseInt(param);
        }
    };

    static final Converter LONG_CONVERTER = new Converter(){
        public Object convert(String param) {
            return Long.parseLong(param);
        }
    };

    static final Converter FLOAT_CONVERTER = new Converter(){
        public Object convert(String param) {
            return Float.parseFloat(param);
        }
    };

    static final Converter DOUBLE_CONVERTER = new Converter(){
        public Object convert(String param) {
            return Double.parseDouble(param);
        }
    };

}
