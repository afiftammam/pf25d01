package TTTConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer {

    private final Seed aiSeed = Seed.NOUGHT;
    private final Seed playerSeed = Seed.CROSS;
    private final Random random = new Random();

    public int[] findBestMove(Board board, GameMain.Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return findRandomMove(board);
            case MEDIUM:
                return findMediumMove(board);
            case HARD:
            default:
                return findMinimaxMove(board);
        }
    }

    // --- STRATEGI TINGKAT KESULITAN ---

    /** Level HARD: Menggunakan algoritma Minimax dengan Alpha-Beta Pruning. */
    private int[] findMinimaxMove(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int r = 0; r < board.ROWS; r++) {
            for (int c = 0; c < board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(aiSeed, r, c);
                    // --- PERUBAHAN: Memanggil minimax dengan alpha-beta ---
                    int score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
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

    /** Level MEDIUM: Cek menang, cek blokir, lalu acak. Logika disesuaikan untuk N x N. */
    private int[] findMediumMove(Board board) {
        // 1. Cek apakah AI bisa menang
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

        // 2. Cek apakah pemain bisa menang, lalu blokir
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

        // 3. Jika tidak, pilih acak
        return findRandomMove(board);
    }

    /** Level EASY: Pilih langkah acak. */
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

    // --- Logika Minimax dengan Alpha-Beta Pruning ---
    private int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta) {
        State result = board.getCurrentGameState();
        if (result != State.PLAYING) {
            return score(result);
        }

        // Batasi kedalaman pencarian untuk papan 4x4 agar tidak terlalu lama
        if (board.ROWS > 3 && depth > 4) {
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int r = 0; r < board.ROWS; r++) {
                for (int c = 0; c < board.COLS; c++) {
                    if (board.isValidMove(r, c)) {
                        board.placeSeed(aiSeed, r, c);
                        int score = minimax(board, depth + 1, false, alpha, beta);
                        board.placeSeed(Seed.NO_SEED, r, c);
                        bestScore = Math.max(score, bestScore);
                        // --- PRUNING ---
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) {
                            return bestScore; // Cut-off
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
                        int score = minimax(board, depth + 1, true, alpha, beta);
                        board.placeSeed(Seed.NO_SEED, r, c);
                        bestScore = Math.min(score, bestScore);
                        // --- PRUNING ---
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) {
                            return bestScore; // Cut-off
                        }
                    }
                }
            }
            return bestScore;
        }
    }

    private int score(State result) {
        if (result == State.NOUGHT_WON) return 10;
        if (result == State.CROSS_WON) return -10;
        return 0; // Draw
    }
}