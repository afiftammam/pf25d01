package TTTConsole;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

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
            // PERUBAHAN: Buat tabel baru untuk game multiplayer
            createGamesTable();
            createMovesTable();
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
            System.err.println("Gagal membuat tabel players: " + e.getMessage());
        }
    }

    // --- LOGIKA BARU UNTUK MULTIPLAYER ONLINE ---

    private void createGamesTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS games (" +
                "game_id VARCHAR(10) PRIMARY KEY," +
                "player_x VARCHAR(50)," +
                "player_o VARCHAR(50)," +
                "status ENUM('WAITING', 'IN_PROGRESS', 'FINISHED') NOT NULL," +
                "winner VARCHAR(50)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel games: " + e.getMessage());
        }
    }

    private void createMovesTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS moves (" +
                "move_id INT AUTO_INCREMENT PRIMARY KEY," +
                "game_id VARCHAR(10)," +
                "move_number INT NOT NULL," +
                "player_seed VARCHAR(10) NOT NULL," +
                "row_pos INT NOT NULL," +
                "col_pos INT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (game_id) REFERENCES games(game_id)" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel moves: " + e.getMessage());
        }
    }

    public String createNewGame(String playerX) {
        String gameId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String query = "INSERT INTO games (game_id, player_x, status) VALUES (?, ?, 'WAITING')";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, gameId);
            pstmt.setString(2, playerX);
            pstmt.executeUpdate();
            return gameId;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean joinGame(String gameId, String playerO) {
        String query = "UPDATE games SET player_o = ?, status = 'IN_PROGRESS' WHERE game_id = ? AND status = 'WAITING'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, playerO);
            pstmt.setString(2, gameId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean recordMove(String gameId, int moveNumber, Seed player, int row, int col) {
        String query = "INSERT INTO moves (game_id, move_number, player_seed, row_pos, col_pos) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, gameId);
            pstmt.setInt(2, moveNumber);
            pstmt.setString(3, player.name());
            pstmt.setInt(4, row);
            pstmt.setInt(5, col);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int[] getLatestMove(String gameId, int currentMoveCount) {
        String query = "SELECT row_pos, col_pos, player_seed FROM moves WHERE game_id = ? ORDER BY move_number DESC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, gameId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Check if a new move has been made
                String moveCountQuery = "SELECT COUNT(*) FROM moves WHERE game_id = ?";
                try (PreparedStatement countPstmt = connection.prepareStatement(moveCountQuery)) {
                    countPstmt.setString(1, gameId);
                    ResultSet countRs = countPstmt.executeQuery();
                    if (countRs.next()) {
                        int totalMoves = countRs.getInt(1);
                        if (totalMoves > currentMoveCount) {
                            return new int[]{rs.getInt("row_pos"), rs.getInt("col_pos")};
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No new move found
    }

    public void updateGameWinner(String gameId, String winnerUsername) {
        String query = "UPDATE games SET status = 'FINISHED', winner = ? WHERE game_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, winnerUsername);
            pstmt.setString(2, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metode lama
    public void updatePlayerStats(String username, GameResult result) {
        if (connection == null || username == null || username.trim().isEmpty() || username.equals("Skynet AI") || username.startsWith("System AI")) {
            return;
        }

        String query = "INSERT INTO players (username, wins, losses, draws) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "wins = wins + VALUES(wins), losses = losses + VALUES(losses), draws = draws + VALUES(draws)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            int winIncrement = (result == GameResult.WIN) ? 1 : 0;
            int lossIncrement = (result == GameResult.LOSS) ? 1 : 0;
            int drawIncrement = (result == GameResult.DRAW) ? 1 : 0;

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