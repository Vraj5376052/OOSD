import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {

    public static void show(Stage primaryStage, Runnable onFinished) {
        Label splashText = new Label("Tetris Game, Group 45, 2006ICT");
        splashText.setStyle("-fx-font-size: 24px; -fx-text-alignment: center;");

        StackPane root = new StackPane(splashText);
        root.setStyle("-fx-background-color: gray; -fx-alignment: center;");

        Scene splashScene = new Scene(root, 480, 360);

        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(splashScene);
        splashStage.show();
        splashStage.centerOnScreen();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            splashStage.close();
            Platform.runLater(onFinished);
        });
        delay.play();
    }
}
