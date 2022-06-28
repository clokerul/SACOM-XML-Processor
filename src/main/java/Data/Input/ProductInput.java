package Data.Input;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ProductInput {
    @JacksonXmlProperty
    String description;

    @JacksonXmlProperty
    String gtin;

    @JacksonXmlProperty
    Price price;

    @JacksonXmlProperty
    String supplier;

    public String getDescription() {
        return description;
    }

    public String getGtin() {
        return gtin;
    }

    public Price getPrice() {
        return price;
    }

    public String getSupplier() {
        return supplier;
    }
}
