package Processor;

import Data.Input.Order;
import Data.Input.Orders;
import Data.Input.ProductInput;
import Data.Output.ProductOutput;
import Data.Output.Products;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderProcessorTask implements Runnable {

    private final String pathToOrder;
    private final AtomicInteger ordersBeingProcessed;

    public OrderProcessorTask(String pathToOrder, AtomicInteger ordersBeingProcessed) {
        this.pathToOrder = pathToOrder;
        this.ordersBeingProcessed = ordersBeingProcessed;
    }

    @Override
    public void run() {
        String fileName;
        String fileDigits;

        try {
            File orderFile = new File(pathToOrder);

            // Process the order file
            Orders orders = processOrderFile(orderFile);
            fileName = orderFile.getName();

            // Obtain the digits from the filename
            fileDigits = fileName.substring(6);
            fileDigits = fileDigits.substring(0, fileDigits.indexOf('.'));

            // Delete the file (or move it to a backup directory)
            if (!orderFile.delete()) {
                System.out.println("Can't successfully process " + fileName);
                throw new IOException();
            }

            // Finish the comuptation and send the orders back to the suppliers
            sendProductsToSuppliers(orders, fileDigits);
            ordersBeingProcessed.decrementAndGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendProductsToSuppliers(Orders orders, String fileDigits) throws IOException {
        List<Order> ordersList = orders.getOrders();
        HashMap<String, List<ProductOutput>> supplierProducts = new HashMap<>();

        // Process individually each order
        for (Order order : ordersList) {
            String orderId = order.getId();
            Date timeStamp = order.getTimeStamp();

            // Process individually each product from the order
            for (ProductInput product : order.getProducts()) {
                String supplier = product.getSupplier();

                // Add the entry to the hashmap for a supplier
                supplierProducts.computeIfAbsent(supplier, k -> new LinkedList<>());
                supplierProducts.get(supplier).add(new ProductOutput(product, orderId, timeStamp));
            }
        }

        // Last step, create output files for every supplier
        for (Map.Entry<String, List<ProductOutput>> supplierEntry : supplierProducts.entrySet()) {
            String supplier = supplierEntry.getKey();
            List<ProductOutput> products = supplierEntry.getValue();
            Collections.sort(products);

            // Create the supplier file with its data
            String fileName = supplier + fileDigits + ".xml";
            XmlMapper xmlMapper = new XmlMapper();

            // Indents output
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Adds XML version
            xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);

            // Writes to file the XML content
            Products products1 = new Products(products);
            xmlMapper.writeValue(new File(XMLProcessor.getPathToProcessed() + fileName), products1);
        }
    }

    public Orders processOrderFile(File inputFile) throws IOException {
        // Initialize xml Mapper and ignore null values
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Deserialize the file
        return xmlMapper.readValue(inputFile, Orders.class);
    }
}
