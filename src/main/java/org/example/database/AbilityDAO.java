package org.example.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AbilityDAO {

    public static Map<String, Integer> getAbilities(int userId) throws SQLException {
        String sql = "SELECT ability_name, damage FROM RusselAbilities WHERE user_id = ?";

        Map<String, Integer> abilities = new HashMap<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("ability_name");
                int dmg = rs.getInt("damage");
                abilities.put(name, dmg);
            }
        }

        return abilities;
    }

    public static void addAbility(int userId, String name, int damage) throws SQLException {
        String sql = "INSERT INTO RusselAbilities (user_id, ability_name, damage) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setInt(3, damage);
            stmt.executeUpdate();
        }
    }
}