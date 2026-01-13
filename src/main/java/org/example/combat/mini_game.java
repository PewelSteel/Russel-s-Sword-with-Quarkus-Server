package org.example.combat;

import java.util.Random;

public class mini_game {

    private static output outputD = new output();

    public static boolean playRhythmEvent() {
        Random rng = new Random();

        int rounds = 3;
        long window = 1000;

        outputD.display("DODGE! Press SPACE when you see \"NOW!\"");
        sleepFor(1500);

        for (int i = 1; i <= rounds; i++) {
            outputD.display("Round " + i + "/" + rounds + "...");

            long delay = 1000 + rng.nextInt(1000);
            sleepFor(delay);

            input.rhythmHit = false;

            outputD.display("NOW!");

            long startTime = System.currentTimeMillis();
            boolean hit = false;

            while (System.currentTimeMillis() - startTime < window) {
                if (input.rhythmHit) {
                    hit = true;
                    break;
                }
                try { Thread.sleep(10); } catch (Exception e) {}
            }

            if (!hit) {
                outputD.display("✘ TOO SLOW!");
                sleepFor(1000);
                return false;
            } else {
                outputD.display("✔ HIT!");
            }
        }

        return true;
    }

    private static void sleepFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}