package TTTConsole;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // Enum baru untuk memperjelas hasil pertandingan
    public enum GameResult {
        WIN, LOSS, DRAW
    }

    private static final String DB_HOST = "mysql-dasprogfinal-akhtar-dasprofinal.f.aivencloud.com";
    private static final String DB_PORT = "28538";
    private static final String DB_NAME = "defaultdb";
    private static final String DB_USER = "avnadmin";
    private static final String DB_PASSWORD = "AVNS_HBkzd0HRku5PSOY_2Gt";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?sslmode=require";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Koneksi database Aiven berhasil.");
            createPlayerTable();
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database Aiven.\n" + "Pesan Error: " + e.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createPlayerTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS players (" +
                "username VARCHAR(50) PRIMARY KEY," +
                "wins INT DEFAULT 0," +
                "losses INT DEFAULT 0," +
                "draws INT DEFAULT 0" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel: " + e.getMessage());
        }
    }

    /**
     * PERBAIKAN: Metode ini sekarang menerima GameResult (WIN, LOSS, DRAW) agar lebih jelas.
     */
    public void updatePlayerStats(String username, GameResult result) {
        if (connection == null || username == null || username.trim().isEmpty() || username.equals("Skynet AI") || username.startsWith("System AI")) {
            return;
        }

        String query = "INSERT INTO players (username, wins, losses, draws) VALUES (?, 1, 0, 0) " +
                "ON DUPLICATE KEY UPDATE " +
                "wins = wins + ?, losses = losses + ?, draws = draws + ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            int winIncrement = 0;
            int lossIncrement = 0;
            int drawIncrement = 0;

            if (result == GameResult.WIN) winIncrement = 1;
            else if (result == GameResult.LOSS) lossIncrement = 1;
            else if (result == GameResult.DRAW) drawIncrement = 1;

            pstmt.setString(1, username);
            pstmt.setInt(2, winIncrement);
            pstmt.setInt(3, lossIncrement);
            pstmt.setInt(4, drawIncrement);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Gagal memperbarui statistik pemain '" + username + "': " + e.getMessage());
        }
    }

    public List<Player> getLeaderboard() {
        List<Player> players = new ArrayList<>();
        if (connection == null) return players;

        String query = "SELECT username, wins, losses, draws FROM players ORDER BY wins DESC, losses ASC LIMIT 10";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                players.add(new Player(
                        rs.getString("username"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("draws")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data leaderboard: " + e.getMessage());
        }
        return players;
    }
}