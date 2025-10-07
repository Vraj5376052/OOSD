package tetrisclient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
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
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayScreen {
    // Tetris grid
    private final int COLUMNS;
    private final int ROWS;
    private final int CELL_SIZE;

    // The root container
    private BorderPane root;
    public Parent getSceneRoot() { return root; }

    // AI check
    public boolean isAIEnabled() { return aiEnabled; }

    // Game elements
    private Pane gamePane;
    private Timeline timeline;
    private Tetromino currentTetromino;
    private Tetromino nextTetromino;
    private List<Rectangle> lockedBlocks = new ArrayList<>();
    private Scene playScene;

    // Game score and line information
    private int score = 0;
    private int totalLines = 0;
    private int level = 1;

    // UI elements
    private Label scoreLabel;
    private Label linesLabel;
    private Label levelLabel;
    private Canvas nextPreview;

    // AI related flags and functionality
    private boolean aiEnabled;
    private boolean sharedTetromino = false;
    private TetrisAI ai;

    // Initialize PlayScreen
    public PlayScreen(int columns, int rows, int cellSize) {
        this(columns, rows, cellSize, false);
    }

    public PlayScreen(int columns, int rows, int cellSize, boolean sharedTetromino) {
        this.COLUMNS = columns;
        this.ROWS = rows;
        this.CELL_SIZE = cellSize;
        this.sharedTetromino = sharedTetromino;
    }

    // Getter methods for grid properties
    public int getColumns() { return COLUMNS; }
    public int getRows() { return ROWS; }
    public int getCellSize() { return CELL_SIZE; }
    public List<Rectangle> getLockedBlocks() { return lockedBlocks; }
    public Scene getScene() { return playScene; }

    // Generates the game board
    public int[][] getBoard() {
        int[][] board = new int[ROWS][COLUMNS];
        for (Rectangle r : lockedBlocks) {
            int row = (int) ((r.getY() - 1) / CELL_SIZE);
            int col = (int) (r.getX() / CELL_SIZE);
            if (row >= 0 && row < ROWS && col >= 0 && col < COLUMNS)
                board[row][col] = 1;
        }
        return board;
    }

    // preview next Tetromino
    public int[][] getNextTetrominoShape() {
        if (nextTetromino != null)
            return nextTetromino.getShape();
        return new int[0][0];
    }

    // Initialize and show the game scene
    public void show(Stage stage, Runnable onBack, boolean aiPlay, boolean attachToStage) {
        this.aiEnabled = aiPlay;
        if (aiPlay) {
            ai = new TetrisAI(this);
            ai.start();
        }

        // Reset game elements
        lockedBlocks.clear();
        score = 0;
        totalLines = 0;
        level = 1;

        // Create game area
        gamePane = new Pane();
        gamePane.setPrefSize(COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);

        // Add grid
        Canvas gridCanvas = new Canvas(COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);
        drawGrid(gridCanvas.getGraphicsContext2D());
        gamePane.getChildren().add(gridCanvas);

        // Add frame
        Rectangle frame = new Rectangle(COLUMNS * CELL_SIZE + 2, ROWS * CELL_SIZE + 2);
        frame.setFill(Color.TRANSPARENT);
        frame.setStroke(Color.SILVER);

        // Pause hint label
        Label pauseHint = new Label("Game is paused,\nPress 'P' to continue...");
        pauseHint.setVisible(false);

        // Play area layout
        StackPane playArea = new StackPane();
        playArea.setAlignment(Pos.TOP_LEFT);
        playArea.getChildren().addAll(new Group(frame), gamePane, pauseHint);

        // Info box layout
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.TOP_LEFT);

        Label infoTitle = new Label("Game Info");
        Label playerType = new Label("Player Type: " + (aiPlay ? "AI" : "Human"));
        linesLabel = new Label("Lines Erased: 0");
        scoreLabel = new Label("Score: 0");
        levelLabel = new Label("Level: 1");
        Label nextLabel = new Label("Next Tetromino:");
        nextPreview = new Canvas(80, 80);

        infoBox.getChildren().addAll(infoTitle, playerType, linesLabel, scoreLabel, nextLabel, nextPreview);

        // Layout for the whole screen
        HBox center = new HBox(20, infoBox, playArea);
        center.setAlignment(Pos.CENTER);

        // Title and top section of the game
        Label title = new Label("Play");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        HBox top = new HBox(title);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(8, 0, 4, 0));

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Stop Game?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    if (timeline != null) timeline.stop();
                    onBack.run();
                }
            });
        });

        // Bottom section
        HBox bottom = new HBox(backButton);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(6, 0, 8, 0));

        // Main root layout of the game screen
        root = new BorderPane();
        root.setTop(top);
        root.setCenter(center);
        root.setBottom(bottom);

        // Create the scene and set it to the stage
        playScene = new Scene(root, COLUMNS * CELL_SIZE + 250, ROWS * CELL_SIZE + 180);

        if (attachToStage) {
            stage.setTitle("Tetris");
            stage.setScene(playScene);
        }

        // Start the game by spawning the first tetromino
        spawnTetromino();

        // Timeline for the game loop
        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            moveDown();
            if (!aiEnabled) {
                new Thread(() -> {
                    try {
                        TetrisClient.sendGameState(this);
                    } catch (Exception ex) {
                        System.err.println("External sync failed: " + ex.getMessage());
                    }
                }).start();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Pause functionality
        final BooleanProperty paused = new SimpleBooleanProperty(false);
        playScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                boolean p = !paused.get();
                paused.set(p);
                if (p) timeline.pause(); else timeline.play();
                pauseHint.setVisible(p);
                return;
            }
            if (paused.get() || currentTetromino == null || aiEnabled) return;
            switch (e.getCode()) {
                case LEFT -> currentTetromino.move(-1, 0);
                case RIGHT -> currentTetromino.move(1, 0);
                case DOWN -> moveDown();
                case UP -> currentTetromino.rotate();
            }
        });

        playScene.getRoot().requestFocus();
    }

    // Draws the grid lines
    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        for (int x = 0; x <= COLUMNS * CELL_SIZE; x += CELL_SIZE)
            gc.strokeLine(x + 0.5, 0.5, x + 0.5, ROWS * CELL_SIZE + 0.5);
        for (int y = 0; y <= ROWS * CELL_SIZE; y += CELL_SIZE)
            gc.strokeLine(0.5, y + 0.5, COLUMNS * CELL_SIZE + 0.5, y + 0.5);
    }

    // Move the current tetromino downwards
    public void moveDown() {
        if (currentTetromino != null) {
            boolean moved = currentTetromino.move(0, 1);
            if (!moved) {
                AudioManager.playPieceFallSound();
                lockedBlocks.addAll(currentTetromino.getBlocks());
                currentTetromino = null;
                clearFullLines();
                if (sharedTetromino) SameTetromino.nextRound();
                spawnTetromino();
            }
        }
    }

    // Method to spawn a new tetromino
    private void spawnTetromino() {
        System.out.println("[PlayScreen] Shared next shape: " + java.util.Arrays.deepToString(SameTetromino.getSharedShape()));

        if (nextTetromino == null) {
            if (initialShape != null) {
                nextTetromino = new Tetromino(initialShape);
                initialShape = null; // use only once
            } else if (sharedTetromino) {
                nextTetromino = new Tetromino(SameTetromino.getSharedShape());
            } else {
                nextTetromino = new Tetromino(sharedTetromino);
            }
        }

        currentTetromino = nextTetromino;

        if (currentTetromino.isColliding()) {
            gameOver();
            return;
        }

        gamePane.getChildren().addAll(currentTetromino.getBlocks());


        if (sharedTetromino) {
            nextTetromino = new Tetromino(SameTetromino.getSharedShape());
        } else {
            nextTetromino = new Tetromino(sharedTetromino);
        }

        drawNextPreview();
    }

    // Draw the preview of the next tetromino
    private void drawNextPreview() {
        GraphicsContext gc = nextPreview.getGraphicsContext2D();
        gc.clearRect(0, 0, 80, 80);
        gc.setFill(Color.GRAY);
        for (int[] cell : nextTetromino.getShape()) {
            gc.fillRect((cell[0] + 2) * 15, (cell[1] + 2) * 15, 15, 15);
        }
    }

    // Clear full lines from the board
    public void clearFullLines() {
        int linesCleared = 0;
        for (int row = ROWS - 1; row >= 0; row--) {
            int blocksInRow = 0;
            for (int col = 0; col < COLUMNS; col++) {
                boolean blockFound = false;
                for (Rectangle r : lockedBlocks) {
                    int blockRow = (int) ((r.getY() - 1) / CELL_SIZE);
                    int blockCol = (int) (r.getX() / CELL_SIZE);
                    if (blockRow == row && blockCol == col) { blockFound = true; break; }
                }
                if (blockFound) blocksInRow++;
            }
            if (blocksInRow == COLUMNS) {
                removeRow(row);
                row++;
                linesCleared++;
                AudioManager.playLineSound();
            }
        }
        // Update score, level and lines
        if (linesCleared > 0) {
            totalLines += linesCleared;
            level += 1;
            score += switch (linesCleared) {
                case 1 -> 150; case 2 -> 300; case 3 -> 400; case 4 -> 500; default -> 0;
            };
            linesLabel.setText("Lines Erased: " + totalLines);
            scoreLabel.setText("Score: " + score);
            levelLabel.setText("Level: " + level);
        }
    }

    private int[][] initialShape = null;

    public void setInitialShape(int[][] shape) {
        this.initialShape = shape;
    }

    // remove a row
    private void removeRow(int rowToRemove) {
        List<Rectangle> toRemove = new ArrayList<>();
        List<Rectangle> toMoveDown = new ArrayList<>();
        for (Rectangle r : lockedBlocks) {
            int blockRow = (int) ((r.getY() - 1) / CELL_SIZE);
            if (blockRow == rowToRemove) toRemove.add(r);
            else if (blockRow < rowToRemove) toMoveDown.add(r);
        }
        for (Rectangle r : toRemove) { gamePane.getChildren().remove(r); lockedBlocks.remove(r); }
        for (Rectangle r : toMoveDown) r.setY(r.getY() + CELL_SIZE);
    }

    // End the game
    private void gameOver() {
        timeline.stop();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Your score: " + score);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("High Score");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> ScoreManager.add(name, score));
        alert.showAndWait();
    }

    public Tetromino getCurrentTetromino() { return currentTetromino; }

    // Tetromino class represents each falling block
    public class Tetromino {
        private Rectangle[] squares = new Rectangle[4];
        private int[][] shape;
        private int x = COLUMNS / 2 - 1;
        private int y = 0;

        Tetromino(int[][] predefinedShape) {
            shape = predefinedShape;
            Color color = Color.color(Math.random(), Math.random(), Math.random());
            for (int i = 0; i < 4; i++) {
                squares[i] = new Rectangle(CELL_SIZE, CELL_SIZE);
                squares[i].setFill(color);
                squares[i].setStroke(Color.BLACK);
            }
            updatePositions();
        }

        // Create a tetromino with random shape
        Tetromino(boolean shared) {
            shape = TetrominoShapes.getRandomShape(shared);
            Color color = Color.color(Math.random(), Math.random(), Math.random());
            for (int i = 0; i < 4; i++) {
                squares[i] = new Rectangle(CELL_SIZE, CELL_SIZE);
                squares[i].setFill(color);
                squares[i].setStroke(Color.BLACK);
            }
            updatePositions();
        }

        // Move the tetromino
        boolean move(int dx, int dy) {
            x += dx; y += dy;
            if (isOutOfBounds() || isColliding()) { x -= dx; y -= dy; return false; }
            updatePositions(); return true;
        }

        // Rotate the tetromino
        void rotate() {
            int[][] rotated = new int[4][2];
            for (int i = 0; i < 4; i++) { rotated[i][0] = -shape[i][1]; rotated[i][1] = shape[i][0]; }
            int[][] original = shape;
            shape = rotated;
            if (isOutOfBounds() || isColliding()) shape = original;
            else updatePositions();
        }

        // Update the positions
        void updatePositions() {
            for (int i = 0; i < 4; i++) {
                squares[i].setX((x + shape[i][0]) * CELL_SIZE);
                squares[i].setY((y + shape[i][1]) * CELL_SIZE + 1);
            }
        }

        // Check if the tetromino is out of bounds
        boolean isOutOfBounds() {
            for (int i = 0; i < 4; i++) {
                int newX = x + shape[i][0]; int newY = y + shape[i][1];
                if (newX < 0 || newX >= COLUMNS || newY >= ROWS || newY < 0) return true;
            }
            return false;
        }

        // Check if the tetromino is colliding
        boolean isColliding() {
            for (Rectangle block : lockedBlocks) {
                for (int i = 0; i < 4; i++) {
                    int blockX = (int) (block.getX() / CELL_SIZE);
                    int blockY = (int) ((block.getY() - 1) / CELL_SIZE);
                    int newX = x + shape[i][0];
                    int newY = y + shape[i][1];
                    if (blockX == newX && blockY == newY) return true;
                }
            }
            return false;
        }

        // Get the blocks for this tetromino
        List<Rectangle> getBlocks() {
            List<Rectangle> list = new ArrayList<>();
            for (Rectangle r : squares) list.add(r);
            return list;
        }

        // Get the shape of the tetromino
        public int[][] getShape() { return shape; }

        // Clone the tetromino
        public Tetromino cloneTetromino() {
            Tetromino copy = new Tetromino(sharedTetromino);
            copy.x = this.x;
            copy.y = this.y;
            copy.shape = new int[4][2];
            for (int i = 0; i < 4; i++) {
                copy.shape[i][0] = this.shape[i][0];
                copy.shape[i][1] = this.shape[i][1];
            }
            return copy;

        }
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    // Manage tetromino shapes
    static class TetrominoShapes {
        private static final int[][][] SHAPES = {
                {{0,0},{1,0},{-1,0},{0,1}},
                {{0,0},{1,0},{0,1},{1,1}},
                {{0,0},{1,0},{-1,0},{-1,1}},
                {{0,0},{1,0},{-1,0},{1,1}},
                {{0,0},{1,0},{0,1},{-1,1}},
                {{0,0},{-1,0},{0,1},{1,1}},
                {{0,0},{-1,0},{1,0},{2,0}}
        };

        public static int[][] getRandomShape(boolean shared) {
            if (shared)
                return SameTetromino.getSharedShape();
            else {
                return SHAPES[new Random().nextInt(SHAPES.length)];
            }
        }

    }
}
