package com.neondf.main;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        new Window(800, 600, "🟣 Neon Defense - Ultimate", game);
        game.start();
    }
}
