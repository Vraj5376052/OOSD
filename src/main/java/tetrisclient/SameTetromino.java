package tetrisclient;

import java.util.Random;

public class SameTetromino {
    private static Random random;
    private static boolean initialized = false;

    //Shared shape
    private static int[][] currentShape;

    public static void setSeed(long seed) {
        random = new Random(seed);
        initialized = true;
        currentShape = null;
        System.out.println("[SameTetromino] Seed set: " + seed);
    }

    private static int[][][] SHAPES = {
            {{0,0},{1,0},{-1,0},{0,1}},     // T
            {{0,0},{1,0},{0,1},{1,1}},      // O
            {{0,0},{1,0},{-1,0},{-1,1}},    // L
            {{0,0},{1,0},{-1,0},{1,1}},     // J
            {{0,0},{1,0},{0,1},{-1,1}},     // S
            {{0,0},{-1,0},{0,1},{1,1}},     // Z
            {{0,0},{-1,0},{1,0},{2,0}}      // I
    };

    //Returns the same shape until Next Round is called
    public static int[][] getSharedShape() {
        if (!initialized) setSeed(System.currentTimeMillis());
        if (currentShape == null) {
            int idx = random.nextInt(SHAPES.length);
            currentShape = SHAPES[idx];
            System.out.println("[SameTetromino] New shared shape index: " + idx);
        } else {
            System.out.println("[SameTetromino] Returning same shape as previous round");
        }
        return currentShape;
    }


    public static void nextRound() {
        currentShape = null;
    }
}
