package org.example.visuals;

import org.example.combat.main_combat;
import org.example.combat.output;
import org.example.combat.input;

import org.example.combat.battle;
import org.example.combat.character;
import org.example.combat.enemy;
import org.example.combat.Guardian;
import org.example.combat.Warden;
import org.example.combat.Strigoi;
import org.example.game.GameState;
import org.example.game.SaveManager;
import org.jline.terminal.TerminalBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.ArrayList;

public class Background extends JPanel implements KeyListener, ActionListener {

    private BufferedImage background;
    private Character russel;
    private final int BG_SCALE = 3;

    private volatile boolean enterPressed = false;

    private boolean movingLeft = false;
    private boolean movingRight = false;

    private Timer timer;
    private long lastMoveTime = System.currentTimeMillis();

    private int currentZone = 0;
    private final String[] zoneBackgrounds = {
            "/images/backgroundfield.png",
            "/images/backgroundforest.png",
            "/images/backgrounddeath.png",
            "/images/backgroundmines.png",
            "/images/backgroundmpeak.png"
    };

    private boolean[] npcTalkedStatus = new boolean[zoneBackgrounds.length];

    private boolean[] enemyDefeatedStatus = new boolean[zoneBackgrounds.length];

    private java.util.List<String> currentDialogueLines = new java.util.ArrayList<>();

    private ArrayList<Npc> npcs = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();

    private boolean inDialogue = false;
    private Npc currentNpc = null;
    private Enemy currentEnemy = null;

    private JPanel buttonPanel;
    private JPanel mainCombatPanel;
    private JPanel abilityPanel;

    private main_combat mainCombat = new main_combat();

    private GameState gameState;

    public Background(GameState state) {
        this();

        this.currentZone = state.currentZone;
        this.gameState = state;

        if (state.visualRussel != null) {
            this.russel = state.visualRussel;
        }

        if (state.enemyDefeated != null) {
            this.enemyDefeatedStatus = state.enemyDefeated;
        }

        if (state.npcTalked != null) {
            this.npcTalkedStatus = state.npcTalked;
        }

        try {
            loadBackground();
            loadEndingImage();

        } catch (ZoneException e) {
            e.printStackTrace();
        }
    }

    public Background() {
        this.setLayout(null);
        setupCombatButtons();
        russel = new Character(200, 260, 2);

        try {
            loadBackground();
            loadEndingImage();

        } catch (ZoneException e) {
            System.out.println("Fatal error loading initial zone:");
            e.printStackTrace();
            System.exit(1);
        }

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        timer = new Timer(16, this);
        timer.start();
    }

    private enum GameMode {
        PLAYING,
        ENDING
    }
    private GameMode gameMode = GameMode.PLAYING;
    private BufferedImage endingImage;

    private void loadEndingImage() {
        try {
            var stream = getClass().getResourceAsStream("/images/endingscreen.png");
            if (stream != null) {
                endingImage = ImageIO.read(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void triggerEnding() {
        gameMode = GameMode.ENDING;
        movingLeft = false;
        movingRight = false;
        inDialogue = false;

        npcs.clear();
        enemies.clear();

        repaint();
    }

    private void loadBackground() throws ZoneException {
        try {
            var stream = getClass().getResourceAsStream(zoneBackgrounds[currentZone]);
            if (stream == null) {
                throw new ZoneException("Resource not found: " + zoneBackgrounds[currentZone]);
            }
            background = ImageIO.read(stream);
            if (background == null) {
                throw new ZoneException("Background image is null for zone " + currentZone);
            }
        } catch (Exception e) {
            throw new ZoneException("Failed to load background for zone " + currentZone, e);
        }
        spawnNpcsForZone(currentZone);
        spawnEnemiesForZone(currentZone);
    }

    private void spawnNpcsForZone(int zone) {
        npcs.clear();
        Npc newNpc = null;

        switch (zone) {
            case 0:
                newNpc = new Npc("/images/brother1.png", 600, 130, 1);
                break;
            case 4:
                newNpc = new Npc("/images/brother2.png", 500, 200, 1);
                break;
        }

        if (newNpc != null) {
            if (npcTalkedStatus[zone]) {
                newNpc.setTalked(true);
            }
            npcs.add(newNpc);
        }
    }

    private void spawnEnemiesForZone(int zone) {
        enemies.clear();

        if (enemyDefeatedStatus[zone]) {
            return;
        }

        switch (zone) {
            case 1: enemies.add(new Enemy("/images/forestwarden.png", 500, 50, 2)); break;
            case 2: enemies.add(new Enemy("/images/firewarden.png", 500, 50, 2)); break;
            case 3: enemies.add(new Enemy("/images/mineswarden.png", 500, 60, 4)); break;
        }
    }

    private void setupCombatButtons() {
        buttonPanel = new JPanel(new CardLayout());
        buttonPanel.setBounds(50, 450, 860, 100); // Position below text box
        buttonPanel.setOpaque(false);

        mainCombatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        mainCombatPanel.setOpaque(false);

        JButton btnAbility = createStyledButton("ABILITY");
        JButton btnInfo = createStyledButton("INFO");
        JButton btnShield = createStyledButton("SHIELD");
        JButton btnHeal = createStyledButton("HEAL");

        btnAbility.addActionListener(e -> org.example.combat.input.sendInput("ABILITY"));
        btnInfo.addActionListener(e -> org.example.combat.input.sendInput("INFO"));
        btnShield.addActionListener(e -> org.example.combat.input.sendInput("SHIELD"));
        btnHeal.addActionListener(e -> org.example.combat.input.sendInput("HEAL"));

        mainCombatPanel.add(btnAbility);
        mainCombatPanel.add(btnInfo);
        mainCombatPanel.add(btnShield);
        mainCombatPanel.add(btnHeal);

        abilityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        abilityPanel.setOpaque(false);

        buttonPanel.add(mainCombatPanel, "MAIN");
        buttonPanel.add(abilityPanel, "ABILITIES");

        this.add(buttonPanel);
        buttonPanel.setVisible(false);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 50));
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    public void showCombatMenu() {
        buttonPanel.setVisible(true);
        CardLayout cl = (CardLayout) (buttonPanel.getLayout());
        cl.show(buttonPanel, "MAIN");
        repaint();
    }

    public void showAbilityMenu(java.util.Map<String, Integer> abilities) {
        abilityPanel.removeAll();

        for (String abilityName : abilities.keySet()) {
            JButton btn = createStyledButton(abilityName);
            btn.addActionListener(e -> org.example.combat.input.sendInput(abilityName));
            abilityPanel.add(btn);
        }

        JButton btnBack = createStyledButton("BACK");
        btnBack.setBackground(new Color(100, 30, 30));
        btnBack.addActionListener(e -> org.example.combat.input.sendInput("BACK"));
        abilityPanel.add(btnBack);

        CardLayout cl = (CardLayout) (buttonPanel.getLayout());
        cl.show(buttonPanel, "ABILITIES");
        buttonPanel.validate();
        repaint();
    }

    public void hideCombatMenu() {
        buttonPanel.setVisible(false);
        repaint();
    }

    public void appendCombatText(String text) {
        currentDialogueLines.add(text);
        if (currentDialogueLines.size() > 6) currentDialogueLines.remove(0);
        repaint();
        try { Thread.sleep(1500); } catch (Exception e) {}
    }

    public void clearCombatText() {
        currentDialogueLines.clear();
        repaint();
    }

    public void waitForEnterThread() {
        requestFocusInWindow();

        currentDialogueLines.add("[Press Enter]");
        repaint();

        enterPressed = false;
        while (!enterPressed) {
            try { Thread.sleep(10); } catch (Exception e) {}
        }

        if (!currentDialogueLines.isEmpty()) {
            currentDialogueLines.remove(currentDialogueLines.size() - 1);
        }

        repaint();
    }

    private void drawEndingScreen(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (endingImage != null) {
            g2d.drawImage(
                    endingImage,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    null
            );
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (gameMode == GameMode.ENDING) {
            drawEndingScreen((Graphics2D) g);
            return;
        }


        if (inDialogue) {
            if (background != null) {
                int scaledWidth = background.getWidth() * BG_SCALE;
                int scaledHeight = background.getHeight() * BG_SCALE;
                g2d.drawImage(background, 0, 0, scaledWidth, scaledHeight, null);
            }

            BufferedImage russelImg = russel.getIdleImage();
            if (russelImg != null) {
                int w = russelImg.getWidth() * 4;
                int h = russelImg.getHeight() * 4;
                g2d.drawImage(russelImg, -50, getHeight() - h + 100, w, h, null);
            }

            if (currentNpc != null) {
                BufferedImage npcImg = currentNpc.getImage();
                if (npcImg != null) {
                    int w = npcImg.getWidth() * 2;
                    int h = npcImg.getHeight() * 2;
                    g2d.drawImage(npcImg, getWidth() - w, getHeight() - h + 50, w, h, null);
                }
            }

            if (currentEnemy != null) {
                BufferedImage enemyImg = currentEnemy.getImage();
                if (enemyImg != null) {
                    int w = enemyImg.getWidth() * 3;
                    int h = enemyImg.getHeight() * 3;
                    if (currentZone == 3)
                    { w = w * 2; h = h * 2; }
                    g2d.drawImage(enemyImg, getWidth() - w + 100, getHeight() - h + 150, w, h, null);
                }
            }

            int lineSpacing = 40;
            int dynamicHeight = Math.max(150, 80 + (currentDialogueLines.size() * lineSpacing));

            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(50, 50, getWidth() - 100, dynamicHeight, 20, 20);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 26));

            for (int j = 0; j < currentDialogueLines.size(); j++) {
                int yPosition = 110 + (j * lineSpacing);
                g2d.drawString(currentDialogueLines.get(j), 70, yPosition);
            }
            return;
        }

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        if (background != null) {
            int scaledWidth = background.getWidth() * BG_SCALE;
            int scaledHeight = background.getHeight() * BG_SCALE;
            g2d.drawImage(background, 0, 0, scaledWidth, scaledHeight, null);
        }

        long now = System.currentTimeMillis();
        russel.setIdle(now - lastMoveTime > 1000);

        for (Npc npc : npcs) npc.draw(g2d);
        for (Enemy e : enemies) e.draw(g2d);
        russel.draw(g2d, movingLeft, movingRight);
    }

    private void runCombat(int zoneIndex) {
        try {
            currentDialogueLines.clear();
            movingLeft = false;
            movingRight = false;

            output.setGui(this);
            enemy combatEnemy = null;
            mainCombat.main(zoneIndex);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            output.setGui(null);
            inDialogue = false;
            currentNpc = null;
            currentEnemy = null;
            enemyDefeatedStatus[zoneIndex] = true;
            try { loadBackground(); } catch (ZoneException e) { e.printStackTrace(); }
            repaint();

        }
        gameState.currentZone = currentZone;
        gameState.enemyDefeated = enemyDefeatedStatus;
        gameState.npcTalked = npcTalkedStatus;
        gameState.visualRussel = russel;
        SaveManager.save(gameState);
    }

    private void runDialogue() {
        try {
            currentDialogueLines.clear();
            movingLeft = false;
            movingRight = false;

            String[] textToDisplay;
            if(currentZone==0) {
                textToDisplay = new String[]{
                        "Boy, if you seek the mightiest blade this ",
                        "land has ever known, you must earn it.",
                        "The sword you seek is not just forged in fire,",
                        "but also in courage.",
                        "Venture into the Forest of Whispers for sacred wood, ",
                        "steal fire from the Eternal Pyre, and mine cursed metal ",
                        "from the Hollowdeep Caverns.",
                        "Only then may you climb the Peak of Echoes,",
                        "in order to forge your fate.",
                        "My brother waits atop the mountain.",
                        "But he only crafts for heroes..."
                };
            } else {
                textToDisplay = new String[]{
                        "Hello, Russel! My brother told me you might come,",
                        "but I didn't think you would.",
                        "Most people who started this journey",
                        "never made it out of the forest",
                        "You brought the wood, the fire, and the metal.",
                        "But are you ready?",
                        "A sword is only as strong as the hand that wields it.",
                        "Let the forge sing your name, Russel..."
                };
            }

            for (String line : textToDisplay) {
                currentDialogueLines.add(line);
                if (currentDialogueLines.size() > 4) currentDialogueLines.remove(0);
                repaint();

                enterPressed = false;
                while(!enterPressed) { Thread.sleep(10); }
                Thread.sleep(150);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            inDialogue = false;
            currentNpc = null;

            npcTalkedStatus[currentZone] = true;

            if (currentZone == 4) {
                triggerEnding();
                return;
            }

            gameState.currentZone = currentZone;
            SaveManager.save(gameState);
            try { loadBackground(); } catch (ZoneException e) { e.printStackTrace(); }
            repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 5;
        boolean moved = false;
        int charWidth = 100 * 2;
        int panelWidth = getWidth();
        int leftLimit = (currentZone == 0) ? 0 : Integer.MIN_VALUE;
        int rightLimit = (currentZone == zoneBackgrounds.length - 1) ? panelWidth / 2 - charWidth / 2 : Integer.MAX_VALUE;

        if (!inDialogue) {
            if (movingLeft && russel.getX() > leftLimit) {
                russel.move(-speed); moved = true;
            }
            if (movingRight && russel.getX() < rightLimit) {
                russel.move(speed); moved = true;
            }
        }
        if (moved) lastMoveTime = System.currentTimeMillis();

        for (Npc npc : npcs) {

            if (!npc.hasTalked() && !inDialogue) {
                double dx = russel.getX() - npc.getX();
                double dy = russel.getY() - npc.getY();
                if (Math.sqrt(dx * dx + dy * dy) <= 200) {
                    inDialogue = true;
                    currentNpc = npc;
                    npcs.clear(); enemies.clear();

                    new Thread(this::runDialogue).start();
                    break;
                }
            }
        }

        for (Enemy enemy : enemies) {
            double xDist = Math.abs(russel.getX() - enemy.getX());


            if (xDist<=100 && !inDialogue) {

                inDialogue = true;
                currentEnemy=enemy;
                npcs.clear(); enemies.clear();

                final int z = currentZone;
                new Thread(() -> runCombat(z)).start();

                break;
            }
        }

        int halfChar = charWidth / 2;
        if (russel.getX() + halfChar > panelWidth && currentZone < zoneBackgrounds.length - 1) {
            currentZone++;
            try { loadBackground(); } catch (ZoneException ex) { ex.printStackTrace(); }
            russel.setPosition(-halfChar, russel.getY());
        }
        gameState.currentZone = currentZone;
        SaveManager.save(gameState);
        if (russel.getX() + halfChar < 0 && currentZone > 0) {
            currentZone--;
            try { loadBackground(); } catch (ZoneException ex) { ex.printStackTrace(); }
            russel.setPosition(panelWidth - halfChar, russel.getY());
        }
        gameState.currentZone = currentZone;
        SaveManager.save(gameState);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) movingLeft = true;
        if (e.getKeyCode() == KeyEvent.VK_D) movingRight = true;
        if (e.getKeyCode() == KeyEvent.VK_ENTER) enterPressed = true;

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            org.example.combat.input.rhythmHit = true;
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) movingLeft = false;
        if (e.getKeyCode() == KeyEvent.VK_D) movingRight = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public Dimension getPreferredSize() {
        if (background != null) return new Dimension(background.getWidth() * BG_SCALE, background.getHeight() * BG_SCALE);
        return new Dimension(960, 540);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

}