package tetrisclient;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ConfigurationTest {

    @BeforeAll
    static void initFX() throws InterruptedException {
        FXInitializer.init();
    }

    @Test
    void testShowDoesNotThrow() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Stage stage = new Stage();
            assertDoesNotThrow(() -> Configuration.show(stage, () -> {}));
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testShowBackAction() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Configuration.show(stage, () -> System.out.println("Back pressed"));
            // Simulate back action
            stage.close();
            latch.countDown();
        });
        latch.await();
    }
}
