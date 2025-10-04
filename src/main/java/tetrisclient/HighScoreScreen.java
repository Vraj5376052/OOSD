package tetrisclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HighScoreScreen {

    // Record to represent each score entry
    record ScoreEntry(String name, int score) { }

    private HighScoreScreen() { }

    public static Scene create(Runnable onBack) {
        // Main container VBox to center content
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);  // Center the content vertically
        root.setSpacing(20);  // Spacing between components

        // GridPane for the high scores, with headers fixed at the top
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER); // Center the grid content

        // Create the headers for the high score table
        Label nameHeader = new Label("Name");
        Label scoreHeader = new Label("Score");
        nameHeader.setStyle("-fx-font-weight: bold;");
        scoreHeader.setStyle("-fx-font-weight: bold;");

        // Add headers at the top of the grid
        grid.add(nameHeader, 0, 0);
        grid.add(scoreHeader, 1, 0);

        // Populate the grid with high scores
        int row = 1;
        for (ScoreManager.ScoreEntry entry : ScoreManager.getScores()) {
            grid.add(new Label(entry.name()), 0, row);
            grid.add(new Label(String.valueOf(entry.score())), 1, row);
            row++;
        }

        // Create "Clear Scores" button
        Button clearButton = new Button("Clear Scores");
        clearButton.setOnAction(e -> {
            // Clear the high scores
            ScoreManager.clear();
            // Refresh the grid after clearing the scores
            grid.getChildren().clear();
            // Rebuild the grid with headers
            grid.add(nameHeader, 0, 0);
            grid.add(scoreHeader, 1, 0);

            // If the list is empty, display a message
            int newRow = 1;
            if (ScoreManager.getScores().isEmpty()) {
                grid.add(new Label("No scores to display."), 0, newRow, 2, 1);
            } else {
                // Populate the grid with the updated scores
                for (ScoreManager.ScoreEntry entry : ScoreManager.getScores()) {
                    grid.add(new Label(entry.name()), 0, newRow);
                    grid.add(new Label(String.valueOf(entry.score())), 1, newRow);
                    newRow++;
                }
            }
        });

        // Create "Back" button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        // VBox to hold buttons at the bottom
        VBox buttonsBox = new VBox(10, clearButton, backButton);
        buttonsBox.setAlignment(Pos.CENTER);  // Center buttons

        // Add the grid and buttons to the root VBox
        root.getChildren().addAll(grid, buttonsBox);

        return new Scene(root, 450, 500);
    }
}
