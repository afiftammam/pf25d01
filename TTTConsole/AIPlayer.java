package TTTConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer {

    private final Seed aiSeed = Seed.NOUGHT;
    private final Seed playerSeed = Seed.CROSS;
    private final Random random = new Random();

    public int[] findBestMove(Board board, GameMain.Difficulty difficulty, GameMain.GameVariant variant) {
        if (variant == GameMain.GameVariant.MISERE && difficulty == GameMain.Difficulty.MEDIUM) {
            difficulty = GameMain.Difficulty.EASY;
        }

        switch (difficulty) {
            case EASY:
                return findRandomMove(board);
            case MEDIUM:
                return findMediumMove(board);
            case HARD:
            default:
                return findMinimaxMove(board, variant);
        }
    }

    private int[] findMinimaxMove(Board board, GameMain.GameVariant variant) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int r = 0; r < board.ROWS; r++) {
            for (int c = 0; c < board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(aiSeed, r, c);
                    int score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE, variant);
                    board.placeSeed(Seed.NO_SEED, r, c);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = r;
                        bestMove[1] = c;
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta, GameMain.GameVariant variant) {
        State result = board.getCurrentGameState();
        if (result != State.PLAYING) {
            return score(result, depth, variant);
        }

        if (board.ROWS > 3 && depth > 4) {
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int r = 0; r < board.ROWS; r++) {
                for (int c = 0; c < board.COLS; c++) {
                    if (board.isValidMove(r, c)) {
                        board.placeSeed(aiSeed, r, c);
                        int score = minimax(board, depth + 1, false, alpha, beta, variant);
                        board.placeSeed(Seed.NO_SEED, r, c);
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) {
                            return bestScore;
                        }
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int r = 0; r < board.ROWS; r++) {
                for (int c = 0; c < board.COLS; c++) {
                    if (board.isValidMove(r, c)) {
                        board.placeSeed(playerSeed, r, c);
                        int score = minimax(board, depth + 1, true, alpha, beta, variant);
                        board.placeSeed(Seed.NO_SEED, r, c);
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) {
                            return bestScore;
                        }
                    }
                }
            }
            return bestScore;
        }
    }

    /**
     * PERBAIKAN: Fungsi skor ditulis ulang agar lebih jelas dan robust.
     * Mencegah error "missing return statement".
     */
    private int score(State result, int depth, GameMain.GameVariant variant) {
        // Kondisi seri (draw) selalu bernilai 0
        if (result == State.DRAW) {
            return 0;
        }

        // Logika untuk mode Misère (tujuan: KALAH)
        if (variant == GameMain.GameVariant.MISERE) {
            if (result == State.NOUGHT_WON) { // Jika AI (Nought) membuat garis...
                return -10 + depth; // ...maka itu adalah hasil yang BURUK bagi AI.
            } else { // Jika Player (Cross) yang membuat garis...
                return 10 - depth;  // ...maka itu adalah hasil yang BAGUS bagi AI.
            }
        }
        // Logika untuk mode Standar (tujuan: MENANG)
        else {
            if (result == State.NOUGHT_WON) { // Jika AI (Nought) membuat garis...
                return 10 - depth;  // ...maka itu adalah hasil yang BAGUS bagi AI.
            } else { // Jika Player (Cross) yang membuat garis...
                return -10 + depth; // ...maka itu adalah hasil yang BURUK bagi AI.
            }
        }
    }

    private int[] findMediumMove(Board board) {
        for (int r = 0; r < board.ROWS; r++) {
            for (int c = 0; c < board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(aiSeed, r, c);
                    if (board.getCurrentGameState() == State.NOUGHT_WON) {
                        board.placeSeed(Seed.NO_SEED, r, c);
                        return new int[]{r, c};
                    }
                    board.placeSeed(Seed.NO_SEED, r, c);
                }
            }
        }

        for (int r = 0; r < board.ROWS; r++) {
            for (int c = 0; c < board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(playerSeed, r, c);
                    if (board.getCurrentGameState() == State.CROSS_WON) {
                        board.placeSeed(Seed.NO_SEED, r, c);
                        return new int[]{r, c};
                    }
                    board.placeSeed(Seed.NO_SEED, r, c);
                }
            }
        }

        if (board.ROWS == 3 && board.COLS == 3 && board.isValidMove(1, 1)) {
            return new int[]{1, 1};
        }

        List<int[]> cornerMoves = new ArrayList<>();
        if (board.isValidMove(0, 0)) cornerMoves.add(new int[]{0, 0});
        if (board.isValidMove(0, board.COLS - 1)) cornerMoves.add(new int[]{0, board.COLS - 1});
        if (board.isValidMove(board.ROWS - 1, 0)) cornerMoves.add(new int[]{board.ROWS - 1, 0});
        if (board.isValidMove(board.ROWS - 1, board.COLS - 1)) cornerMoves.add(new int[]{board.ROWS - 1, board.COLS - 1});

        if (!cornerMoves.isEmpty()) {
            return cornerMoves.get(random.nextInt(cornerMoves.size()));
        }

        return findRandomMove(board);
    }

    private int[] findRandomMove(Board board) {
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < board.ROWS; r++) {
            for (int c = 0; c < board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }
        if (emptyCells.isEmpty()) return new int[]{-1, -1};
        return emptyCells.get(random.nextInt(emptyCells.size()));
    }
}