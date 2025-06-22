package TTTConsole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final DatabaseManager dbManager;
    private JTable leaderboardTable;
    private final String[] columnNames = {"Rank", "Username", "Wins", "Losses", "Draws", "Win Rate"};

    public LeaderboardPanel(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        add(titleLabel, BorderLayout.NORTH);

        leaderboardTable = new JTable();
        leaderboardTable.setBackground(Theme.BG_PANEL);
        leaderboardTable.setForeground(Theme.TEXT_LIGHT);
        leaderboardTable.setFont(Theme.FONT_STATUS);
        leaderboardTable.setRowHeight(30);
        leaderboardTable.getTableHeader().setFont(Theme.FONT_BUTTON);
        leaderboardTable.getTableHeader().setBackground(Theme.BG_PANEL);
        leaderboardTable.getTableHeader().setForeground(Theme.TEXT_LIGHT);
        leaderboardTable.getTableHeader().setReorderingAllowed(false);

        leaderboardTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 2));
        scrollPane.getViewport().setBackground(Theme.BG_PANEL);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Theme.BG_MAIN);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshLeaderboard() {
        List<Player> players = dbManager.getLeaderboard();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(1);

        int rank = 1;
        for (Player p : players) {
            model.addRow(new Object[]{
                    rank++,
                    p.getUsername(),
                    p.getWins(),
                    p.getLosses(),
                    p.getDraws(),
                    percentFormat.format(p.getWinRate())
            });
        }
        leaderboardTable.setModel(model);

        setColumnWidths();
    }

    /**
     * PERBAIKAN: Nilai untuk kolom "Rank" (indeks 0) dinaikkan dari 50 menjadi 75
     * agar teks header tidak terpotong. Lebar kolom lain juga disesuaikan.
     */
    private void setColumnWidths() {
        TableColumnModel columnModel = leaderboardTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(75);   // Rank
        columnModel.getColumn(1).setPreferredWidth(180);  // Username
        columnModel.getColumn(2).setPreferredWidth(75);   // Wins
        columnModel.getColumn(3).setPreferredWidth(75);   // Losses
        columnModel.getColumn(4).setPreferredWidth(75);   // Draws
        columnModel.getColumn(5).setPreferredWidth(100);  // Win Rate
    }

    private void styleButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.BG_PANEL);
        button.setForeground(Theme.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 65));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.BG_PANEL.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.BG_PANEL);
            }
        });
    }
}