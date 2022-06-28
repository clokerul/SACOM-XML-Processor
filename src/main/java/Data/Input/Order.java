package Data.Input;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    @JacksonXmlProperty(isAttribute = true, localName = "created")
    Date timestamp;

    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id;

    @JacksonXmlProperty(localName = "product")
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ProductInput> productInputs = new ArrayList<>();

    public Date getTimeStamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public List<ProductInput> getProducts() {
        return productInputs;
    }
}
