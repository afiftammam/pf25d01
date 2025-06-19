package TTTConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Kelas AI yang telah diperbarui.
 * - Menggunakan metode dari Board untuk cek status (refactoring).
 * - Memiliki 3 tingkat kesulitan: Easy, Medium, Hard.
 */
public class AIPlayer {

    private final Seed aiSeed = Seed.NOUGHT;
    private final Seed playerSeed = Seed.CROSS;
    private final Random random = new Random();

    /**
     * Metode utama yang menjadi router untuk memilih strategi AI berdasarkan kesulitan.
     * @param board Papan permainan saat ini.
     * @param difficulty Tingkat kesulitan yang dipilih.
     * @return Array int {baris, kolom} untuk langkah terbaik.
     */
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

    /** Level HARD: Menggunakan algoritma Minimax. */
    private int[] findMinimaxMove(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(aiSeed, r, c);
                    int score = minimax(board, 0, false);
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

    /** Level MEDIUM: Cek menang, cek blokir, lalu acak. */
    private int[] findMediumMove(Board board) {
        // 1. Cek apakah AI bisa menang dalam satu langkah
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(aiSeed, r, c);
                    if (board.getCurrentGameState() == State.NOUGHT_WON) {
                        board.placeSeed(Seed.NO_SEED, r, c); // Batalkan langkah
                        return new int[]{r, c};
                    }
                    board.placeSeed(Seed.NO_SEED, r, c); // Batalkan langkah
                }
            }
        }

        // 2. Cek apakah pemain bisa menang, lalu blokir
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(playerSeed, r, c);
                    if (board.getCurrentGameState() == State.CROSS_WON) {
                        board.placeSeed(Seed.NO_SEED, r, c); // Batalkan langkah
                        return new int[]{r, c};
                    }
                    board.placeSeed(Seed.NO_SEED, r, c); // Batalkan langkah
                }
            }
        }

        // 3. Jika tidak ada yang bisa diblokir atau dimenangkan, pilih acak
        return findRandomMove(board);
    }

    /** Level EASY: Pilih langkah acak dari sel yang kosong. */
    private int[] findRandomMove(Board board) {
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }
        if (emptyCells.isEmpty()) return null;
        return emptyCells.get(random.nextInt(emptyCells.size()));
    }

    // --- Logika Minimax (Tidak Berubah, tapi lebih bersih) ---

    private int minimax(Board board, int depth, boolean isMaximizing) {
        // Menggunakan metode dari Board, bukan metode lokal lagi.
        State result = board.getCurrentGameState();
        if (result != State.PLAYING) {
            return score(result);
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int r = 0; r < Board.ROWS; r++) {
                for (int c = 0; c < Board.COLS; c++) {
                    if (board.isValidMove(r, c)) {
                        board.placeSeed(aiSeed, r, c);
                        int score = minimax(board, depth + 1, false);
                        board.placeSeed(Seed.NO_SEED, r, c);
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int r = 0; r < Board.ROWS; r++) {
                for (int c = 0; c < Board.COLS; c++) {
                    if (board.isValidMove(r, c)) {
                        board.placeSeed(playerSeed, r, c);
                        int score = minimax(board, depth + 1, true);
                        board.placeSeed(Seed.NO_SEED, r, c);
                        bestScore = Math.min(score, bestScore);
                    }
                }
            } //akhtar
            return bestScore;
        }
    }

    private int score(State result) {
        if (result == State.NOUGHT_WON) return 10;
        if (result == State.CROSS_WON) return -10;
        return 0;
    }
}