package org.example.menu;

import org.example.auth.AuthClient;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private JTextField emailField;
    private JPasswordField passwordField;
    private int userId = -1;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        setSize(420, 260);
        setResizable(false);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel();
        root.setBackground(new Color(21, 41, 64));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        root.setLayout(new BorderLayout(0, 15));
        setContentPane(root);

        JLabel title = new JLabel("PLAYER LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 24));
        title.setForeground(new Color(179, 179, 24));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 1, 0, 12));
        form.setOpaque(false);

        emailField = createField("Email");
        passwordField = createPasswordField("Password");

        form.add(emailField);
        form.add(passwordField);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 15, 0));
        buttons.setOpaque(false);

        JButton loginBtn = createButton("LOGIN");
        JButton registerBtn = createButton("REGISTER");

        buttons.add(loginBtn);
        buttons.add(registerBtn);

        root.add(buttons, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password required");
            return;
        }

        int id = AuthClient.login(email, password);

        if (id != -1) {
            userId = id;
            dispose();
        } else {
            showError("Invalid email or password");
        }
    }

    private void register() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password required");
            return;
        }

        int id = AuthClient.register(email, password);

        if (id != -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Account created.\nYou can now log in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            showError("User already exists");
        }
    }

    private JTextField createField(String placeholder) {
        JTextField field = new JTextField();
        styleField(field);
        field.setToolTipText(placeholder);
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        styleField(field);
        field.setToolTipText(placeholder);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(25, 52, 82));
        field.setCaretColor(Color.WHITE);
        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(179, 179, 24), 2),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                )
        );
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setFocusPainted(false);

        btn.setForeground(new Color(179, 179, 24));
        btn.setBackground(new Color(25, 52, 82));
        btn.setBorder(BorderFactory.createLineBorder(new Color(179, 179, 24), 3));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(31, 69, 110));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(25, 52, 82));
            }
        });

        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public int getUserId() {
        return userId;
    }
}
