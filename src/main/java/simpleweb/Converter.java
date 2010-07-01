package simpleweb;

import java.util.Map;

public interface Converter {

    public Object convert(String paramName, Map<String, String> parameters);

}
