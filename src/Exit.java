import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class Exit extends Application {

    @Override
    public void start(Stage stage) {
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> showExitConfirmation(stage));

        VBox root = new VBox(10, exitButton);
        root.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Tetris Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void showExitConfirmation(Stage stage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Choose your option.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
