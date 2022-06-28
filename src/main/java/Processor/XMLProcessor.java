package Processor;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class XMLProcessor extends Thread {
    private static String pathToProcessed = "./src/test/resources/processed/";

    private final Timer timer;
    private final List<String> syncOrdersList;
    private final ThreadPoolExecutor threadPoolExecutor;

    private final Object lock;
    private final AtomicInteger ordersBeingProcessed;
    private final AtomicBoolean running;

    private final DirectoryObserver directoryObserver;
    private final long scanRate;

    /***
     * @param inputDirectory the location that will be scanned for orders
     * @param scanRate rate in milliseconds
     */
    public XMLProcessor(String inputDirectory, long scanRate) {
        this.scanRate = scanRate;
        this.timer = new Timer();
        this.ordersBeingProcessed = new AtomicInteger(0);
        this.syncOrdersList = Collections.synchronizedList(new LinkedList<>());
        this.lock = new Object();
        this.directoryObserver = new DirectoryObserver(inputDirectory, syncOrdersList, ordersBeingProcessed, lock);
        this.threadPoolExecutor = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 5);
        this.running = new AtomicBoolean(true);
    }

    /***
     * The Processor will run until it is explicitly stopped by @func processAndEnd
     */
    @Override
    public void run() {
        // Starting the scanner at fixed intervals, will use shared memory list syncOrdersList
        timer.scheduleAtFixedRate(directoryObserver, 0, scanRate);

        // AtomicBoolean triggered by XMLProcessor.finish() function
        while (running.get()) {

            // Critical region, launching worker threads to process found orders
            synchronized (lock) {
                // Multi-thread processing of orders
                for (String pathToOrder : syncOrdersList) {
                    threadPoolExecutor.submit(new OrderProcessorTask(pathToOrder, ordersBeingProcessed));
                }

                try {
                    // Wait roughly 0.1s for each order to be processed
                    Thread.sleep(100L * ordersBeingProcessed.get());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Create some delay in-between checks to avoid CPU starvation
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * After each order is processed, the processor is closed
     */
    public void processAndEnd() {
        try {
            // Make sure the directory observer it's fired before checking for pending orders
            Thread.sleep(scanRate * 2);

            // Wait until the last order is processed
            while (ordersBeingProcessed.get() > 0) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // End the execution
        running.set(false);
        timer.cancel();
    }

    public static String getPathToProcessed() {
        return pathToProcessed;
    }

    public void setPathToProcessed(String pathToProcessed) {
        XMLProcessor.pathToProcessed = pathToProcessed;
    }
}

// by George Lazureanu