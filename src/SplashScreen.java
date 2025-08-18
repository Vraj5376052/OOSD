import com.sun.tools.javac.Main;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the splash content
        Label splashText = new Label("Tetris Game, Group 45, 2006ICT / 2805ICT / 3815ICT");
        splashText.setStyle("-fx-font-size: 24px; -fx-text-alignment: center;");

        StackPane root = new StackPane(splashText);
        root.setStyle("-fx-background-color: gray; -fx-alignment: center;");

        Scene splashScene = new Scene(root, 400, 300);

        // Create splash stage
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(splashScene);
        splashStage.show();

        // Show splash for 3 seconds, then open main menu
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            splashStage.close();
//            new Main().start(primaryStage);
        });
        delay.play();
    }
}
