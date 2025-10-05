package tetrisclient;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayScreenTest {

    @BeforeAll
    static void initFX() throws InterruptedException {
        FXInitializer.init();
    }

    @Test
    void testClearFullLinesAndScore() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                PlayScreen screen = new PlayScreen(10, 20, 30);

                // Inject gamePane
                Field paneField = PlayScreen.class.getDeclaredField("gamePane");
                paneField.setAccessible(true);
                paneField.set(screen, new Pane());

                // Inject lockedBlocks to form a complete line
                Field blocksField = PlayScreen.class.getDeclaredField("lockedBlocks");
                blocksField.setAccessible(true);
                List<Rectangle> lockedBlocks = (List<Rectangle>) blocksField.get(screen);

                for (int x = 0; x < 10; x++) {
                    Rectangle r = new Rectangle(30, 30);
                    r.setX(x * 30);
                    r.setY(19 * 30);
                    lockedBlocks.add(r);
                }

                // Inject score and totalLines
                Field scoreField = PlayScreen.class.getDeclaredField("score");
                scoreField.setAccessible(true);
                scoreField.set(screen, 0);

                Field totalLinesField = PlayScreen.class.getDeclaredField("totalLines");
                totalLinesField.setAccessible(true);
                totalLinesField.set(screen, 0);

                // Inject scoreLabel and linesLabel
                Field linesLabelField = PlayScreen.class.getDeclaredField("linesLabel");
                linesLabelField.setAccessible(true);
                linesLabelField.set(screen, new javafx.scene.control.Label());

                Field scoreLabelField = PlayScreen.class.getDeclaredField("scoreLabel");
                scoreLabelField.setAccessible(true);
                scoreLabelField.set(screen, new javafx.scene.control.Label());

                // Call the method
                screen.clearFullLines();

                assertEquals(150, scoreField.get(screen));
                assertEquals(1, totalLinesField.get(screen));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
}
