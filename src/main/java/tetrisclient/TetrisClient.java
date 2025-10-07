package tetrisclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class TetrisClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;

    private static boolean serverStarted = false;
    private static boolean errorShown = false;


    private static boolean isServerRunning() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private static void startServerManually() {
        try {
            System.out.println("[TetrisClient] Starting TetrisServer.jar...");
            new ProcessBuilder("java", "-jar", "TetrisServer.jar")
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            Thread.sleep(2000); // allow startup time
            serverStarted = true;
        } catch (Exception e) {
            System.err.println("[TetrisClient] Failed to start TetrisServer.jar: " + e.getMessage());
        }
    }


    public static void sendGameState(PlayScreen playScreen, Stage primaryStage) {
        if (playScreen == null) {
            System.err.println("[TetrisClient] PlayScreen is null. Cannot send game state.");
            return;
        }


        if (!Configuration.isExternalPlayerEnabled()) {
            if (!errorShown) {
                errorShown = true;
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("External Player Disabled");
                    alert.setHeaderText("External Player mode is turned OFF");
                    alert.setContentText(
                            "To use the external Tetris server, please enable 'External Player' in Configuration.\n\n" +
                                    "The server will not start while it’s OFF."
                    );
                    alert.showAndWait();

                    try {
                        playScreen.stopGame();
                        new Main().showMainMenu(primaryStage);
                    } catch (Exception e) {
                        System.err.println("[TetrisClient] Failed to return to main menu: " + e.getMessage());
                    }
                });
            }
            return;
        }


        if (!isServerRunning()) {
            startServerManually();

            // Check again if still not running
            if (!isServerRunning()) {
                if (!errorShown) {
                    errorShown = true;
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Tetris Server Not Running");
                        alert.setHeaderText("Failed to connect to Tetris server");
                        alert.setContentText("Please ensure TetrisServer.jar exists and can be started manually.");
                        alert.showAndWait();

                        try {
                            playScreen.stopGame();
                            new Main().showMainMenu(primaryStage);
                        } catch (Exception e) {
                            System.err.println("[TetrisClient] Failed to return to main menu: " + e.getMessage());
                        }
                    });
                }
                return;
            }
        }

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            PureGame game = new PureGame(playScreen);
            ObjectMapper mapper = new ObjectMapper();
            String jsonGameState = mapper.writeValueAsString(game);

            out.println(jsonGameState);
            System.out.println("[TetrisClient] Server started, Game state sent ");


            String response = in.readLine();
            if (response != null && !response.isEmpty() && !"null".equals(response)) {
                OpMove move = mapper.readValue(response, OpMove.class);
                System.out.println("Move → X=" + move.opX() + ", Rotate=" + move.opRotate());

            } else {
                System.out.println("[TetrisClient] Server returned empty response.");
            }

        } catch (IOException e) {
            System.err.println("[TetrisClient] Connection failed: " + e.getMessage());
        }
    }
}
