package tetrisclient;

public class TetrisAI {

    private final PlayScreen screen;

    public TetrisAI(PlayScreen screen) {
        this.screen = screen;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try { Thread.sleep(1500); } catch (InterruptedException e) { return; }
                PlayScreen.Tetromino current = screen.getCurrentTetromino();
                if (current == null) continue;

                PlayScreen.Tetromino simulated = current.cloneTetromino();
                Move bestMove = findBestMove(simulated);

                if (bestMove != null) {
                    //rotation
                    for (int i = 0; i < bestMove.rotation; i++) current.rotate();

                    // Move horizontally
                    while (current.getX() < bestMove.column) current.move(1, 0);
                    while (current.getX() > bestMove.column) current.move(-1, 0);

                    // Dropping piece
                    while (current.move(0, 1)) {}
                }
            }
        }).start();
    }

    private Move findBestMove(PlayScreen.Tetromino tetro) {
        int[][] board = getBoardMatrix();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (int rotation = 0; rotation < 4; rotation++) {
            PlayScreen.Tetromino temp = tetro.cloneTetromino();
            for (int r = 0; r < rotation; r++) temp.rotate();

            for (int col = 0; col < screen.getColumns(); col++) {
                PlayScreen.Tetromino temp2 = temp.cloneTetromino();
                int dx = col - temp2.getX();
                if (!temp2.move(dx, 0)) continue;

                // Drop to bottom
                while (temp2.move(0,1)) {}

                int[][] simulatedBoard = copyBoard(board);
                placePiece(simulatedBoard, temp2);

                int score = evaluateBoard(simulatedBoard);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(col, temp2.getY(), rotation);
                }
            }
        }
        return bestMove;
    }

    private int[][] getBoardMatrix() {
        int[][] board = new int[screen.getRows()][screen.getColumns()];
        for (javafx.scene.shape.Rectangle r : screen.getLockedBlocks()) {
            int row = (int) (r.getY() / screen.getCellSize());
            int col = (int) (r.getX() / screen.getCellSize());
            board[row][col] = 1;
        }
        return board;
    }

    private void placePiece(int[][] board, PlayScreen.Tetromino tetro) {
        for (int[] cell : tetro.getShape()) {
            int x = tetro.getX() + cell[0];
            int y = tetro.getY() + cell[1];
            if (y >= 0 && y < board.length && x >= 0 && x < board[0].length)
                board[y][x] = 1;
        }
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++)
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        return copy;
    }

    private int evaluateBoard(int[][] board) {
        int clearedLines = 0;
        int height = 0;
        int holes = 0;

        for (int y = 0; y < board.length; y++) {
            boolean full = true;
            for (int x = 0; x < board[0].length; x++) {
                if (board[y][x] == 0) full = false;
            }
            if (full) clearedLines++;
        }

        for (int x = 0; x < board[0].length; x++) {
            boolean blockFound = false;
            for (int y = 0; y < board.length; y++) {
                if (board[y][x] == 1) { blockFound = true; height += (board.length - y); }
                else if (blockFound) holes++;
            }
        }

        return clearedLines * 100 - height - holes * 10;
    }

    public static class Move {
        public final int column;
        public final int row;
        public final int rotation;
        public Move(int column, int row, int rotation) {
            this.column = column;
            this.row = row;
            this.rotation = rotation;
        }
    }
}
