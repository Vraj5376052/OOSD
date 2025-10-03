package tetrisclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HighScoreScreen {

    //record type usage
    record ScoreEntry(String name, int score) { }

    private HighScoreScreen() { }

    public static Scene create(Runnable onBack) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("High Score");
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

        //update Score entry
        ScoreManager.load();

        int row = 1;
        for (ScoreManager.ScoreEntry entry : ScoreManager.getScores()) {
            Label name = new Label(entry.name());
            Label score = new Label(String.valueOf(entry.score()));
            grid.add(name, 0, row);
            grid.add(score, 1, row);
            row++;
        }


        root.getChildren().add(grid);

        Button backButton = new Button("Return to Menu");
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> { if (onBack != null) onBack.run(); });
        root.getChildren().add(backButton);

        return new Scene(root, 450, 500);
    }
}
