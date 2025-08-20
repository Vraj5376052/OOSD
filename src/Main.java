import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.application.Platform;

public class Main extends Application {

    private static final int CELL_SIZE = 30;
    private static int COLUMNS = 10;
    private static int ROWS = 20;
    private int LEVEL = 5;
    private boolean MUSIC = false;
    private boolean SOUND_EFFECTS = false;
    private boolean AI_PLAY = false;
    private boolean EXTEND_MODE = false;

    private Scene homeScene;

    @Override
    public void start(Stage primaryStage) {
        SplashScreen.show(primaryStage, () -> showMainMenu(primaryStage));
    }

    private void showMainMenu(Stage primaryStage) {
        Label title = new Label("Main Menu");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        Button btnPlay = new Button("Play");
        Button btnConfig = new Button("Configuration");
        Button btnHighScore = new Button("High Scores");
        Button btnExit = new Button("Exit");

        for (Button b : new Button[]{btnPlay, btnConfig, btnHighScore, btnExit}) {
            b.setPrefWidth(240);
        }

        btnPlay.setOnAction(e -> {
            PlayScreen playScreen = new PlayScreen(COLUMNS, ROWS, CELL_SIZE);
            playScreen.show(primaryStage, () -> primaryStage.setScene(homeScene));
        });

        btnConfig.setOnAction(e -> showConfigScreen(primaryStage));
        btnHighScore.setOnAction(e -> showHighScoreScreen(primaryStage));
        btnExit.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit?");
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no  = new ButtonType("No",  ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(no, yes);
            alert.showAndWait().ifPresent(bt -> { if (bt == yes) Platform.exit(); });
        });

        VBox box = new VBox(18, title, btnPlay, btnConfig, btnHighScore, btnExit);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20, 30, 20, 30));

        homeScene = new Scene(box, 480, 360);
        primaryStage.setTitle("Tetris");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private void showConfigScreen(Stage stage) {
        Configuration.show(stage, () -> stage.setScene(homeScene),
                COLUMNS, ROWS, LEVEL, MUSIC, SOUND_EFFECTS, AI_PLAY, EXTEND_MODE);
    }

    private void showHighScoreScreen(Stage stage) {
        Scene scene = HighScoreScreen.create(() -> stage.setScene(homeScene));
        stage.setTitle("High Scores");
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
