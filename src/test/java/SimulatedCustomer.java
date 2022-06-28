import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.TimerTask;

// Simulated Customer
public class SimulatedCustomer extends TimerTask{

    private final String basePath;
    private final Path inputPath;
    private final int rank;
    private int iteration;

    public SimulatedCustomer(String testPath, int index) {
        this.basePath = testPath;
        this.inputPath = Paths.get(testPath + "/input/sample_order");
        this.rank = index;
        iteration = 1;
    }

    @Override
    public void run() {
        try {
            // For each customer, a maximum of 1000 iterations can be achieved without overlapping
            Path outputPath = Paths.get(basePath + "/orders/orders" + (rank * 1000 + iteration) + ".xml");
            Files.copy(inputPath, outputPath, StandardCopyOption.REPLACE_EXISTING);

            iteration++;
        } catch (IOException e) {
            // If the copy fails, we will just ignore it for now
            e.printStackTrace();
        }
    }
}
