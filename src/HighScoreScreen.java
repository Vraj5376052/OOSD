import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class HighScoreScreen extends Application {

    public void start(Stage primaryStage) {

        primaryStage.setTitle("Tetris");

        // Root layout
        VBox root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("High Score");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 24));
        root.getChildren().add(title);

        // GridPane for the table
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Column headers
        Label nameHeader = new Label("Name");
        nameHeader.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        Label scoreHeader = new Label("Score");
        scoreHeader.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));

        grid.add(nameHeader, 0, 0);
        grid.add(scoreHeader, 1, 0);

        // Sample data
        String[][] data = {
                {"Vraj", "869613"},
                {"Tom", "754569"},
                {"Anh", "642871"},
                {"Jack", "549280"},
                {"Luis", "537728"},
                {"Tom", "462740"},
                {"Vraj", "366765"},
                {"Vraj", "326181"},
                {"Luis", "301649"},
                {"Jack", "260598"},
        };
        // Populate GridPane with data
        for (int i = 0; i < data.length; i++) {
            Label name = new Label(data[i][0]);
            name.setFont(Font.font("SansSerif", 14));
            Label score = new Label(data[i][1]);
            score.setFont(Font.font("SansSerif", 14));
            grid.add(name, 0, i + 1);
            grid.add(score, 1, i + 1);
        }

        root.getChildren().add(grid);

        // Back button
        Button backButton = new Button("Return to Menu");
        backButton.setPrefWidth(100);
        root.getChildren().add(backButton);


        // Scene and stage
        Scene scene = new Scene(root, 450, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    }