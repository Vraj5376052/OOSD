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
        showSplashScreen(primaryStage);
    }

    private void showSplashScreen(Stage primaryStage) {
        Label splashText = new Label("Tetris Game, Group 45, 2006ICT / 2805ICT / 3815ICT");
        splashText.setStyle("-fx-font-size: 24px; -fx-text-alignment: center;");

        StackPane root = new StackPane(splashText);
        root.setStyle("-fx-background-color: gray; -fx-alignment: center;");

        Scene splashScene = new Scene(root, 600, 300);

        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(splashScene);
        splashStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            splashStage.close();
            showMainMenu(primaryStage);
        });
        delay.play();
    }

    private void showMainMenu(Stage primaryStage) {
        Button btnConfigure = new Button("Configure");
        Button btnPlay = new Button("Play");
        Button btnHighScore = new Button("High Scores");
        Button btnExit = new Button("Exit");

        btnConfigure.setOnAction(e -> showConfigScreen(primaryStage));
        btnPlay.setOnAction(e -> showPlayScreen(primaryStage));
        btnHighScore.setOnAction(e -> showHighScoreScreen(primaryStage));
        btnExit.setOnAction(e -> primaryStage.close());

        VBox mainLayout = new VBox(20, btnConfigure, btnPlay, btnHighScore, btnExit);
        mainLayout.setAlignment(Pos.CENTER);
        homeScene = new Scene(mainLayout, 400, 400);

        primaryStage.setTitle("Tetris Main Menu");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private void showConfigScreen(Stage stage) {
        Label widthValue = new Label(String.valueOf(COLUMNS));
        Label heightValue = new Label(String.valueOf(ROWS));
        Label LEVELValue = new Label(String.valueOf(LEVEL));

        Slider widthSlider = new Slider(5, 15, COLUMNS);
        widthSlider.setMajorTickUnit(1);
        widthSlider.setSnapToTicks(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            COLUMNS = newVal.intValue();
            widthValue.setText(String.valueOf(COLUMNS));
        });

        Slider heightSlider = new Slider(15, 30, ROWS);
        heightSlider.setMajorTickUnit(1);
        heightSlider.setSnapToTicks(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setShowTickLabels(true);
        heightSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            ROWS = newVal.intValue();
            heightValue.setText(String.valueOf(ROWS));
        });

        Slider LEVELSlider = new Slider(3, 10, LEVEL);
        LEVELSlider.setMajorTickUnit(1);
        LEVELSlider.setSnapToTicks(true);
        LEVELSlider.setShowTickMarks(true);
        LEVELSlider.setShowTickLabels(true);
        LEVELSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            LEVEL = newVal.intValue();
            LEVELValue.setText(String.valueOf(LEVEL));
        });

        Label musicValue = new Label(MUSIC ? "ON" : "OFF");
        CheckBox musicCheck = new CheckBox("MUSIC");
        musicCheck.setSelected(MUSIC);
        musicCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            MUSIC = newVal;
            musicValue.setText(MUSIC ? "ON" : "OFF");
        });

        Label soundValue = new Label(SOUND_EFFECTS ? "ON" : "OFF");
        CheckBox soundCheck = new CheckBox("SOUND EFFECT");
        soundCheck.setSelected(SOUND_EFFECTS);
        soundCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            SOUND_EFFECTS = newVal;
            soundValue.setText(SOUND_EFFECTS ? "ON" : "OFF");
        });

        Label aiValue = new Label(AI_PLAY ? "ON" : "OFF");
        CheckBox aiCheck = new CheckBox("AI PLAY");
        aiCheck.setSelected(AI_PLAY);
        aiCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            AI_PLAY = newVal;
            aiValue.setText(AI_PLAY ? "ON" : "OFF");
        });

        Label extendValue = new Label(EXTEND_MODE ? "ON" : "OFF");
        CheckBox extendCheck = new CheckBox("EXTEND MODE");
        extendCheck.setSelected(EXTEND_MODE);
        extendCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            EXTEND_MODE = newVal;
            extendValue.setText(EXTEND_MODE ? "ON" : "OFF");
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(homeScene));

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("FIELD WIDTH:"), 0, 0);
        grid.add(widthSlider, 1, 0);
        grid.add(widthValue, 2, 0);

        grid.add(new Label("FIELD HEIGHT:"), 0, 1);
        grid.add(heightSlider, 1, 1);
        grid.add(heightValue, 2, 1);

        grid.add(new Label("GAME LEVEL:"), 0, 2);
        grid.add(LEVELSlider, 1, 2);
        grid.add(LEVELValue, 2, 2);

        grid.add(musicCheck, 0, 3);
        grid.add(musicValue, 2, 3);

        grid.add(soundCheck, 0, 4);
        grid.add(soundValue, 2, 4);

        grid.add(aiCheck, 0, 5);
        grid.add(aiValue, 2, 5);

        grid.add(extendCheck, 0, 6);
        grid.add(extendValue, 2, 6);

        grid.add(backButton, 0, 7, 3, 1);
        GridPane.setHalignment(backButton, HPos.CENTER);

        Scene configScene = new Scene(grid, 450, 400);
        stage.setTitle("Configuration");
        stage.setScene(configScene);
    }

    private void showHighScoreScreen(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("High Scores");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 24));
        root.getChildren().add(title);

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label nameHeader = new Label("Name");
        nameHeader.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        Label scoreHeader = new Label("Score");
        scoreHeader.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));

        grid.add(nameHeader, 0, 0);
        grid.add(scoreHeader, 1, 0);

        String[][] data = {
                {"Tom", "869613"}, {"Vraj", "754569"}, {"Anh", "642871"},
                {"Jack", "549280"}, {"Luis", "537728"}, {"Anna", "462740"},
                {"Larry", "366765"}, {"Alice", "326181"}, {"Spiderman", "301649"},
                {"Chris", "260598"},
        };

        for (int i = 0; i < data.length; i++) {
            Label name = new Label(data[i][0]);
            name.setFont(Font.font("SansSerif", 14));
            Label score = new Label(data[i][1]);
            score.setFont(Font.font("SansSerif", 14));
            grid.add(name, 0, i + 1);
            grid.add(score, 1, i + 1);
        }

        root.getChildren().add(grid);

        Button backButton = new Button("Back");
        backButton.setPrefWidth(100);
        backButton.setOnAction(e -> stage.setScene(homeScene));
        root.getChildren().add(backButton);

        Scene scene = new Scene(root, 450, 500);
        stage.setTitle("High Scores");
        stage.setScene(scene);
    }

    private void showPlayScreen(Stage stage) {
        lockedBlocks.clear();

        gamePane = new Pane();
        gamePane.setPrefSize(COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);

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

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> moveDown()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        playScene.setOnKeyPressed(e -> {
            if (currentTetromino == null) return;
            if (e.getCode() == KeyCode.LEFT) currentTetromino.move(-1, 0);
            else if (e.getCode() == KeyCode.RIGHT) currentTetromino.move(1, 0);
            else if (e.getCode() == KeyCode.DOWN) moveDown();
            else if (e.getCode() == KeyCode.UP) currentTetromino.rotate();
        });

        gamePane.requestFocus();
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
