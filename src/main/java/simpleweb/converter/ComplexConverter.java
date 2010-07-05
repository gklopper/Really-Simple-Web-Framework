package simpleweb.converter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ComplexConverter {

    public Object convert(String paramName, HttpServletRequest request, Map<String, String> urlParams);

}
