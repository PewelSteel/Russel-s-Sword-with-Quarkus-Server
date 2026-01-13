package org.example.database;

import org.example.combat.enemy;
import java.sql.*;

public class EnemyDAO {

    public static enemy getEnemyByZone(int zoneId) {
        String sql = "SELECT enemy_id, defeated FROM Enemies WHERE zone_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, zoneId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                boolean defeated = rs.getBoolean("defeated");

                // Anonymous subclass of abstract enemy
                enemy e = new enemy() {
                    @Override
                     public void set() {

                    }
                };

                e.setDefeated(defeated);

                return e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateEnemyState(int enemyId, boolean defeated) {
        String sql = "UPDATE Enemies SET defeated = ? WHERE enemy_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, defeated);
            stmt.setInt(2, enemyId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}