package src.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.application.Platform;

public class MainMenuController {

    @FXML
    private Button playButton;

    @FXML
    private Button configButton;

    @FXML
    private Button scoreButton;

    @FXML
    private Button exitButton;

    @FXML
    private void handlePlay() {
        System.out.println("Play clicked - Launch game screen");
        // TODO: Load gameplay scene
    }

    @FXML
    private void handleConfig() {
        System.out.println("Configuration clicked - Open configuration screen");
        // TODO: Load configuration screen
    }

    @FXML
    private void handleHighScores() {
        System.out.println("High Scores clicked - Show scores");
        // TODO: Load high score screen
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
}
