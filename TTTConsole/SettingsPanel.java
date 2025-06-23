// C:\Users\Akhtar\pf25d01\TTTConsole\SettingsPanel.java
package TTTConsole;


import javax.swing.*;
import java.awt.*;


public class SettingsPanel extends JPanel {


    // PERBAIKAN: Deklarasikan variabel sebagai field/anggota kelas
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final JFrame mainFrame;


    public SettingsPanel(JPanel mainPanel, CardLayout cardLayout, JFrame mainFrame) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.mainFrame = mainFrame;


        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        add(titleLabel, BorderLayout.NORTH);


        JPanel themePanel = new JPanel();
        themePanel.setOpaque(false);
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));


        JLabel themeLabel = new JLabel("Select Theme:");
        themeLabel.setFont(Theme.FONT_STATUS);
        themeLabel.setForeground(Theme.TEXT_LIGHT);
        themeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JButton mainThemeButton = new JButton("Main Theme");
        styleButton(mainThemeButton);
        mainThemeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainThemeButton.addActionListener(e -> {
            Theme.applyMainTheme();
            Theme.updateUI(mainFrame);
        });


        JButton cyberThemeButton = new JButton("Cyber Theme");
        styleButton(cyberThemeButton);
        cyberThemeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cyberThemeButton.addActionListener(e -> {
            Theme.applyCyberTheme();
            Theme.updateUI(mainFrame);
        });


        themePanel.add(themeLabel);
        themePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        themePanel.add(mainThemeButton);
        themePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        themePanel.add(cyberThemeButton);


        add(themePanel, BorderLayout.CENTER);


        JButton backButton = new JButton("Back to Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void styleButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.BG_PANEL);
        button.setForeground(Theme.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 65));
        button.setMaximumSize(new Dimension(280, 65));
    }
}


