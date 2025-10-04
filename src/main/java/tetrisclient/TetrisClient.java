package tetrisclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;

public class TetrisClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;
    private static boolean serverStarted = false; //


    private static void ensureServerRunning() {
        if (serverStarted) return; // already launched

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("[TetrisClient] Server already running on port " + SERVER_PORT);
            serverStarted = true;
        } catch (IOException e) {
            try {
                System.out.println("[TetrisClient] 🚀 Starting local TetrisServer.jar...");
                new ProcessBuilder("java", "-jar", "TetrisServer.jar")
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start();
                Thread.sleep(1500); // give it a moment to boot
                serverStarted = true;
            } catch (Exception ex) {
                System.err.println("[TetrisClient] Could not auto-start TetrisServer.jar: " + ex.getMessage());
            }
        }
    }

    public static void sendGameState(PlayScreen playScreen) {
        if (playScreen == null) {
            System.err.println("PlayScreen is null. Cannot send game state.");
            return;
        }


        ensureServerRunning();

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            PureGame game = new PureGame(playScreen);
            ObjectMapper mapper = new ObjectMapper();
            String jsonGameState = mapper.writeValueAsString(game);

            out.println(jsonGameState);
            System.out.println("Sent game state to server: " + jsonGameState);

            String response = in.readLine();
            System.out.println("Received response from server: " + response);

            if (response != null && !response.isEmpty() && !"null".equals(response)) {
                OpMove move = mapper.readValue(response, OpMove.class);
                System.out.println("Optimal Move: X=" + move.opX() + ", Rotations=" + move.opRotate());

                if (move.opX() == 0) {
                    System.out.println("Place the piece at the left-most position.");
                } else {
                    System.out.println("Move the piece to X=" + move.opX());
                }
                if (move.opRotate() == 0) {
                    System.out.println("No rotation needed.");
                } else {
                    System.out.println("Rotate the piece " + move.opRotate() + " times.");
                }
            } else {
                System.out.println("Server returned null/empty response.");
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server at " + SERVER_HOST + ":" + SERVER_PORT);
            e.printStackTrace();
        }
    }
}
