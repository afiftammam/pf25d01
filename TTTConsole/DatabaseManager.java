package TTTConsole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DatabaseManager {

    // --- Konfigurasi Database (Aiven Cloud) ---
    private static final String DB_HOST = "mysql-dasprogfinal-akhtar-dasprofinal.f.aivencloud.com";
    private static final String DB_PORT = "28538";
    private static final String DB_NAME = "Finaldasprog"; // PASTIKAN NAMA INI BENAR
    private static final String DB_USER = "avnadmin";
    private static final String DB_PASSWORD = "AVNS_HBkzd0HRku5PSOY_2Gt";

    // URL Koneksi JDBC dengan mode SSL yang diperlukan untuk Aiven
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?sslmode=require";

    private Connection connection;

    public DatabaseManager() {
        try {
            // Membuat koneksi ke database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Koneksi database Aiven berhasil.");
            // Memastikan tabel 'players' ada
            createPlayerTable();
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database: " + e.getMessage());
            // Tampilkan dialog error yang lebih informatif
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database Aiven.\n" +
                            "Pesan Error: " + e.getMessage() + "\n\n" +
                            "Pastikan:\n" +
                            "1. Anda memiliki koneksi internet.\n" +
                            "2. Aturan Firewall di Aiven mengizinkan IP Anda.\n" +
                            "3. Detail koneksi (host, port, user, password) sudah benar.",
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

    public void updatePlayerStats(String username, State result) {
        if (connection == null || username == null || username.trim().isEmpty() || username.equals("Skynet AI")) {
            return; // Jangan simpan skor untuk AI atau jika nama tidak valid
        }

        String query = "INSERT INTO players (username, wins, losses, draws) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "wins = wins + VALUES(wins), " +
                "losses = losses + VALUES(losses), " +
                "draws = draws + VALUES(draws)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);

            int winIncrement = (result == State.CROSS_WON || result == State.NOUGHT_WON) ? 1 : 0;
            int drawIncrement = (result == State.DRAW) ? 1 : 0;
            int lossIncrement = (result != State.CROSS_WON && result != State.NOUGHT_WON && result != State.DRAW) ? 1 : 0;

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