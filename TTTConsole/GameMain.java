// TTTConsole/GameMain.java

import java.util.Scanner;

public class GameMain {
    private static Game game;
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        game = new Game();
        String playAgain;

        do {
            game.initGame();
            game.start(); // Metode ini sekarang menangani seluruh loop permainan.

            // Setelah permainan berakhir, tawarkan opsi replay.
            System.out.print("Watch replay? (y/n): ");
            String watchReplay = in.next().toLowerCase();
            if (watchReplay.equals("y")) {
                game.replayGame();
            }

            System.out.print("Play again? (y/n): ");
            playAgain = in.next().toLowerCase();
        } while (playAgain.equals("y"));

        System.out.println("Bye!");
        in.close();
    }
}