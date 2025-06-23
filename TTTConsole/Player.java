// TTTConsole/Player.java

import java.util.Scanner;

public class Player {
    protected Seed seed;

    public Player(Seed seed) {
        this.seed = seed;
    }

    public Seed getSeed() {
        return seed;
    }

    /**
     * Meminta pemain untuk memasukkan langkah dan melakukan validasi.
     * Jika valid, langkah langsung diterapkan ke papan.
     * Method ini tidak diubah secara fungsionalitas dari kode asli Anda,
     * tetapi input 'undo' akan ditangani di kelas Game.
     * @param board Papan permainan saat ini.
     * @return Mengembalikan true jika langkah berhasil dibuat, false jika tidak.
     */
    public boolean move(Board board) {
        Scanner in = new Scanner(System.in);
        boolean validInput = false;
        do {
            System.out.print("Player '" + seed + "', enter your move (row[1-3] col[1-3]): ");
            if (in.hasNextInt()) {
                int row = in.nextInt() - 1;
                int col = in.nextInt() - 1;
                if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                        && board.getCells()[row][col].getContent() == Seed.EMPTY) {
                    board.getCells()[row][col].setContent(seed);
                    board.setCurrentRow(row);
                    board.setCurrentCol(col);
                    validInput = true;
                } else {
                    System.out.println("This move at (" + (row + 1) + "," + (col + 1)
                            + ") is not valid. Try again...");
                }
            } else {
                // Jika input bukan angka, kita anggap sebagai 'undo' atau input tidak valid
                String inputStr = in.next();
                if (inputStr.equalsIgnoreCase("undo")) {
                    return false; // Sinyal untuk 'undo'
                }
                System.out.println("Invalid input. Please enter numbers.");
            }
        } while (!validInput);
        return true; // Langkah berhasil dibuat
    }
}