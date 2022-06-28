import Processor.XMLProcessor;

/***
 *  Asta e o aplicatie dezvoltata si mai tarziu livrata ce scaneaza un director pentru fisiere "orders" de la clienti
 * jar-ul este dat mai departe pentru folosinta.
 */
public class Demo {
    public static void main(String[] args) {
        // Rate and Period are presented in milliseconds
        long directoryScanRate = 2000L;
        long directoryScanPeriod = 50000L;

        // Relative or absolute paths to your desired directories
        String pathToOrdersDirectory = "./src/test/resources/processed";
        String pathToProcessedOrdersDirectory = "./src/test/resources/orders/";

        XMLProcessor xmlProcessor = new XMLProcessor(pathToOrdersDirectory, directoryScanRate);
        xmlProcessor.setPathToProcessed(pathToProcessedOrdersDirectory);

        // Driver function
        xmlProcessor.start();

        try {
            Thread.sleep(directoryScanPeriod);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // End the execution
        xmlProcessor.processAndEnd();
    }
}
