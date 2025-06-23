// TTTConsole/AIPlayer.java

import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends Player {

    public AIPlayer(Seed seed) {
        super(seed);
    }

    @Override
    public boolean move(Board board) {
        int[] result = minimax(2, seed, board); // {score, row, col}
        board.getCells()[result[1]][result[2]].setContent(this.seed);
        board.setCurrentRow(result[1]);
        board.setCurrentCol(result[2]);
        return true;
    }

    private int[] minimax(int depth, Seed playerSeed, Board board) {
        List<int[]> nextMoves = generateMoves(board);

        int bestScore = (playerSeed == seed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            bestScore = evaluate(board);
        } else {
            for (int[] move : nextMoves) {
                board.getCells()[move[0]][move[1]].setContent(playerSeed);
                if (playerSeed == seed) { // mySeed is maximizing player
                    currentScore = minimax(depth - 1, (seed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS, board)[0];
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                } else { // oppSeed is minimizing player
                    currentScore = minimax(depth - 1, seed, board)[0];
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }
                board.getCells()[move[0]][move[1]].setContent(Seed.EMPTY);
            }
        }
        return new int[] { bestScore, bestRow, bestCol };
    }

    private List<int[]> generateMoves(Board board) {
        List<int[]> nextMoves = new ArrayList<>();
        if (board.hasWon(Seed.CROSS) || board.hasWon(Seed.NOUGHT)) {
            return nextMoves;
        }

        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                if (board.getCells()[row][col].getContent() == Seed.EMPTY) {
                    nextMoves.add(new int[] { row, col });
                }
            }
        }
        return nextMoves;
    }

    private int evaluate(Board board) {
        int score = 0;
        score += evaluateLine(board, 0, 0, 0, 1, 0, 2);
        score += evaluateLine(board, 1, 0, 1, 1, 1, 2);
        score += evaluateLine(board, 2, 0, 2, 1, 2, 2);
        score += evaluateLine(board, 0, 0, 1, 0, 2, 0);
        score += evaluateLine(board, 0, 1, 1, 1, 2, 1);
        score += evaluateLine(board, 0, 2, 1, 2, 2, 2);
        score += evaluateLine(board, 0, 0, 1, 1, 2, 2);
        score += evaluateLine(board, 0, 2, 1, 1, 2, 0);
        return score;
    }

    private int evaluateLine(Board board, int r1, int c1, int r2, int c2, int r3, int c3) {
        int score = 0;

        if (board.getCells()[r1][c1].getContent() == seed) {
            score = 1;
        } else if (board.getCells()[r1][c1].getContent() != Seed.EMPTY) {
            score = -1;
        }

        if (board.getCells()[r2][c2].getContent() == seed) {
            if (score == 1) {
                score = 10;
            } else if (score == -1) {
                return 0;
            } else {
                score = 1;
            }
        } else if (board.getCells()[r2][c2].getContent() != Seed.EMPTY) {
            if (score == -1) {
                score = -10;
            } else if (score == 1) {
                return 0;
            } else {
                score = -1;
            }
        }

        if (board.getCells()[r3][c3].getContent() == seed) {
            if (score > 0) {
                score *= 10;
            } else if (score < 0) {
                return 0;
            } else {
                score = 1;
            }
        } else if (board.getCells()[r3][c3].getContent() != Seed.EMPTY) {
            if (score < 0) {
                score *= 10;
            } else if (score > 1) {
                return 0;
            } else {
                score = -1;
            }
        }
        return score;
    }
}