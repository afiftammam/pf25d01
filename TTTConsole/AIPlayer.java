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
                    board.placeSeed(Seed.NO_SEED, r, c); // Batalkan langkah
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

        int maxDepth = (board.ROWS > 3) ? 4 : 8;
        if (depth >= maxDepth) {
            return evaluateBoard(board);
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
                            return bestScore; // Alpha-beta pruning
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
                            return bestScore; // Alpha-beta pruning
                        }
                    }
                }
            }
            return bestScore;
        }
    }


    private int score(State result, int depth, GameMain.GameVariant variant) {
        if (result == State.DRAW) {
            return 0;
        }


        if (variant == GameMain.GameVariant.MISERE) {
            // Logika terbalik untuk Misere
            if (result == State.NOUGHT_WON) { // AI (Nought) membuat baris (kalah)
                return -1000 + depth;
            } else { // Player (Cross) membuat baris (AI menang)
                return 1000 - depth;
            }
        } else {
            // Logika standar
            if (result == State.NOUGHT_WON) { // AI (Nought) menang
                return 1000 - depth;
            } else { // Player (Cross) menang
                return -1000 + depth;
            }
        }
    }




    private int evaluateBoard(Board board) {
        int score = 0;
        // Evaluasi baris, kolom, dan diagonal
        score += evaluateLines(board, 1, 0); // Vertikal
        score += evaluateLines(board, 0, 1); // Horizontal
        score += evaluateLines(board, 1, 1); // Diagonal
        score += evaluateLines(board, 1, -1); // Anti-diagonal
        return score;
    }


    private int evaluateLines(Board board, int dr, int dc) {
        int score = 0;
        for (int r = 0; r < board.ROWS; r++) {
            for (int c = 0; c < board.COLS; c++) {
                // Pastikan window tidak keluar dari papan
                if (c + (board.WIN_STREAK - 1) * dc >= 0 && c + (board.WIN_STREAK - 1) * dc < board.COLS &&
                        r + (board.WIN_STREAK - 1) * dr >= 0 && r + (board.WIN_STREAK - 1) * dr < board.ROWS) {


                    int aiPieces = 0;
                    int playerPieces = 0;
                    for (int i = 0; i < board.WIN_STREAK; i++) {
                        if (board.cells[r + i * dr][c + i * dc].content == aiSeed) {
                            aiPieces++;
                        } else if (board.cells[r + i * dr][c + i * dc].content == playerSeed) {
                            playerPieces++;
                        }
                    }
                    score += evaluateSingleLine(aiPieces, playerPieces);
                }
            }
        }
        return score;
    }


    private int evaluateSingleLine(int aiPieces, int playerPieces) {
        int score = 0;
        if (aiPieces > 0 && playerPieces > 0) {
            return 0;
        } else if (aiPieces > 0) {
            if (aiPieces == 1) score = 1;
            else if (aiPieces == 2) score = 10;
            else if (aiPieces == 3) score = 100;
            else if (aiPieces == 4) score = 1000;
        } else if (playerPieces > 0) {
            if (playerPieces == 1) score = -1;
            else if (playerPieces == 2) score = -10;
            else if (playerPieces == 3) score = -100;
            else if (playerPieces == 4) score = -1000;
        }
        return score;
    }


    private int[] findMediumMove(Board board) {
        for (int r = 0; r < board.ROWS; r++) {
            for (int c = 0; c < board.COLS; c++) {
                if (board.isValidMove(r, c)) {
                    board.placeSeed(aiSeed, r, c);
                    if (board.checkGameState(aiSeed, r, c) == State.NOUGHT_WON) {
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
                    if (board.checkGameState(playerSeed, r, c) == State.CROSS_WON) {
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

