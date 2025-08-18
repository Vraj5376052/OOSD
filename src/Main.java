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
        splashStage.centerOnScreen();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            splashStage.close();
            showMainMenu(primaryStage);
        });
        delay.play();
    }

    private void showMainMenu(Stage primaryStage) {
        Label title = new Label("Main Menu");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        Button btnPlay = new Button("Play");
        Button btnConfig = new Button("Configuration"); // fix typo
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
        // live value labels
        Label widthValue = new Label(String.valueOf(COLUMNS));
        Label heightValue = new Label(String.valueOf(ROWS));
        Label levelValue = new Label(String.valueOf(LEVEL));
        Label musicValue = new Label(MUSIC ? "On" : "Off");
        Label soundValue = new Label(SOUND_EFFECTS ? "On" : "Off");
        Label aiValue = new Label(AI_PLAY ? "On" : "Off");
        Label extendValue = new Label(EXTEND_MODE ? "On" : "Off");

        // sliders
        Slider widthSlider = new Slider(5, 15, COLUMNS);
        widthSlider.setMajorTickUnit(1); widthSlider.setSnapToTicks(true);
        widthSlider.setShowTickMarks(true); widthSlider.setShowTickLabels(true);
        widthSlider.valueProperty().addListener((o,ov,nv)->{ COLUMNS = nv.intValue(); widthValue.setText(""+COLUMNS); });

        Slider heightSlider = new Slider(15, 30, ROWS);
        heightSlider.setMajorTickUnit(1); heightSlider.setSnapToTicks(true);
        heightSlider.setShowTickMarks(true); heightSlider.setShowTickLabels(true);
        heightSlider.valueProperty().addListener((o,ov,nv)->{ ROWS = nv.intValue(); heightValue.setText(""+ROWS); });

        Slider levelSlider = new Slider(1, 10, LEVEL);
        levelSlider.setMajorTickUnit(1); levelSlider.setSnapToTicks(true);
        levelSlider.setShowTickMarks(true); levelSlider.setShowTickLabels(true);
        levelSlider.valueProperty().addListener((o,ov,nv)->{ LEVEL = nv.intValue(); levelValue.setText(""+LEVEL); });

        // checkboxes
        CheckBox musicCheck = new CheckBox();
        musicCheck.setSelected(MUSIC);
        musicCheck.selectedProperty().addListener((o,ov,nv)->{ MUSIC = nv; musicValue.setText(nv?"On":"Off"); });

        CheckBox soundCheck = new CheckBox();
        soundCheck.setSelected(SOUND_EFFECTS);
        soundCheck.selectedProperty().addListener((o,ov,nv)->{ SOUND_EFFECTS = nv; soundValue.setText(nv?"On":"Off"); });

        CheckBox aiCheck = new CheckBox();
        aiCheck.setSelected(AI_PLAY);
        aiCheck.selectedProperty().addListener((o,ov,nv)->{ AI_PLAY = nv; aiValue.setText(nv?"On":"Off"); });

        CheckBox extendCheck = new CheckBox();
        extendCheck.setSelected(EXTEND_MODE);
        extendCheck.selectedProperty().addListener((o,ov,nv)->{ EXTEND_MODE = nv; extendValue.setText(nv?"On":"Off"); });

        // grid
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(14); grid.setAlignment(Pos.CENTER);
        ColumnConstraints c0 = new ColumnConstraints(); c0.setPrefWidth(220);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(60);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPrefWidth(60);
        grid.getColumnConstraints().addAll(c0, c1, c2);

        grid.add(new Label("Field Width (No of cells):"), 0, 0); grid.add(widthSlider, 1, 0); grid.add(widthValue, 2, 0);
        grid.add(new Label("Field Height (No of cells):"),0, 1); grid.add(heightSlider,1, 1); grid.add(heightValue,2, 1);
        grid.add(new Label("Game Level:"),                 0, 2); grid.add(levelSlider, 1, 2); grid.add(levelValue, 2, 2);

        grid.add(new Label("Music (On/Off):"),            0, 3); grid.add(musicCheck, 1, 3); grid.add(musicValue, 2, 3);
        grid.add(new Label("Sound Effect (On/Off):"),     0, 4); grid.add(soundCheck,1, 4); grid.add(soundValue,2, 4);
        grid.add(new Label("AI Play (On/Off):"),          0, 5); grid.add(aiCheck,   1, 5); grid.add(aiValue,   2, 5);
        grid.add(new Label("Extend Mode (On/Off):"),      0, 6); grid.add(extendCheck,1,6); grid.add(extendValue,2,6);

        Label title = new Label("Configuration");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        HBox top = new HBox(title); top.setAlignment(Pos.CENTER); top.setPadding(new Insets(10,0,10,0));

        Button back = new Button("Back");
        back.setOnAction(e -> stage.setScene(homeScene));
        HBox bottom = new HBox(back); bottom.setAlignment(Pos.CENTER); bottom.setPadding(new Insets(8,0,8,0));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(grid);
        root.setBottom(bottom);

        stage.setTitle("Tetris");
        stage.setScene(new Scene(root, 640, 420));
    }


    private void showHighScoreScreen(Stage stage) {
        Scene scene = HighScoreScreen.create(() -> stage.setScene(homeScene));
        stage.setTitle("High Scores");
        stage.setScene(scene);
    }

    private void showPlayScreen(Stage stage) {
        lockedBlocks.clear();

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
        Label pauseHint = new Label("Game is paused,\npress P to continue...");
        pauseHint.setVisible(false);

        // center game field + frame
        StackPane center = new StackPane(new Group(frame), gamePane, pauseHint);
        center.setPadding(new Insets(10));
        center.setAlignment(Pos.CENTER);

        StackPane.setAlignment(pauseHint, Pos.CENTER_LEFT);
        StackPane.setMargin(pauseHint, new Insets(0, 0, 0, 20));

        // top title
        Label title = new Label("Play");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        HBox top = new HBox(title); top.setAlignment(Pos.CENTER); top.setPadding(new Insets(8,0,4,0));

        // back button with stop confirmation
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Stop Game");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to stop the current game?");
            ButtonType no  = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(no, yes);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == yes) {
                    if (timeline != null) timeline.stop();
                    stage.setScene(homeScene);
                }
            });
        });
        HBox bottom = new HBox(backButton); bottom.setAlignment(Pos.CENTER); bottom.setPadding(new Insets(6,0,8,0));

        // layout
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(center);
        root.setBottom(bottom);

        playScene = new Scene(root, COLUMNS * CELL_SIZE + 160, ROWS * CELL_SIZE + 180);
        stage.setTitle("Tetris");
        stage.setScene(playScene);

        spawnTetromino();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> moveDown()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // pause toggle with P
        final BooleanProperty paused = new SimpleBooleanProperty(false);

        //Control
        playScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                boolean p = !paused.get();
                paused.set(p);
                if (p) { timeline.pause(); pauseHint.setVisible(true); }
                else   { timeline.play();  pauseHint.setVisible(false); }
                return;
            }
            if (paused.get() || currentTetromino == null) return;

            if (e.getCode() == KeyCode.LEFT)      currentTetromino.move(-1, 0);
            else if (e.getCode() == KeyCode.RIGHT) currentTetromino.move(1, 0);
            else if (e.getCode() == KeyCode.DOWN)  moveDown();
            else if (e.getCode() == KeyCode.UP)    currentTetromino.rotate();
        });

        playScene.getRoot().requestFocus();
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