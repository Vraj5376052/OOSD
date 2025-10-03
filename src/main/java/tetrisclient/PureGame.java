package tetrisclient;

import java.util.Arrays;

public class PureGame {
    private int width;
    private int height;
    private int[][] cells;
    private int[][] currentShape;
    private int[][] nextShape;

    // Default constructor (needed by Jackson)
    public PureGame() {}

    // Convenience constructor: build from PlayScreen
    public PureGame(PlayScreen playScreen) {
        if (playScreen != null) {
            this.width = playScreen.getColumns();
            this.height = playScreen.getRows();
            this.cells = playScreen.getBoard();
            if (playScreen.getCurrentTetromino() != null) {
                this.currentShape = playScreen.getCurrentTetromino().getShape();
            }
            this.nextShape = playScreen.getNextTetrominoShape();
        }
    }

    // Getters and setters
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int[][] getCells() { return cells != null ? cells : new int[0][0]; }
    public void setCells(int[][] cells) { this.cells = cells; }

    public int[][] getCurrentShape() { return currentShape != null ? currentShape : new int[0][0]; }
    public void setCurrentShape(int[][] currentShape) { this.currentShape = currentShape; }

    public int[][] getNextShape() { return nextShape != null ? nextShape : new int[0][0]; }
    public void setNextShape(int[][] nextShape) { this.nextShape = nextShape; }

    @Override
    public String toString() {
        return "PureGame{" +
                "width=" + width +
                ", height=" + height +
                ", cells=" + Arrays.deepToString(getCells()) +
                ", currentShape=" + Arrays.deepToString(getCurrentShape()) +
                ", nextShape=" + Arrays.deepToString(getNextShape()) +
                '}';
    }
}
