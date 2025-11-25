package com.neondf.main;

import com.neondf.entities.*;
import com.neondf.systems.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;

public class Game extends Canvas implements Runnable, KeyListener {

    private Thread thread;
    private boolean running = false;
    private enum STATE { MENU, PLAYING, GAME_OVER }
    private STATE gameState = STATE.MENU;

    private Tower tower;
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private WaveManager waveManager;
    private HUD hud;

    // --- SUAS NOVAS CLASSES ORGANIZADAS ---
    private Atiradora atiradora;
    private Medica medica;
    private Escudeira escudeira;

    private boolean up, down, left, right;
    private boolean isShooting = false; // CORREÇÃO DO CRASH

    public Game() {
        setPreferredSize(new Dimension(800, 600));
        addKeyListener(this);
        tower = new Tower(370, 270);
        waveManager = new WaveManager(enemies);
        hud = new HUD();

        // Inicializa cada uma individualmente
        atiradora = new Atiradora();
        medica = new Medica();
        escudeira = new Escudeira();

        setFocusable(true);
        requestFocusInWindow();
        requestFocus();
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "Game Thread");
        thread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void tick() {
        if (gameState == STATE.MENU || gameState == STATE.GAME_OVER) return;

        tower.updateDirection(up, down, left, right);
        if (isShooting) tower.tryShoot(bullets);
        tower.tick();

        // --- ATUALIZA CADA TORRE ---
        atiradora.tick(tower, enemies, bullets);
        medica.tick(tower, enemies, bullets);
        escudeira.tick(tower, enemies, bullets);

        hud.tick();
        hud.setWave(waveManager.getCurrentWave());

        bullets.removeIf(b -> {
            b.tick();
            return !b.statusBullet();
        });

        waveManager.tick();

        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            e.tick(tower.getCenterX(), tower.getCenterY());

            for (Bullet b : bullets) {
                Rectangle br = new Rectangle((int) b.getX(), (int) b.getY(), 6, 6);
                if (br.intersects(e.getBounds()) && b.isAlive()) {
                    e.takeDamage((b.getBaseDmg()));
                    b.hitEnemy();
                    if(!e.isAlive()){
                        hud.addScore(e.getScore());
                        hud.addCoin(e.calculateCoin());
                        waveManager.enemyDied();
                    }
                }
            }

            double dx = e.getX() - tower.getCenterX();
            double dy = e.getY() - tower.getCenterY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 40) {
                tower.takeDamage(e.getBaseDamage());
                hud.damage(e.getBaseDamage());
                e.kill();
                waveManager.enemyDied();
                if (tower.getHp() <= 0) gameState = STATE.GAME_OVER;
            }
            if (!e.isAlive()) enemyIt.remove();
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) { createBufferStrategy(3); return; }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Consolas", Font.BOLD, 32));
        g.setColor(Color.CYAN);

        if (gameState == STATE.MENU) {
            drawCenteredText(g, "Neon Defense", 200);
            g.setFont(new Font("Consolas", Font.PLAIN, 22));
            drawCenteredText(g, "Pressione ENTER para começar", 300);
            drawCenteredText(g, "Para controlar a direção da torre,", 400);
            drawCenteredText(g, "pressione W A S D ou as quatro setas", 450);
        }
        else if (gameState == STATE.PLAYING) {
            tower.render(g);
            atiradora.render(g);
            medica.render(g);
            escudeira.render(g);

            for (Bullet b : bullets) b.render(g);
            for (Enemy e : enemies) e.render(g);

            // Passa as 3 torres separadas para o HUD
            hud.render(g, tower, atiradora, medica, escudeira);
        }
        else if (gameState == STATE.GAME_OVER) {
            drawCenteredText(g, "GAME OVER", 250);
            g.setColor(Color.WHITE);
            drawCenteredText(g, "R para reiniciar", 310);
        }
        g.dispose();
        bs.show();
    }

    private void drawCenteredText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (getWidth() - textWidth) / 2, y);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 60.0;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (delta >= 1) { tick(); delta--; }
            render();
        }
        stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if ((gameState == STATE.MENU && key == KeyEvent.VK_ENTER) ||(gameState == STATE.GAME_OVER && key == KeyEvent.VK_R)) {
            resetGame();
            gameState = STATE.PLAYING;
        }
        if (gameState == STATE.PLAYING) {
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) up = true;
            if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) down = true;
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = true;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = true;
            if (key == KeyEvent.VK_SPACE) isShooting = true;

            if (key == KeyEvent.VK_1) tower.buyUpgradeDamage(hud);
            if (key == KeyEvent.VK_2) tower.buyUpgradeSpeed(hud);
            if (key == KeyEvent.VK_3) tower.buyUpgradePierce(hud);

            // Upgrades individuais
            if (key == KeyEvent.VK_4) atiradora.upgrade(hud);
            if (key == KeyEvent.VK_5) medica.upgrade(hud);
            if (key == KeyEvent.VK_6) escudeira.upgrade(hud);
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) up = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) down = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = false;
        if (key == KeyEvent.VK_SPACE) isShooting = false;
    }
    @Override public void keyTyped(KeyEvent e) {}

    private void resetGame() {
        bullets.clear();
        enemies.clear();
        tower = new Tower(370, 270);
        waveManager = new WaveManager(enemies);
        hud = new HUD();
        atiradora = new Atiradora();
        medica = new Medica();
        escudeira = new Escudeira();
    }

    public Tower getTower() { return tower; }

    public static void main(String[] args) {
        Game game = new Game();
        new Window(800, 600, "🟣 Neon Defense - Final", game);
    }
}