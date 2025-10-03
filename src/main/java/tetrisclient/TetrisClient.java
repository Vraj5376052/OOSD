package tetrisclient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;

public class TetrisClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;

    public static void main(String[] args) {
        PureGame game = new PureGame();

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(new
                     OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new
                     InputStreamReader(socket.getInputStream()))) {
// Step 2: Convert PureGame object to JSON (Jackson)
            ObjectMapper mapper = new ObjectMapper();
            String jsonGameState = mapper.writeValueAsString(game);
// Step 3: Send the game state to the server
            out.println(jsonGameState);
            System.out.println("Sent game state to server: " +
                    jsonGameState);
// Step 4: Wait for the server's response (OpMove)
            String response = in.readLine();
            System.out.println("Received response from server: " +
                    response);
// Step 5: Convert the JSON response to an OpMove object (Jackson)
                    OpMove move = mapper.readValue(response, OpMove.class);
            System.out.println("Optimal Move: X=" + move.opX() + ", Rotations=" + move.opRotate());
// Step 6: Apply the move based on the opX and opRotate values
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
// Note: The server will close the connection afte responding.
// For the next query, create a new Socket and repeat.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
