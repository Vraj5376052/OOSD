package tetrisclient;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AudioManagerMockTest {

    @BeforeAll
    static void initFX() throws InterruptedException {
        FXInitializer.init();
    }

    @Test
    void testPlaySound() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> {
                AudioManager.play("mock_sound.mp3");
            });
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testStopAll() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertDoesNotThrow(AudioManager::stopAll);
            latch.countDown();
        });
        latch.await();
    }
}
