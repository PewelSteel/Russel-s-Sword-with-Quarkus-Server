package org.example;

import org.example.game.GameLoader;
import org.example.game.GameState;
import org.example.visuals.Npc;
import org.example.combat.character;
import org.example.database.ZoneData;

import java.util.List;

public class TestRun {

    public static void main(String[] args) {
        System.out.println("=== Starting TestRun ===");

        try {
            int userId = 1;
            GameLoader loader = new GameLoader();

            GameState state = loader.loadGame(userId);

            System.out.println("[OK] Loaded current zone: " + state.currentZone);
            System.out.println("[OK] Loaded abilities: " + state.combatRussel.getAbilities());

            if (state.currentEnemy != null) {
                System.out.println("[OK] Enemy loaded: " + state.currentEnemy.getId());
            } else {
                System.out.println("[WARN] No enemy found for this zone.");
            }

            if (state.zoneData != null) {
                System.out.println("[OK] Zone name: " + state.zoneData.getName());
                System.out.println("[OK] Zone difficulty: " + state.zoneData.getDifficulty());
            } else {
                System.out.println("[WARN] No zone metadata found.");
            }

        } catch (Exception e) {
            System.err.println("=== ERROR OCCURRED ===");
            e.printStackTrace();
        }

        System.out.println("=== TestRun Finished ===");
    }
}