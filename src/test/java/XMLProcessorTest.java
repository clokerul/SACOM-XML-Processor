import Processor.XMLProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMLProcessorTest {

    // Environment constants
    private final static int NO_SIMULATED_CUSTOMERS = 2;
    private final static long ORDER_RATE = 500;
    private final static long NO_SIMULATED_ORDERS = 10;
    private final long DIRECTORY_CHECK_RATE = 2000;
    private final static String TEST_PATH = "./src/test/resources";

    private XMLProcessor xmlProcessor;
    private Timer timer;

    @BeforeEach
    void init() {
        timer = new Timer();
        xmlProcessor = new XMLProcessor(TEST_PATH + "/orders/", DIRECTORY_CHECK_RATE);
    }

    @Test
    void testXmlProcessor() {
        simulateRealTimeEnvironment();

        checkSimulationResults();
    }

    /***
     * Emulates in an independent parallel environment order placement and processing
     */
    void simulateRealTimeEnvironment() {
        Thread customerSimulator = new Thread(this::simulateCustomersBehaviour);

        // Start customer simulator as well as the main processor
        customerSimulator.start();
        xmlProcessor.start();

        // Emulate service shutdown
        try {
            // Each customer submits his order
            customerSimulator.join();
            timer.cancel();

            // Kill the processor too
            xmlProcessor.processAndEnd();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /***
     * Simulates customers placing a known number of orders at a fixed rate
     */
    void simulateCustomersBehaviour() {
        SimulatedCustomer[] simulatedCustomers = initCustomers();

        for (SimulatedCustomer simulatedCustomer : simulatedCustomers) {
            timer.scheduleAtFixedRate(simulatedCustomer, 0, ORDER_RATE);
        }

        try {
            // Wait for a total of 15 orders to be placed before shutting down
            Thread.sleep(ORDER_RATE * NO_SIMULATED_ORDERS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /***
     * Populates an array of customers, further used to emulate behaviour
     * @return array of customers
     */
    SimulatedCustomer[] initCustomers() {
        SimulatedCustomer[] simulatedCustomers = new SimulatedCustomer[NO_SIMULATED_CUSTOMERS];

        for (int i = 0; i < simulatedCustomers.length; i++) {
            simulatedCustomers[i] = new SimulatedCustomer(TEST_PATH, i);
        }

        return simulatedCustomers;
    }

    private void checkSimulationResults() {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(TEST_PATH + "/processed/"))) {
            for (Path p : ds) {
                File testFile = p.toFile();

                if (testFile.getName().contains("Sony")) {
                    assertTrue(IOUtils.contentEquals(new FileInputStream(p.toFile()), new FileInputStream(Paths.get(TEST_PATH + "/refs/Sony.xml").toFile())));
                } else if (testFile.getName().contains("Apple")) {
                    assertTrue(IOUtils.contentEquals(new FileInputStream(p.toFile()), new FileInputStream(Paths.get(TEST_PATH + "/refs/Apple.xml").toFile())));
                } else {
                    assertTrue(IOUtils.contentEquals(new FileInputStream(p.toFile()), new FileInputStream(Paths.get(TEST_PATH + "/refs/Panasonic.xml").toFile())));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // Mark objects for garbage collection
    @AfterEach
    void destroy() {
        timer = null;
        xmlProcessor = null;
    }
}
