import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Configuration {

    // Fields that mirror Main's settings
    private int columns, rows, level;
    private boolean music, soundEffects, aiPlay, extendMode;

    // Callback to return to main menu
    public static void show(Stage stage, Runnable onBack,
                            int columns, int rows, int level,
                            boolean music, boolean soundEffects,
                            boolean aiPlay, boolean extendMode) {

        Configuration config = new Configuration(columns, rows, level, music, soundEffects, aiPlay, extendMode);
        config.display(stage, onBack);
    }

    private Configuration(int columns, int rows, int level,
                         boolean music, boolean soundEffects,
                         boolean aiPlay, boolean extendMode) {
        this.columns = columns;
        this.rows = rows;
        this.level = level;
        this.music = music;
        this.soundEffects = soundEffects;
        this.aiPlay = aiPlay;
        this.extendMode = extendMode;
    }

    private void display(Stage stage, Runnable onBack) {

        // Labels (sin cambios de lógica)
        Label widthValue = new Label(String.valueOf(columns));
        Label heightValue = new Label(String.valueOf(rows));
        Label levelValue = new Label(String.valueOf(level));
        Label musicValue = new Label(music ? "On" : "Off");
        Label soundValue = new Label(soundEffects ? "On" : "Off");
        Label aiValue = new Label(aiPlay ? "On" : "Off");
        Label extendValue = new Label(extendMode ? "On" : "Off");

        // Sliders (igual)
        Slider widthSlider = new Slider(5, 15, columns);
        widthSlider.setMajorTickUnit(1); widthSlider.setSnapToTicks(true);
        widthSlider.setShowTickMarks(true); widthSlider.setShowTickLabels(true);
        widthSlider.valueProperty().addListener((o,ov,nv)->{ columns = nv.intValue(); widthValue.setText(""+columns); });

        Slider heightSlider = new Slider(15, 30, rows);
        heightSlider.setMajorTickUnit(1); heightSlider.setSnapToTicks(true);
        heightSlider.setShowTickMarks(true); heightSlider.setShowTickLabels(true);
        heightSlider.valueProperty().addListener((o,ov,nv)->{ rows = nv.intValue(); heightValue.setText(""+rows); });

        Slider levelSlider = new Slider(1, 10, level);
        levelSlider.setMajorTickUnit(1); levelSlider.setSnapToTicks(true);
        levelSlider.setShowTickMarks(true); levelSlider.setShowTickLabels(true);
        levelSlider.valueProperty().addListener((o,ov,nv)->{ level = nv.intValue(); levelValue.setText(""+level); });

        // Checkboxes (igual)
        CheckBox musicCheck = new CheckBox(); musicCheck.setSelected(music);
        musicCheck.selectedProperty().addListener((o,ov,nv)->{ music = nv; musicValue.setText(nv?"On":"Off"); });

        CheckBox soundCheck = new CheckBox(); soundCheck.setSelected(soundEffects);
        soundCheck.selectedProperty().addListener((o,ov,nv)->{ soundEffects = nv; soundValue.setText(nv?"On":"Off"); });

        CheckBox aiCheck = new CheckBox(); aiCheck.setSelected(aiPlay);
        aiCheck.selectedProperty().addListener((o,ov,nv)->{ aiPlay = nv; aiValue.setText(nv?"On":"Off"); });

        CheckBox extendCheck = new CheckBox(); extendCheck.setSelected(extendMode);
        extendCheck.selectedProperty().addListener((o,ov,nv)->{ extendMode = nv; extendValue.setText(nv?"On":"Off"); });

        // Grid más centrado y con márgenes
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(0, 12, 0, 12)); // separa del borde del popup

        // Columnas por porcentaje (quita el ancho fijo grande)
        ColumnConstraints c0 = new ColumnConstraints(); c0.setPercentWidth(40); // etiquetas
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(45); // sliders/checkbox
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(15); // valores
        grid.getColumnConstraints().addAll(c0, c1, c2);

        // Fila por fila
        Label lW = new Label("Field Width (No of cells):");
        Label lH = new Label("Field Height (No of cells):");
        Label lL = new Label("Game Level:");
        Label lM = new Label("Music (On/Off):");
        Label lS = new Label("Sound Effect (On/Off):");
        Label lA = new Label("AI Play (On/Off):");
        Label lE = new Label("Extend Mode (On/Off):");

        grid.add(lW, 0, 0); grid.add(widthSlider, 1, 0); grid.add(widthValue, 2, 0);
        grid.add(lH, 0, 1); grid.add(heightSlider,1, 1); grid.add(heightValue,2, 1);
        grid.add(lL, 0, 2); grid.add(levelSlider, 1, 2); grid.add(levelValue, 2, 2);
        grid.add(lM, 0, 3); grid.add(musicCheck,1, 3);   grid.add(musicValue, 2, 3);
        grid.add(lS, 0, 4); grid.add(soundCheck,1, 4);   grid.add(soundValue, 2, 4);
        grid.add(lA, 0, 5); grid.add(aiCheck,1, 5);      grid.add(aiValue, 2, 5);
        grid.add(lE, 0, 6); grid.add(extendCheck,1,6);   grid.add(extendValue,2,6);

        // Alinea etiquetas a la derecha para que queden más “al centro”
        GridPane.setHalignment(lW, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(lH, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(lL, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(lM, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(lS, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(lA, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(lE, javafx.geometry.HPos.RIGHT);

        Label title = new Label("Configuration");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20)); // un pelín más compacto
        HBox top = new HBox(title);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(8, 0, 8, 0));

        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());
        HBox bottom = new HBox(back);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(8, 0, 6, 0));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12, 18, 12, 18)); // margen general del popup
        root.setTop(top);
        root.setCenter(grid);
        root.setBottom(bottom);

        stage.setTitle("Tetris");
        // Popup un poco más pequeño
        stage.setScene(new Scene(root, 560, 340));
        stage.sizeToScene();
    }


    // Add getters to retrieve updated values if needed
//    public int getColumns() { return columns; }
//    public int getRows() { return rows; }
//    public int getLevel() { return level; }
//    public boolean isMusic() { return music; }
//    public boolean isSoundEffects() { return soundEffects; }
//    public boolean isAiPlay() { return aiPlay; }
//    public boolean isExtendMode() { return extendMode; }
}
