package Data.Output;

import Data.Input.Price;
import Data.Input.ProductInput;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Date;

public class ProductOutput implements Comparable<ProductOutput> {
    @JacksonXmlProperty
    String description;

    @JacksonXmlProperty
    String gtin;

    @JacksonXmlProperty
    Price price;

    @JacksonXmlProperty(localName = "orderid")
    String orderId;

    Date timeStamp;


    public ProductOutput(ProductInput product, String orderId, Date timeStamp) {
        this.description = product.getDescription();
        this.gtin = product.getGtin();
        this.price = product.getPrice();
        this.orderId = orderId;
        this.timeStamp = timeStamp;
    }

    public String getDescription() {
        return description;
    }

    public String getGtin() {
        return gtin;
    }

    public Price getPrice() {
        return price;
    }

    public String getOrderId() {
        return orderId;
    }

    private Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int compareTo(ProductOutput o) {
        return o.getTimeStamp().compareTo(this.timeStamp);
    }
}
