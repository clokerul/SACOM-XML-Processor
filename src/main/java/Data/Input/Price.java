package Data.Input;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Price {
    @JacksonXmlProperty(isAttribute = true)
    String currency;

    @JacksonXmlText
    Double price;

    public String getCurrency() {
        return currency;
    }

    public Double getPrice() {
        return price;
    }
}
