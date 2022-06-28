package Data.Input;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "orders")
public class Orders {
    @JacksonXmlProperty(localName = "order")
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Order> orders = new ArrayList<>();

    public List<Order> getOrders() {
        return orders;
    }
}
