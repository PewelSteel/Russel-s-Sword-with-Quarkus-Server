package org.example.game;

import org.example.database.UserProgressDAO;
import org.example.database.AbilityDAO;
import org.example.database.EnemyDAO;
import org.example.database.ZoneDAO;
import org.example.combat.character;
import org.example.combat.enemy;
import org.example.database.ZoneData;
import org.example.database.ZoneData;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

public class GameLoader {

    public GameState loadGame(int userId) {

        GameState state = new GameState();
        state.userId = userId;

        state.currentZone = UserProgressDAO.getCurrentZone(userId);

        int[] stats = UserProgressDAO.getStats(userId);
        int hp = stats[0];
        int level = stats[1];

        Map<String, Integer> abilities = new HashMap<>();
        try {
            abilities = AbilityDAO.getAbilities(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        state.combatRussel = new character("Russel", hp, level, abilities);
        state.visualRussel = new org.example.visuals.Character(200, 260, 2);

        state.currentEnemy = EnemyDAO.getEnemyByZone(state.currentZone);

        state.zoneData = ZoneDAO.getZone(state.currentZone);

        return state;
    }
}