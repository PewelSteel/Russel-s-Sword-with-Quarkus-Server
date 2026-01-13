package org.example.menu;

import org.example.auth.AuthClient;
import org.example.menu.LoginDialog;
import org.example.database.UserProgressDAO;
import org.example.game.GamePanel;
import org.example.visuals.Background;
import javax.swing.*;
import java.awt.*;
import org.example.game.GameLoader;
import org.example.game.GameState;
import org.example.combat.character;

public class menu extends JFrame {

    public menu() {
        setTitle("Russel's Sword");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 540);
        setResizable(false);

        JPanel background = new JPanel() {
            private Image img = new ImageIcon(getClass().getResource("/images/menu.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, 960, 540, this);
            }
        };
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton newGameBtn = createMenuButton("New Game");
        JButton continueBtn = createMenuButton("Continue");
        JButton exitBtn = createMenuButton("Exit");

        buttonPanel.add(newGameBtn);
        buttonPanel.add(continueBtn);
        buttonPanel.add(exitBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(150, 0, 0, 0);
        background.add(buttonPanel, gbc);

        setLocationRelativeTo(null);
        setVisible(true);

        exitBtn.addActionListener(e -> System.exit(0));

        newGameBtn.addActionListener(e -> {
            LoginDialog dialog = new LoginDialog(this);
            dialog.setVisible(true);

            int userId = dialog.getUserId();
            if (userId == -1) return;

            UserProgressDAO.resetProgress(userId);

            GameLoader loader = new GameLoader();
            GameState state = loader.loadGame(userId);

            JFrame gameFrame = new JFrame("Russel's Sword");
            gameFrame.add(new GamePanel(state));
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);

            dispose();
        });

        continueBtn.addActionListener(e -> {
            LoginDialog dialog = new LoginDialog(this);
            dialog.setVisible(true);

            int userId = dialog.getUserId();
            if (userId == -1) return;

            GameLoader loader = new GameLoader();
            GameState state = loader.loadGame(userId);

            JFrame gameFrame = new JFrame("Russel's Sword");
            gameFrame.add(new GamePanel(state));
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);

            dispose();
        });
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 22));
        btn.setFocusPainted(false);

        Color baseBG = new Color(21, 41, 64);
        Color hoverBG = new Color(31, 61, 94);
        Color borderColor = new Color(179, 179, 24);

        btn.setForeground(borderColor);
        btn.setBackground(baseBG);

        btn.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 4),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                )
        );

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setRolloverEnabled(false);
        btn.setFocusPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverBG);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(baseBG);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverBG);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                btn.setBackground(baseBG);
            }
        });

        return btn;
    }

    private String[] promptLogin() {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Email:", emailField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Login",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) return null;

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password required.");
            return null;
        }

        return new String[]{email, password};
    }

    private void startGame(GameState state) {
        JFrame gameFrame = new JFrame("Russel's Sword");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel panel = new GamePanel(state);
        gameFrame.add(panel);

        gameFrame.pack();
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(menu::new);
    }
}