package src;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Splash screen
        Label splashLabel = new Label("TETRIS\n2006ICT / 2805ICT / 3815ICT\nGroup Name: <Your Team>");
        splashLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-alignment: center;");
        StackPane splashPane = new StackPane(splashLabel);
        splashPane.setStyle("-fx-background-color: #000000;");
        Scene splashScene = new Scene(splashPane, 600, 400);

        primaryStage.setTitle("Tetris Game");
        primaryStage.setScene(splashScene);
        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            try {
                Parent mainMenu = FXMLLoader.load(getClass().getResource("/views/main_menu.fxml"));
                primaryStage.setScene(new Scene(mainMenu));
            } catch (Exception ex) {
                System.out.println("Failed to load main_menu.fxml");
                ex.printStackTrace();
            }
        });
        delay.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}