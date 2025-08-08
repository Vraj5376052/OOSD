import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private static final int CELL_SIZE = 30;
    private static final int COLUMNS = 10;
    private static final int ROWS = 20;

    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        Button btnConfigure = new Button("Configure");
        Button btnPlay = new Button("Play");
        Button btnExit = new Button("Exit");

        btnConfigure.setOnAction(e -> showConfigScreen(primaryStage));
        btnPlay.setOnAction(e -> showPlayScreen(primaryStage));
        btnExit.setOnAction(e -> primaryStage.close());

        VBox mainLayout = new VBox(20, btnConfigure, btnPlay, btnExit);
        mainLayout.setAlignment(Pos.CENTER);

        Scene mainScene = new Scene(mainLayout, 400, 400);
        primaryStage.setTitle("Tetris Main Menu");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void showConfigScreen(Stage stage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> start(stage));

        VBox configLayout = new VBox(10, new Button("Setting 1"), new Button("Setting 2"), backButton);
        configLayout.setAlignment(Pos.CENTER);

        Scene configScene = new Scene(configLayout, 300, 200);
        stage.setTitle("Configuration");
        stage.setScene(configScene);
    }

    private void showPlayScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);

        // Build empty grid cells
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.LIGHTGRAY);
                cell.setStroke(Color.BLACK);
                grid.add(cell, col, row);
            }
        }

        // Creating a falling block
        Rectangle fallingBlock = new Rectangle(CELL_SIZE, CELL_SIZE);
        fallingBlock.setFill(Color.DODGERBLUE);

        // Start position for the falling block
        int[] position = {0, COLUMNS / 2}; // row, col

        // Add block to grid at initial position
        grid.add(fallingBlock, position[1], position[0]);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (timeline != null) timeline.stop();
            start(stage);
        });

        VBox playLayout = new VBox(10, grid, backButton);
        playLayout.setAlignment(Pos.CENTER);

        Scene playScene = new Scene(playLayout, COLUMNS * CELL_SIZE + 50, ROWS * CELL_SIZE + 100);
        stage.setTitle("Game Play");
        stage.setScene(playScene);

        // Animate block falling down every 500ms
        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            // Remove old block position
            grid.getChildren().remove(fallingBlock);

            if (position[0] < ROWS - 1) {
                position[0]++; // move down by one row
            } else {
                position[0] = 0; // reset to top (just for starting)
            }

            grid.add(fallingBlock, position[1], position[0]);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
