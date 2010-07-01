package simpleweb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class DateConverter implements Converter{

    private final SimpleDateFormat format;

    public DateConverter(String format, Locale locale) {
        this.format = new SimpleDateFormat(format, locale);
    }

    public DateConverter(String format) {
        this(format, Locale.getDefault());
    }


    public Object convert(String param) {
        try {
            return format.parse(param);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse date " + param, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateConverter)) return false;

        DateConverter that = (DateConverter) o;

        if (!format.equals(that.format)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return format.hashCode();
    }
}
