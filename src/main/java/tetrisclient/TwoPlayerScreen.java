package tetrisclient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class TwoPlayerScreen {

    private final PlayScreen player1Screen;
    private final PlayScreen player2Screen;
    private Scene scene;
    private Label player1Score;
    private Label player2Score;

    private boolean p1Finished = false;
    private boolean p2Finished = false;

    public TwoPlayerScreen(int cols, int rows, int cellSize) {

        long sharedSeed = System.currentTimeMillis();
        SameTetromino.setSeed(sharedSeed);
        System.out.println("[TwoPlayerScreen] Shared seed set: " + sharedSeed);


        player1Screen = new PlayScreen(cols, rows, cellSize, true);
        player2Screen = new PlayScreen(cols, rows, cellSize, true);


        int[][] firstShape = SameTetromino.getSharedShape();
        System.out.println("[TwoPlayerScreen], First shared shape: " + java.util.Arrays.deepToString(firstShape));
        player1Screen.setInitialShape(firstShape);
        player2Screen.setInitialShape(firstShape);
    }

    public void show(Stage stage, boolean player2AI) {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        player1Score = new Label("Player 1 Score: 0");
        leftPanel.getChildren().add(player1Score);

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        player2Score = new Label("Player 2 Score: 0");
        rightPanel.getChildren().add(player2Score);

        player1Screen.show(stage, () -> markPlayerFinished(1), false, false);
        player2Screen.show(stage, () -> markPlayerFinished(2), player2AI, false);

        HBox center = new HBox(40);
        center.setAlignment(Pos.CENTER);

        Parent p1Root = player1Screen.getSceneRoot();
        Parent p2Root = player2Screen.getSceneRoot();
        if (p1Root != null) center.getChildren().add(p1Root);
        if (p2Root != null) center.getChildren().add(p2Root);

        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(center);
        root.setRight(rightPanel);

        scene = new Scene(
                root,
                player1Screen.getColumns() * player1Screen.getCellSize() * 2 + 400,
                player1Screen.getRows() * player1Screen.getCellSize() + 200
        );

        stage.setTitle("Two Player Tetris");
        stage.setScene(scene);

        scene.setOnMouseClicked(e -> scene.getRoot().requestFocus());
        scene.getRoot().requestFocus();

        setupControls();
    }

    private void setupControls() {
        scene.setOnKeyPressed(e -> {
            if (player1Screen.getCurrentTetromino() != null) {
                switch (e.getCode()) {
                    case LEFT -> player1Screen.getCurrentTetromino().move(-1, 0);
                    case RIGHT -> player1Screen.getCurrentTetromino().move(1, 0);
                    case DOWN -> player1Screen.moveDown();
                    case UP -> player1Screen.getCurrentTetromino().rotate();
                }
            }

            if (!player2Screen.isAIEnabled() && player2Screen.getCurrentTetromino() != null) {
                switch (e.getCode()) {
                    case A -> player2Screen.getCurrentTetromino().move(-1, 0);
                    case D -> player2Screen.getCurrentTetromino().move(1, 0);
                    case S -> player2Screen.moveDown();
                    case W -> player2Screen.getCurrentTetromino().rotate();
                }
            }
        });
    }

    private void markPlayerFinished(int player) {
        if (player == 1) p1Finished = true;
        else if (player == 2) p2Finished = true;

        if (p1Finished && p2Finished) {
            endGame();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Player " + player + " has finished.\nWaiting for the other player...");
            alert.setHeaderText("Game Paused for Comparison");
            alert.showAndWait();
        }
    }

    private void endGame() {
        int score1 = getScore(player1Screen);
        int score2 = getScore(player2Screen);

        String message;
        if (score1 > score2) {
            message = "Player 1 Wins! (" + score1 + " vs " + score2 + ")";
        } else if (score2 > score1) {
            message = "Player 2 Wins! (" + score2 + " vs " + score1 + ")";
        } else {
            message = "Draw! (Both scored " + score1 + ")";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText("Game Over");
        alert.showAndWait();

        Stage stage = (Stage) scene.getWindow();
        if (stage != null) {
            try {
                new Main().start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getScore(PlayScreen screen) {
        try {
            var field = PlayScreen.class.getDeclaredField("score");
            field.setAccessible(true);
            return (int) field.get(screen);
        } catch (Exception e) {
            return 0;
        }
    }
}
