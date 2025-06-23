// TTTConsole/Game.java

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private Player player;
    private AIPlayer aiPlayer;

    // Menyimpan riwayat langkah untuk Undo dan Replay
    private List<int[]> moveHistory = new ArrayList<>();

    public Game() {
        // Objek-objek ini tidak perlu diinisialisasi ulang setiap game baru
        player = new Player(Seed.CROSS);
        aiPlayer = new AIPlayer(Seed.NOUGHT);
    }

    public void initGame() {
        board = new Board(); // Buat papan baru untuk setiap game
        board.init();
        moveHistory.clear(); // Kosongkan riwayat
        currentPlayer = (new Random().nextInt(2) == 0) ? Seed.CROSS : Seed.NOUGHT; // Pilih pemain pertama secara acak
        currentState = State.PLAYING;
    }

    public void start() {
        do {
            board.paint();
            boolean moveMade;

            if (currentPlayer == player.getSeed()) {
                System.out.println("Type 'undo' to take back your last turn.");
                moveMade = player.move(board);
                if (!moveMade) { // Jika pemain mengetik 'undo'
                    undoLastMove();
                    // Setelah undo, giliran tidak berganti dan loop akan mengulang
                    continue;
                }
            } else {
                System.out.println("AI's turn...");
                sleep(500); // Jeda agar gerakan AI terlihat
                aiPlayer.move(board);
            }

            // Simpan langkah yang baru saja dibuat
            int[] currentMove = {board.getCurrentRow(), board.getCurrentCol()};
            moveHistory.add(currentMove);

            updateGame(currentPlayer); // Periksa status game

            // Ganti giliran pemain
            if (currentState == State.PLAYING) {
                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            }

        } while (currentState == State.PLAYING);

        // --- Game Over ---
        board.paint();
        if (currentState == State.CROSS_WON) {
            System.out.println("'X' won! Bye!");
        } else if (currentState == State.NOUGHT_WON) {
            System.out.println("'O' won! Bye!");
        } else if (currentState == State.DRAW) {
            System.out.println("It's a draw! Bye!");
        }
    }

    private void undoLastMove() {
        // Undo berarti membatalkan giliran pemain dan giliran AI sebelumnya.
        if (moveHistory.size() >= 2) {
            // Batalkan langkah AI
            int[] aiMove = moveHistory.remove(moveHistory.size() - 1);
            board.getCells()[aiMove[0]][aiMove[1]].setContent(Seed.EMPTY);

            // Batalkan langkah Pemain
            int[] playerMove = moveHistory.remove(moveHistory.size() - 1);
            board.getCells()[playerMove[0]][playerMove[1]].setContent(Seed.EMPTY);

            System.out.println("Undo successful. Your turn again.");
            // Setel ulang status permainan menjadi PLAYING jika sebelumnya sudah menang/seri
            currentState = State.PLAYING;
            // Giliran tidak berubah, pemain bisa mencoba langkah lain.
        } else if (moveHistory.size() == 1){
            // Hanya bisa terjadi jika pemain pertama melakukan undo sebelum AI bergerak
            int[] firstMove = moveHistory.remove(moveHistory.size() - 1);
            board.getCells()[firstMove[0]][firstMove[1]].setContent(Seed.EMPTY);
            System.out.println("First move undone. Your turn again.");
            currentState = State.PLAYING;
        }
        else {
            System.out.println("Cannot undo. No moves to undo.");
        }
    }

    public void replayGame() {
        if (moveHistory.isEmpty()) {
            System.out.println("No game was played to replay.");
            return;
        }

        System.out.println("\n--- Starting Replay ---");
        Board replayBoard = new Board(); // Gunakan board terpisah untuk replay
        replayBoard.init();

        Seed currentPlayerForReplay = (new Random().nextInt(2) == 0) ? Seed.CROSS : Seed.NOUGHT; // Sesuaikan dengan logika initGame() Anda

        for (int[] move : moveHistory) {
            clearConsole(); // Membersihkan layar konsol agar rapi
            replayBoard.getCells()[move[0]][move[1]].setContent(currentPlayerForReplay);
            System.out.println("Replaying move: " + currentPlayerForReplay + " at (" + (move[0] + 1) + "," + (move[1] + 1) + ")");
            replayBoard.paint();
            sleep(1500); // Jeda 1.5 detik antar langkah
            currentPlayerForReplay = (currentPlayerForReplay == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
        }

        System.out.println("--- Replay Finished ---");
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Jika gagal, cetak beberapa baris baru sebagai alternatif
            for (int i = 0; i < 20; i++) System.out.println();
        }
    }

    public void updateGame(Seed theSeed) {
        if (board.hasWon(theSeed)) {
            currentState = (theSeed == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else if (board.isDraw()) {
            currentState = State.DRAW;
        }
    }
}