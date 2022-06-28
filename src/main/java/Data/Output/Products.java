package Data.Output;

import Data.Output.ProductOutput;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "products")
public class Products {
    @JacksonXmlProperty(localName = "product")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<ProductOutput> productOutput;

    public Products(List<ProductOutput> productOutput) {
        this.productOutput = productOutput;
    }
}
