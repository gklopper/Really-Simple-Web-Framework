package simpleweb.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class DateConverter implements SimpleConverter {

    private final SimpleDateFormat format;

    public DateConverter(String format, Locale locale) {
        this.format = new SimpleDateFormat(format, locale);
    }

    public DateConverter(String format) {
        this(format, Locale.getDefault());
    }


    public Object convert(String value) {
        try {
            return format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse date " + value, e);
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
