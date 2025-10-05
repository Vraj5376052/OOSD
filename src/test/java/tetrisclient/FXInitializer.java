package tetrisclient;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;

public class FXInitializer {
    private static boolean initialized = false;

    public static synchronized void init() throws InterruptedException {
        if (!initialized) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> latch.countDown());
            latch.await();
            initialized = true;
        }
    }
}
