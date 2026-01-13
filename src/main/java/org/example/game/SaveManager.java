package org.example.game;

import org.example.database.UserProgressDAO;
import org.example.database.EnemyDAO;
import org.example.combat.character;

public class SaveManager {

    public static void save(GameState state) {

        if (state == null) return;

        UserProgressDAO.updateZone(state.userId, state.currentZone);

        if (state.combatRussel != null && state.combatRussel.getAbilities() != null) {
            UserProgressDAO.updateAbilities(
                    state.userId,
                    state.combatRussel.getAbilities()
            );
        }

        if (state.combatRussel != null) {
            UserProgressDAO.updateStats(
                    state.userId,
                    state.combatRussel.getHp(),
                    state.combatRussel.getLevel()
            );
        }

        if (state.currentEnemy != null) {
            EnemyDAO.updateEnemyState(
                    state.currentEnemy.getId(),
                    state.currentEnemy.isDefeated()
            );
        }

    }

    public static GameState load(int userId) {

        GameState state = new GameState();
        state.userId = userId;

        state.currentZone = UserProgressDAO.getCurrentZone(userId);

        int[] stats = UserProgressDAO.getStats(userId);
        int hp = stats[0];
        int level = stats[1];

        var abilities = UserProgressDAO.getAbilities(userId);

        state.combatRussel = new character(
                "Russel",
                hp,
                level,
                abilities
        );

        return state;
    }


}
