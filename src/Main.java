import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            showMain(primaryStage);
        });
        delay.play();
    }
}

public class Main extends Application {

    private static final int CELL_SIZE = 30;
    private static final int COLUMNS = 10;
    private static final int ROWS = 20;

    private Pane gamePane;
    private Timeline timeline;

    private Tetromino currentTetromino;
    private List<Rectangle> lockedBlocks = new ArrayList<>();

    private Scene homeScene, playScene;

    @Override
    public void start(Stage primaryStage) {
        // Main menu buttons
        Button btnConfigure = new Button("Configure");
        Button btnPlay = new Button("Play");
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> showExitConfirmation(stage));


        btnConfigure.setOnAction(e -> showConfigScreen(primaryStage));
        btnPlay.setOnAction(e -> showPlayScreen(primaryStage));
        btnExit.setOnAction(e -> primaryStage.close());

        VBox mainLayout = new VBox(20, btnConfigure, btnPlay, btnExit);
        mainLayout.setAlignment(Pos.CENTER);
        homeScene = new Scene(mainLayout, 400, 400);

        primaryStage.setTitle("Tetris Main Menu");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private void showConfigScreen(Stage stage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(homeScene));

        VBox configLayout = new VBox(10, new Button("Setting 1"), new Button("Setting 2"), backButton);
        configLayout.setAlignment(Pos.CENTER);

        Scene configScene = new Scene(configLayout, 300, 200);
        stage.setTitle("Configuration");
        stage.setScene(configScene);
    }

    private void showPlayScreen(Stage stage) {
        lockedBlocks.clear();

        // pane for drawing blocks and grid
        gamePane = new Pane();
        gamePane.setPrefSize(COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);

        // drawing grid lines
        Canvas gridCanvas = new Canvas(COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);
        drawGrid(gridCanvas.getGraphicsContext2D());
        gamePane.getChildren().add(gridCanvas);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (timeline != null) timeline.stop();
            stage.setScene(homeScene);
        });

        VBox playLayout = new VBox(10, gamePane, backButton);
        playLayout.setAlignment(Pos.CENTER);
        playScene = new Scene(playLayout, COLUMNS * CELL_SIZE + 40, ROWS * CELL_SIZE + 80);

        stage.setTitle("Tetris Game");
        stage.setScene(playScene);

        spawnTetromino();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            moveDown();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        playScene.setOnKeyPressed(e -> {
            if (currentTetromino == null) return;

            if (e.getCode() == KeyCode.LEFT) {
                currentTetromino.move(-1, 0);
            } else if (e.getCode() == KeyCode.RIGHT) {
                currentTetromino.move(1, 0);
            } else if (e.getCode() == KeyCode.DOWN) {
                moveDown();
            } else if (e.getCode() == KeyCode.UP) {
                currentTetromino.rotate();
            }
        });

        gamePane.requestFocus();

    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);

        for (int x = 0; x <= COLUMNS * CELL_SIZE; x += CELL_SIZE) {
            gc.strokeLine(x, 0, x, ROWS * CELL_SIZE);
        }
        for (int y = 0; y <= ROWS * CELL_SIZE; y += CELL_SIZE) {
            gc.strokeLine(0, y, COLUMNS * CELL_SIZE, y);
        }
    }

    private void moveDown() {
        if (currentTetromino != null) {
            boolean moved = currentTetromino.move(0, 1);
            if (!moved) {
                // Lock blocks in place
                lockedBlocks.addAll(currentTetromino.getBlocks());
                currentTetromino = null;

                clearFullLines();

                spawnTetromino();
            }
        }
    }

    private void spawnTetromino() {
        currentTetromino = new Tetromino();
        gamePane.getChildren().addAll(currentTetromino.getBlocks());
    }

    private void clearFullLines() {
        // Check each row when th line is full
        for (int row = ROWS -1; row >= 0; row--) {
            int blocksInRow = 0;
            for (int col = 0; col < COLUMNS; col++) {
                boolean blockFound = false;
                for (Rectangle r : lockedBlocks) {
                    int blockRow = (int) (r.getY() / CELL_SIZE);
                    int blockCol = (int) (r.getX() / CELL_SIZE);
                    if (blockRow == row && blockCol == col) {
                        blockFound = true;
                        break;
                    }
                }
                if (blockFound) blocksInRow++;
            }
            if (blocksInRow == COLUMNS) {
                removeRow(row);
                row++; // recheck same row after removal
            }
        }
    }

    private void removeRow(int rowToRemove) {
        List<Rectangle> toRemove = new ArrayList<>();
        List<Rectangle> toMoveDown = new ArrayList<>();

        for (Rectangle r : lockedBlocks) {
            int blockRow = (int) (r.getY() / CELL_SIZE);
            if (blockRow == rowToRemove) {
                toRemove.add(r);
            } else if (blockRow < rowToRemove) {
                toMoveDown.add(r);
            }
        }

        // Remove rectangles in full line from pane and lockedBlocks list
        for (Rectangle r : toRemove) {
            gamePane.getChildren().remove(r);
            lockedBlocks.remove(r);
        }

        // Move down all blocks above the removed line
        for (Rectangle r : toMoveDown) {
            r.setY(r.getY() + CELL_SIZE);
        }
    }

    // inner class for Tetromino blocks
    class Tetromino {
        private Rectangle[] squares = new Rectangle[4];
        private int[][] shape;
        private int x = COLUMNS / 2 - 1;  // column position
        private int y = 0;                // row position

        Tetromino() {
            shape = TetrominoShapes.getRandomShape();
            Color color = Color.color(Math.random(), Math.random(), Math.random());
            for (int i = 0; i < 4; i++) {
                squares[i] = new Rectangle(CELL_SIZE, CELL_SIZE);
                squares[i].setFill(color);
                squares[i].setStroke(Color.BLACK);
            }
            updatePositions();
        }

        boolean move(int dx, int dy) {
            x += dx;
            y += dy;
            if (isOutOfBounds() || isColliding()) {
                x -= dx;
                y -= dy;
                return false;
            }
            updatePositions();
            return true;
        }

        void rotate() {
            int[][] rotated = new int[4][2];
            for (int i = 0; i < 4; i++) {
                rotated[i][0] = -shape[i][1];
                rotated[i][1] = shape[i][0];
            }
            int[][] original = shape;
            shape = rotated;
            if (isOutOfBounds() || isColliding()) {
                shape = original; // rollback if invalid rotation
            } else {
                updatePositions();
            }
        }

        void updatePositions() {
            for (int i = 0; i < 4; i++) {
                squares[i].setX((x + shape[i][0]) * CELL_SIZE);
                squares[i].setY((y + shape[i][1]) * CELL_SIZE);
            }
        }

        boolean isOutOfBounds() {
            for (int i = 0; i < 4; i++) {
                int newX = x + shape[i][0];
                int newY = y + shape[i][1];
                if (newX < 0 || newX >= COLUMNS || newY >= ROWS || newY < 0) return true;
            }
            return false;
        }

        boolean isColliding() {
            for (Rectangle block : lockedBlocks) {
                for (int i = 0; i < 4; i++) {
                    int blockX = (int) (block.getX() / CELL_SIZE);
                    int blockY = (int) (block.getY() / CELL_SIZE);
                    int newX = x + shape[i][0];
                    int newY = y + shape[i][1];
                    if (blockX == newX && blockY == newY) {
                        return true;
                    }
                }
            }
            return false;
        }

        List<Rectangle> getBlocks() {
            List<Rectangle> list = new ArrayList<>();
            for (Rectangle r : squares) {
                list.add(r);
            }
            return list;
        }
    }

    // Tetromino shapes generation
    static class TetrominoShapes {
        private static final int[][][] SHAPES = {
                {{0, 0}, {1, 0}, {-1, 0}, {0, 1}},   // T shape
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},    // O shape
                {{0, 0}, {1, 0}, {-1, 0}, {-1, 1}},  // L shape
                {{0, 0}, {1, 0}, {-1, 0}, {1, 1}},   // J shape
                {{0, 0}, {1, 0}, {0, 1}, {-1, 1}},   // S shape
                {{0, 0}, {-1, 0}, {0, 1}, {1, 1}},   // Z shape
                {{0, 0}, {-1, 0}, {1, 0}, {2, 0}}    // I shape
        };

        public static int[][] getRandomShape() {
            return SHAPES[new Random().nextInt(SHAPES.length)];
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

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
            stage.close(); // Close the main window
        }
        // If "No" is clicked, dialog closes automatically and program continues
    }

    public static void main(String[] args) {
        launch(args);
    }
}
