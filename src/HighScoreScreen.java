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

        //record type usage in scores
        ScoreEntry[] data = {
                new ScoreEntry("Tom", 869613),
                new ScoreEntry("Vraj", 754569),
                new ScoreEntry("Anh", 642871),
                new ScoreEntry("Jack", 549280),
                new ScoreEntry("Luis", 537728),
                new ScoreEntry("Tom", 462740),
                new ScoreEntry("Vraj", 366765),
                new ScoreEntry("Vraj", 326181),
                new ScoreEntry("Luis", 301649),
                new ScoreEntry("Jack", 260598),
        };

        for (int i = 0; i < data.length; i++) {
            Label name = new Label(data[i].name());
            name.setFont(Font.font("SansSerif", 14));
            Label score = new Label(String.valueOf(data[i].score()));
            score.setFont(Font.font("SansSerif", 14));
            grid.add(name, 0, i + 1);
            grid.add(score, 1, i + 1);
        }


        root.getChildren().add(grid);

        Button backButton = new Button("Return to Menu");
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> { if (onBack != null) onBack.run(); });
        root.getChildren().add(backButton);

        return new Scene(root, 450, 500);
    }
}
