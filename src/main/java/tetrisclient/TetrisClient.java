package tetrisclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;

/**
 * External Tetris client that connects to the server,
 * sends the current game state (from PlayScreen),
 * and receives an optimal move.
 */
public class TetrisClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;

    /**
     * Sends the game state from a PlayScreen instance to the server
     * and prints/logs the optimal move.
     */
    public static void sendGameState(PlayScreen playScreen) {
        if (playScreen == null) {
            System.err.println("⚠ PlayScreen is null. Cannot send game state.");
            return;
        }

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // --- Build PureGame directly from PlayScreen ---
            PureGame game = new PureGame(playScreen);

            // --- Convert PureGame → JSON ---
            ObjectMapper mapper = new ObjectMapper();
            String jsonGameState = mapper.writeValueAsString(game);
            out.println(jsonGameState);
            System.out.println("Sent game state to server: " + jsonGameState);

            // --- Receive response ---
            String response = in.readLine();
            System.out.println("Received response from server: " + response);

            if (response != null && !response.isEmpty() && !"null".equals(response)) {
                OpMove move = mapper.readValue(response, OpMove.class);
                System.out.println("Optimal Move: X=" + move.opX() + ", Rotations=" + move.opRotate());

                // Example handling
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
                System.out.println("⚠ Server returned null/empty response.");
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server at " + SERVER_HOST + ":" + SERVER_PORT);
            e.printStackTrace();
        }
    }
}
