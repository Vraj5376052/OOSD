package tetrisclient;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {

    public static void show(Stage primaryStage, Runnable onFinished) {
        // Load your splash image from resources
        Image splashImage = new Image(SplashScreen.class.getResourceAsStream("/images/TetrisImage.jpeg"));
        ImageView splashView = new ImageView(splashImage);

        // Adjust image size (optional)
        splashView.setPreserveRatio(true);
        splashView.setFitWidth(480);
        splashView.setFitHeight(360);

        StackPane root = new StackPane(splashView);
        root.setStyle("-fx-background-color: black; -fx-alignment: center;");

        Scene splashScene = new Scene(root, 480, 360);

        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.show();

        // Delay before switching to the main game
        PauseTransition delay = new PauseTransition(Duration.seconds(2.5)); // increased for visibility
        delay.setOnFinished(event -> {
            splashStage.close();
            Platform.runLater(onFinished);
        });
        delay.play();
    }
}
