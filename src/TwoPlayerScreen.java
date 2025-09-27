import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//vraj fix
import javafx.scene.Parent;


public class TwoPlayerScreen {


    private final PlayScreen player1Screen;
    private final PlayScreen player2Screen;
    private Scene scene;
    private Label player1Score;
    private Label player2Score;

    public TwoPlayerScreen(int cols, int rows, int cellSize) {
        player1Screen = new PlayScreen(cols, rows, cellSize);
        player2Screen = new PlayScreen(cols, rows, cellSize);
    }

    public void show(Stage stage, boolean player2AI) {
        // Create score panels
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        player1Score = new Label("Player 1 Score: 0");
        leftPanel.getChildren().add(player1Score);

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        player2Score = new Label("Player 2 Score: 0");
        rightPanel.getChildren().add(player2Score);

        // Build root layout
//        HBox center = new HBox(40);
//        center.setAlignment(Pos.CENTER);
//        center.getChildren().addAll(
//                player1Screen.getSceneRoot(),
//                player2Screen.getSceneRoot()
//        );

        //vraj fix
        player1Screen.show(stage, this::onGameOver, false, false);      // do not attach to stage
        player2Screen.show(stage, this::onGameOver, player2AI, false);  // do not attach to stage

        HBox center = new HBox(40);
        center.setAlignment(Pos.CENTER);

// make sure we don’t add nulls
        Parent p1Root = player1Screen.getSceneRoot();
        Parent p2Root = player2Screen.getSceneRoot();

        if (p1Root != null) center.getChildren().add(p1Root);
        if (p2Root != null) center.getChildren().add(p2Root);
//end vraj fix
        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(center);
        root.setRight(rightPanel);

        scene = new Scene(root,
                player1Screen.getColumns() * player1Screen.getCellSize() * 2 + 400,
                player1Screen.getRows() * player1Screen.getCellSize() + 200

        );

        stage.setTitle("Two Player Tetris");
        stage.setScene(scene);

        // Launch both play screens
//        player1Screen.show(stage, this::onGameOver, false);      // Player 1: human
//        player2Screen.show(stage, this::onGameOver, player2AI);  // Player 2: human or AI
//vraj fix

        //vraj fix
        scene.setOnMouseClicked(e -> scene.getRoot().requestFocus());
        scene.getRoot().requestFocus();

        setupControls();
    }

    private void setupControls() {
        scene.setOnKeyPressed(e -> {
            // Player 1 controls (arrow keys)
            if (player1Screen.getCurrentTetromino() != null) {
                switch (e.getCode()) {
                    case LEFT -> player1Screen.getCurrentTetromino().move(-1, 0);
                    case RIGHT -> player1Screen.getCurrentTetromino().move(1, 0);
                    case DOWN -> player1Screen.moveDown();
                    case UP -> player1Screen.getCurrentTetromino().rotate();
                }
            }

            // Player 2 controls (WASD) - only if not AI
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

    private void onGameOver() {
        // Determine who is still alive
        boolean p1Alive = player1Screen.getCurrentTetromino() != null;
        boolean p2Alive = player2Screen.getCurrentTetromino() != null;

        String message;
        if (p1Alive && !p2Alive) {
            message = "Player 1 Wins!";
        } else if (!p1Alive && p2Alive) {
            message = "Player 2 Wins!";
        } else {
            message = "Draw!";
        }

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION,
                message
        );
        alert.setHeaderText("Game Over");
        alert.showAndWait();
    }
}
