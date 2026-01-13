package org.example.database;

import org.example.auth.Hasher;

import java.sql.*;

public class UserDAO {

    public void registerUser(String email, String password) throws SQLException {
        String sql = "INSERT INTO UserAccount (email, password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            String hashed = Hasher.hash(password);
            stmt.setString(2, hashed);

            stmt.executeUpdate();
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM UserAccount WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public int login(String email, String password) throws SQLException {
        String sql = "SELECT id FROM UserAccount WHERE email = ? AND password_hash = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            String hashedPassword = Hasher.hash(password);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public String getEmailById(int userId) throws SQLException {
        String sql = "SELECT email FROM UserAccount WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("email");
            }
        }
        return null;
    }
}