package org.example.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC loaded");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC NOT found", e);
        }
    }

    private static final String DB_URL = "jdbc:sqlite:game.db";

    public static Integer login(String email, String passwordHash) {
        System.out.println("DB URL = " + DB_URL);
        String sql = """
            SELECT id
            FROM UserAccount
            WHERE email = ? AND password_hash = ?
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT 1 FROM UserAccount WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            return stmt.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void register(String email, String passwordHash) {

        String sql = """
            INSERT INTO UserAccount (email, password_hash)
            VALUES (?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
