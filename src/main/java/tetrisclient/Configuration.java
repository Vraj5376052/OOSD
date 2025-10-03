package tetrisclient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Configuration {

    public static void show(Stage stage, Runnable onBack) {
        // Labels for displaying current values
        Label widthValue = new Label(String.valueOf(Main.getColumns()));
        Label heightValue = new Label(String.valueOf(Main.getRows()));
        Label levelValue = new Label(String.valueOf(Main.getLEVEL()));
        Label musicValue = new Label(Main.isMUSIC() ? "ON" : "OFF");
        Label soundValue = new Label(Main.isSOUND_EFFECTS() ? "ON" : "OFF");
        Label aiValue = new Label(Main.isAI_PLAY() ? "ON" : "OFF");
        Label extendValue = new Label(Main.isEXTEND_MODE() ? "ON" : "OFF");

        // Sliders
        Slider widthSlider = new Slider(5, 15, Main.getColumns());
        widthSlider.setMajorTickUnit(1);
        widthSlider.setSnapToTicks(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Main.setColumns(newVal.intValue());
            widthValue.setText(String.valueOf(Main.getColumns()));
        });

        Slider heightSlider = new Slider(15, 30, Main.getRows());
        heightSlider.setMajorTickUnit(1);
        heightSlider.setSnapToTicks(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setShowTickLabels(true);
        heightSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Main.setRows(newVal.intValue());
            heightValue.setText(String.valueOf(Main.getRows()));
        });

        Slider levelSlider = new Slider(1, 10, Main.getLEVEL());
        levelSlider.setMajorTickUnit(1);
        levelSlider.setSnapToTicks(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setShowTickLabels(true);
        levelSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Main.setLEVEL(newVal.intValue());
            levelValue.setText(String.valueOf(Main.getLEVEL()));
        });

        // Checkboxes
        CheckBox musicCheck = new CheckBox();
        musicCheck.setSelected(Main.isMUSIC());
        musicCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Main.setMUSIC(newVal);
            musicValue.setText(newVal ? "ON" : "OFF");
            if(newVal) AudioManager.playBackgroundMusic();
            else AudioManager.stopBackgroundMusic();
        });

        CheckBox soundCheck = new CheckBox();
        soundCheck.setSelected(Main.isSOUND_EFFECTS());
        soundCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Main.setSOUND_EFFECTS(newVal);
            soundValue.setText(newVal ? "ON" : "OFF");
        });

        CheckBox aiCheck = new CheckBox();
        aiCheck.setSelected(Main.isAI_PLAY());
        aiCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Main.setAI_PLAY(newVal);
            aiValue.setText(newVal ? "ON" : "OFF");
        });

        CheckBox extendCheck = new CheckBox();
        extendCheck.setSelected(Main.isEXTEND_MODE());
        extendCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Main.setEXTEND_MODE(newVal);
            extendValue.setText(newVal ? "ON" : "OFF");
        });

        // Grid layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Field Width:"), 0, 0);
        grid.add(widthSlider, 1, 0);
        grid.add(widthValue, 2, 0);

        grid.add(new Label("Field Height:"), 0, 1);
        grid.add(heightSlider, 1, 1);
        grid.add(heightValue, 2, 1);

        grid.add(new Label("Game Level:"), 0, 2);
        grid.add(levelSlider, 1, 2);
        grid.add(levelValue, 2, 2);

        grid.add(new Label("Music:"), 0, 3);
        grid.add(musicCheck, 1, 3);
        grid.add(musicValue, 2, 3);

        grid.add(new Label("Sound Effect:"), 0, 4);
        grid.add(soundCheck, 1, 4);
        grid.add(soundValue, 2, 4);

        grid.add(new Label("AI Play:"), 0, 5);
        grid.add(aiCheck, 1, 5);
        grid.add(aiValue, 2, 5);

        grid.add(new Label("External Player:"), 0, 6);
        grid.add(extendCheck, 1, 6);
        grid.add(extendValue, 2, 6);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> onBack.run());
        grid.add(backButton, 0, 8, 3, 1);
        GridPane.setHalignment(backButton, javafx.geometry.HPos.CENTER);

        // Title
        Label title = new Label("Configuration");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        HBox top = new HBox(title);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));

        // Layout
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(grid);

        Scene configScene = new Scene(root, 500, 400);
        stage.setTitle("Configuration");
        stage.setScene(configScene);
    }
}
