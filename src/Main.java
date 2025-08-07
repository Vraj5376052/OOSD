import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showMainScreen();
    }

    private void showMainScreen() {
        Button btnConfigure = new Button("Configure");
        Button btnPlay = new Button("Play");
        Button btnExit = new Button("Exit");

        btnConfigure.setOnAction(e -> showConfigScreen());
        btnPlay.setOnAction(e -> showPlayScreen());
        btnExit.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(btnConfigure, btnPlay, btnExit);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Tetris - Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showConfigScreen() {
        Button btnBack = new Button("Back to Main");

        // Example configuration options
        Button dummySetting = new Button("Toggle Sound (not functional yet)");

        btnBack.setOnAction(e -> showMainScreen());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(dummySetting, btnBack);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Tetris - Configuration");
        primaryStage.setScene(scene);
    }

    private void showPlayScreen() {
        Button btnBack = new Button("Back to Main");

        // Placeholder for future game content
        Button dummyGame = new Button("Play Mode (Animation TBD)");

        btnBack.setOnAction(e -> showMainScreen());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(dummyGame, btnBack);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Tetris - Game Screen");
        primaryStage.setScene(scene);
    }
}
