package org.example.game;

import org.example.visuals.Npc;
import org.example.database.ZoneData;
import org.example.combat.character;
import org.example.combat.enemy;
import org.example.visuals.Character;
import java.util.List;

public class GameState {
    public int userId;
    public int currentZone;

    public Character visualRussel;

    public character combatRussel;

    public enemy currentEnemy;

    public boolean[] enemyDefeated;
    public boolean[] npcTalked;

    public ZoneData zoneData;
}
