package org.example.combat;

public class input {

    public static volatile String receivedCommand = null;
    public static volatile boolean rhythmHit = false;


    public String waitForGUI() {

        receivedCommand = null;
        while (receivedCommand == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return receivedCommand;
    }

    public static void sendInput(String command) {
        receivedCommand = command;
    }

    public String readInput_line() { return waitForGUI(); }
    public int readInt() { return 0; }
}