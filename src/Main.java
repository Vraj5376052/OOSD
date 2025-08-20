import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.shape.Rectangle;
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

    private Pane gamePane;
    private Timeline timeline;
    private Tetromino currentTetromino;
    private List<Rectangle> lockedBlocks = new ArrayList<>();

    private Scene homeScene, playScene;

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

        // Button
        for (Button b : new Button[]{btnPlay, btnConfig, btnHighScore, btnExit}) {
            b.setPrefWidth(240);
        }

        btnPlay.setOnAction(e -> showPlayScreen(primaryStage));
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


    private void showPlayScreen(Stage stage) {
        PlayScreen playScreen = new PlayScreen();
        playScreen.showPlayScreen(primaryStage);
    }


    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        for (int x = 0; x <= COLUMNS * CELL_SIZE; x += CELL_SIZE) gc.strokeLine(x, 0, x, ROWS * CELL_SIZE);
        for (int y = 0; y <= ROWS * CELL_SIZE; y += CELL_SIZE) gc.strokeLine(0, y, COLUMNS * CELL_SIZE, y);
    }

    private void moveDown() {
        if (currentTetromino != null) {
            boolean moved = currentTetromino.move(0, 1);
            if (!moved) {
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
        for (int row = ROWS - 1; row >= 0; row--) {
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
                row++;
            }
        }
    }

    private void removeRow(int rowToRemove) {
        List<Rectangle> toRemove = new ArrayList<>();
        List<Rectangle> toMoveDown = new ArrayList<>();

        for (Rectangle r : lockedBlocks) {
            int blockRow = (int) (r.getY() / CELL_SIZE);
            if (blockRow == rowToRemove) toRemove.add(r);
            else if (blockRow < rowToRemove) toMoveDown.add(r);
        }

        for (Rectangle r : toRemove) {
            gamePane.getChildren().remove(r);
            lockedBlocks.remove(r);
        }

        for (Rectangle r : toMoveDown) r.setY(r.getY() + CELL_SIZE);
    }

    class Tetromino {
        private Rectangle[] squares = new Rectangle[4];
        private int[][] shape;
        private int x = COLUMNS / 2 - 1;
        private int y = 0;

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
            if (isOutOfBounds() || isColliding()) shape = original;
            else updatePositions();
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
                    if (blockX == newX && blockY == newY) return true;
                }
            }
            return false;
        }

        List<Rectangle> getBlocks() {
            List<Rectangle> list = new ArrayList<>();
            for (Rectangle r : squares) list.add(r);
            return list;
        }
    }

    static class TetrominoShapes {
        private static final int[][][] SHAPES = {
                {{0, 0}, {1, 0}, {-1, 0}, {0, 1}},   // T
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},    // O
                {{0, 0}, {1, 0}, {-1, 0}, {-1, 1}},  // L
                {{0, 0}, {1, 0}, {-1, 0}, {1, 1}},   // J
                {{0, 0}, {1, 0}, {0, 1}, {-1, 1}},   // S
                {{0, 0}, {-1, 0}, {0, 1}, {1, 1}},   // Z
                {{0, 0}, {-1, 0}, {1, 0}, {2, 0}}    // I
        };

        public static int[][] getRandomShape() {
            return SHAPES[new Random().nextInt(SHAPES.length)];
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}