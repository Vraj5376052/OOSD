import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int CELL_SIZE = 30;
    private static int COLUMNS = 10;
    private static int ROWS = 20;
    private static int LEVEL = 5;
    private static boolean MUSIC = true;
    private static boolean SOUND_EFFECTS = true;
    private static boolean AI_PLAY = false;
    private static boolean EXTEND_MODE = false;

    private Scene homeScene;

    @Override
    public void start(Stage primaryStage) {
        SplashScreen.show(primaryStage, () -> showMainMenu(primaryStage));
    }

    private void showMainMenu(Stage primaryStage) {
        Label title = new Label("Main Menu");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        AudioManager.initialize();
        AudioManager.playBackgroundMusic();


        Button btnPlay = new Button("Play");
        Button btnConfig = new Button("Configuration");
        Button btnHighScore = new Button("High Scores");
        Button btnExit = new Button("Exit");

        for (Button b : new Button[]{btnPlay, btnConfig, btnHighScore, btnExit}) {
            b.setPrefWidth(240);
        }

        btnPlay.setOnAction(e -> {
            PlayScreen playScreen = new PlayScreen(COLUMNS, ROWS, CELL_SIZE);
            // Pass AI_PLAY boolean to PlayScreen.show
            playScreen.show(primaryStage, () -> primaryStage.setScene(homeScene), AI_PLAY);
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
        Configuration.show(stage, () -> stage.setScene(homeScene));
    }

    private void showHighScoreScreen(Stage stage) {
        Scene scene = HighScoreScreen.create(() -> stage.setScene(homeScene));
        stage.setTitle("High Scores");
        stage.setScene(scene);
    }

    // ===== Static Getters & Setters for Configuration =====
    public static int getColumns() { return COLUMNS; }
    public static void setColumns(int columns) { COLUMNS = columns; }

    public static int getRows() { return ROWS; }
    public static void setRows(int rows) { ROWS = rows; }

    public static int getLEVEL() { return LEVEL; }
    public static void setLEVEL(int level) { LEVEL = level; }

    public static boolean isMUSIC() { return MUSIC; }
    public static void setMUSIC(boolean music) { MUSIC = music; }

    public static boolean isSOUND_EFFECTS() { return SOUND_EFFECTS; }
    public static void setSOUND_EFFECTS(boolean sound) { SOUND_EFFECTS = sound; }

    public static boolean isAI_PLAY() { return AI_PLAY; }
    public static void setAI_PLAY(boolean ai) { AI_PLAY = ai; }

    public static boolean isEXTEND_MODE() { return EXTEND_MODE; }
    public static void setEXTEND_MODE(boolean extend) { EXTEND_MODE = extend; }

    public static void main(String[] args) {
        launch(args);
    }
}
