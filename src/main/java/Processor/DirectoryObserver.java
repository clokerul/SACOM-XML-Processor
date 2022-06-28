package Processor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class DirectoryObserver extends TimerTask {

    // Directory Stream is better for large number of directory entries
    private final String szPathToDirectory;
    private final WeakReference<List<String>> syncOrdersListRef;

    private final AtomicInteger ordersBeingProcessed;
    private final Path pathToDirectory;

    private final Object lock;

    public DirectoryObserver(String pathToDirectory, List<String> syncOrdersListRef, AtomicInteger ordersBeingProcessed, Object lock) {
        this.szPathToDirectory = pathToDirectory;
        this.pathToDirectory = Paths.get(pathToDirectory);
        this.syncOrdersListRef = new WeakReference<>(syncOrdersListRef);
        this.ordersBeingProcessed = ordersBeingProcessed;
        this.lock = lock;
    }

    /***
     * Scans the directory found at @var pathToDirectory
     */
    @Override
    public void run() {
        synchronized (lock) {
            // Avoid double file processing
            if (ordersBeingProcessed.get() > 0 || syncOrdersListRef.get() == null) {
                return;
            }
            syncOrdersListRef.get().clear();

            // Start iterating through directory files and add them to the processing list
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(pathToDirectory)) {
                for (Path p : ds) {
                    // Check for fileName integrity constraints
                    if (isNumeric(p.toString()))
                        syncOrdersListRef.get().add(p.toString());
                }

                // If orders were found, they will start being processed
                ordersBeingProcessed.set(syncOrdersListRef.get().size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Checks if fileName is valid
     * @return true if fileName is valid
     */
    public boolean isNumeric(String fileName) {
        String strNum = fileName.substring(fileName.lastIndexOf("orders") + 6); // Remove orders
        strNum = strNum.substring(0, strNum.indexOf('.')); // Remove .xml

        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
