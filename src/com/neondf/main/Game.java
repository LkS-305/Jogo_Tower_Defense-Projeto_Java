package com.neondf.main;

import com.neondf.entities.*;
import com.neondf.systems.WaveManager;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Game extends Canvas implements Runnable {

    private Thread thread;
    private boolean running = false;

    private Tower tower;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private WaveManager waveManager;
    private Random random = new Random();

    public Game() {
        setPreferredSize(new Dimension(800, 600));

        tower = new Tower(370, 270);
        addMouseMotionListener(new InputHandler(tower));

        waveManager = new WaveManager(enemies);
    }

    // ==========================
    // MÉTODOS DE CONTROLE DO LOOP
    // ==========================

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "Game Thread");
        thread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // ATUALIZAÇÃO DO JOGO (LÓGICA)
    // ==========================

    private void tick() {
        tower.tick();

        // === Sistema de tiro ===
        Bullet newBullet = tower.tryShoot();
        if (newBullet != null) bullets.add(newBullet);

        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            b.tick();
            if (!b.isAlive()) bulletIt.remove();
        }

        // === Sistema de waves ===
        waveManager.tick();

        // === Atualiza inimigos ===
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            e.tick(tower.getCenterX(), tower.getCenterY());

            // Checar colisão com balas
            for (Bullet b : bullets) {
                Rectangle br = new Rectangle((int) b.getX(), (int) b.getY(), 6, 6);
                if (br.intersects(e.getBounds())) {
                    e.kill();
                }
            }

            if (!e.isAlive()) enemyIt.remove();
        }
    }

    // ==========================
    // RENDERIZAÇÃO (DESENHO)
    // ==========================

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        // Fundo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // === Título central ===
        g.setColor(Color.CYAN);
        g.setFont(new Font("Consolas", Font.BOLD, 32));
        String title = "Neon Defense";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(title);
        g.drawString(title, (getWidth() - textWidth) / 2, 50);

        // === Exibe wave atual ===
        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        String waveText = "Wave " + waveManager.getCurrentWave();
        g.drawString(waveText, 20, 80);

        // === Renderiza Torre ===
        tower.render(g);

        // === Renderiza Balas ===
        for (Bullet b : bullets) {
            b.render(g);
        }

        // === Renderiza Inimigos ===
        for (Enemy e : enemies) {
            e.render(g);
        }

        g.dispose();
        bs.show();
    }

    // ==========================
    // LOOP PRINCIPAL
    // ==========================

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                tick();
                delta--;
            }

            render();
        }

        stop();
    }

    // ==========================
    // PONTO DE ENTRADA
    // ==========================

    public static void main(String[] args) {
        Game game = new Game();
        new Window(800, 600, "🟣 Neon Defense", game);
    }
}
