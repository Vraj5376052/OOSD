import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayScreen {
    private final int COLUMNS;
    private final int ROWS;
    private final int CELL_SIZE;

    private Pane gamePane;
    private Timeline timeline;
    private Tetromino currentTetromino;
    private Tetromino nextTetromino;
    private List<Rectangle> lockedBlocks = new ArrayList<>();
    private Scene playScene;

    private int score = 0;
    private int totalLines = 0;

    // UI labels
    private Label scoreLabel;
    private Label linesLabel;
    private Canvas nextPreview;

    public PlayScreen(int columns, int rows, int cellSize) {
        this.COLUMNS = columns;
        this.ROWS = rows;
        this.CELL_SIZE = cellSize;
    }

    public void show(Stage stage, Runnable onBack) {
        lockedBlocks.clear();
        score = 0;
        totalLines = 0;

        // game field
        gamePane = new Pane();
        gamePane.setPrefSize(COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);
        gamePane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        gamePane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(gamePane, Pos.CENTER);

        Canvas gridCanvas = new Canvas(COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);
        drawGrid(gridCanvas.getGraphicsContext2D());
        gamePane.getChildren().add(gridCanvas);

        // frame
        Rectangle frame = new Rectangle(COLUMNS * CELL_SIZE + 2, ROWS * CELL_SIZE + 2);
        frame.setFill(Color.TRANSPARENT);
        frame.setStroke(Color.SILVER);

        // pause label
        Label pauseHint = new Label("Game is paused,\nPress 'P' to continue...");
        pauseHint.setVisible(false);

        StackPane playArea = new StackPane(new Group(frame), gamePane, pauseHint);
        playArea.setPadding(new Insets(10));
        playArea.setAlignment(Pos.CENTER);
        StackPane.setAlignment(pauseHint, Pos.BASELINE_CENTER);
        StackPane.setMargin(pauseHint, new Insets(0, 0, 0, 20));

        // info panel (left side)
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.TOP_LEFT);

        Label infoTitle = new Label("Game Info (Player 1)");
        Label playerType = new Label("Player Type: Human");
        Label initLevel = new Label("Initial Level: 1");
        Label currLevel = new Label("Current Level: 1");
        linesLabel = new Label("Lines Erased: 0");
        scoreLabel = new Label("Score: 0");
        Label nextLabel = new Label("Next Tetromino:");

        nextPreview = new Canvas(80, 80);

        infoBox.getChildren().addAll(
                infoTitle, playerType, initLevel, currLevel,
                linesLabel, scoreLabel, nextLabel, nextPreview
        );

        // combine info + play area
        HBox center = new HBox(20, infoBox, playArea);
        center.setAlignment(Pos.CENTER);

        // top title
        Label title = new Label("Play");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        HBox top = new HBox(title);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(8, 0, 4, 0));

        // back button with stop confirmation
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Stop Game");
            alert.setHeaderText(null);
            alert.setContentText("Stop Game?");
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(no, yes);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == yes) {
                    if (timeline != null) timeline.stop();
                    onBack.run();
                }
            });
        });
        HBox bottom = new HBox(backButton);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(6, 0, 8, 0));

        // layout
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(center);
        root.setBottom(bottom);

        playScene = new Scene(root, COLUMNS * CELL_SIZE + 250, ROWS * CELL_SIZE + 180);
        stage.setTitle("Tetris");
        stage.setScene(playScene);

        spawnTetromino();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> moveDown()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        final BooleanProperty paused = new SimpleBooleanProperty(false);

        //pause game with 'P' key
        playScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                boolean p = !paused.get();
                paused.set(p);
                if (p) {
                    timeline.pause();
                    pauseHint.setVisible(true);
                } else {
                    timeline.play();
                    pauseHint.setVisible(false);
                }
                return;
            }
            if (paused.get() || currentTetromino == null) return;

            //switch cases
            switch (e.getCode()) {
                case LEFT -> currentTetromino.move(-1, 0);
                case RIGHT -> currentTetromino.move(1, 0);
                case DOWN -> moveDown();
                case UP -> currentTetromino.rotate();
                default -> {
                }
            }
        });

        playScene.getRoot().requestFocus();
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        for (int x = 0; x <= COLUMNS * CELL_SIZE; x += CELL_SIZE)
            gc.strokeLine(x, 0, x, ROWS * CELL_SIZE);
        for (int y = 0; y <= ROWS * CELL_SIZE; y += CELL_SIZE)
            gc.strokeLine(0, y, COLUMNS * CELL_SIZE, y);
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
        if (nextTetromino == null) nextTetromino = new Tetromino();
        currentTetromino = nextTetromino;

        if (currentTetromino.isColliding()) {
            gameOver();
            return;
        }

        gamePane.getChildren().addAll(currentTetromino.getBlocks());
        nextTetromino = new Tetromino();
        drawNextPreview();
    }

    private void drawNextPreview() {
        GraphicsContext gc = nextPreview.getGraphicsContext2D();
        gc.clearRect(0, 0, 80, 80);
        gc.setFill(Color.GRAY);
        for (int[] cell : nextTetromino.shape) {
            gc.fillRect((cell[0] + 2) * 15, (cell[1] + 2) * 15, 15, 15);
        }
    }

    private void clearFullLines() {
        int linesCleared = 0;

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
                linesCleared++;
            }
        }

        if (linesCleared > 0) {
            totalLines += linesCleared;
            score += switch (linesCleared) {
                case 1 -> 100;
                case 2 -> 300;
                case 3 -> 600;
                case 4 -> 1000;
                default -> 0;
            };

            linesLabel.setText("Lines Erased: " + totalLines);
            scoreLabel.setText("Score: " + score);
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

    private void gameOver() {
        timeline.stop();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Your score: " + score);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("High Score");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            ScoreManager.add(name, score);
        });

        alert.showAndWait();
        // Stay on play screen, no reset
    }

    // ========= Inner classes =========
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
                {{0, 0}, {1, 0}, {-1, 0}, {0, 1}},
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
                {{0, 0}, {1, 0}, {-1, 0}, {-1, 1}},
                {{0, 0}, {1, 0}, {-1, 0}, {1, 1}},
                {{0, 0}, {1, 0}, {0, 1}, {-1, 1}},
                {{0, 0}, {-1, 0}, {0, 1}, {1, 1}},
                {{0, 0}, {-1, 0}, {1, 0}, {2, 0}}
        };

        public static int[][] getRandomShape() {
            return SHAPES[new Random().nextInt(SHAPES.length)];
        }
    }
}
