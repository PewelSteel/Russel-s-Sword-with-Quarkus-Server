package org.example.game;
import org.example.combat.enemy;
import javax.swing.*;
import java.awt.*;
import org.example.combat.character;
import org.example.visuals.Background;

public class GamePanel extends JPanel {

    private GameState state;

    public GamePanel(GameState state) {
        this.state = state;

        setPreferredSize(new Dimension(960, 540));
        setLayout(new BorderLayout());

        Background background = new Background(state);
        add(background, BorderLayout.CENTER);

        setFocusable(true);
        requestFocusInWindow();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    public void onEnemyDefeated(enemy enemy) {
        enemy.setDefeated(true);
        SaveManager.save(state);
    }

    public void onZoneChange(int newZone) {
        state.currentZone = newZone;
        SaveManager.save(state);
    }

    public void onAbilityLearned(String ability, int damage) {
        state.combatRussel.add_ability(ability, damage);
        SaveManager.save(state);
    }

}