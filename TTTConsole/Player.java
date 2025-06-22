package TTTConsole;

public class Player {
    private final String username;
    private final int wins;
    private final int losses;
    private final int draws;

    public Player(String username, int wins, int losses, int draws) {
        this.username = username;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public String getUsername() { return username; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getDraws() { return draws; }

    public double getWinRate() {
        int totalGames = wins + losses + draws;
        if (totalGames == 0) {
            return 0.0;
        }
        return (double) wins / totalGames;
    }
}