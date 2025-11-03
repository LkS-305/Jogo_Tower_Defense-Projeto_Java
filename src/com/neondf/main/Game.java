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
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private WaveManager waveManager;
    private HUD hud;

    private boolean up, down, left, right;

    public Game() {
        setPreferredSize(new Dimension(800, 600));
        addKeyListener(this);

        tower = new Tower(370, 270);
        waveManager = new WaveManager(enemies);
        hud = new HUD();

        setFocusable(true);
        requestFocusInWindow();
        requestFocus();
    }

    // ==============================
    // CONTROLE DO LOOP
    // ==============================

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

    // ==============================
    // LÓGICA PRINCIPAL
    // ==============================

    private void tick() {
        if (gameState == STATE.MENU) return;
        if (gameState == STATE.GAME_OVER) return;

        // Atualiza direção da torre
        tower.updateDirection(up, down, left, right);

        tower.tick();
        hud.tick();
        hud.setWave(waveManager.getCurrentWave());

        // Tiro automático
        Bullet newBullet = tower.tryShoot();
        if (newBullet != null) bullets.add(newBullet);

        // Balas
        bullets.removeIf(b -> {
            b.tick();
            return !b.isAlive();
        });

        // Waves
        waveManager.tick();

        // Inimigos
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            e.tick(tower.getCenterX(), tower.getCenterY());

            for (Bullet b : bullets) {
                Rectangle br = new Rectangle((int) b.getX(), (int) b.getY(), 6, 6);
                if (br.intersects(e.getBounds())) {
                    e.kill();
                    hud.addScore(10);
                    hud.addCoin(); // 💠 drop de NeonCoin
}

            }

            double dx = e.getX() - tower.getCenterX();
            double dy = e.getY() - tower.getCenterY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 40) {
                e.kill();
                tower.takeDamage(10);
                hud.damage(10);
                if (tower.getHp() <= 0) gameState = STATE.GAME_OVER;
            }

            if (!e.isAlive()) enemyIt.remove();
        }
    }

    // ==============================
    // RENDERIZAÇÃO
    // ==============================

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
            drawCenteredText(g, "Use W, A, S, D para girar a torre", 340);
        } 
        else if (gameState == STATE.PLAYING) {
            String title = "Neon Defense";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(title);
            g.drawString(title, (getWidth() - textWidth) / 2, 50);

            tower.render(g);
            for (Bullet b : bullets) b.render(g);
            for (Enemy e : enemies) e.render(g);

            hud.render(g);
        } 
        else if (gameState == STATE.GAME_OVER) {
            drawCenteredText(g, "GAME OVER", 250);
            g.setFont(new Font("Consolas", Font.PLAIN, 22));
            g.setColor(Color.WHITE);
            drawCenteredText(g, "Pressione R para reiniciar", 310);
        }

        g.dispose();
        bs.show();
    }

    private void drawCenteredText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (getWidth() - textWidth) / 2, y);
    }

    // ==============================
    // LOOP
    // ==============================

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 60.0;
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

    // ==============================
    // INPUT
    // ==============================

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameState == STATE.MENU && key == KeyEvent.VK_ENTER) {
            resetGame();
            gameState = STATE.PLAYING;
        }

        if (gameState == STATE.GAME_OVER && key == KeyEvent.VK_R) {
            resetGame();
            gameState = STATE.PLAYING;
        }

        if (gameState == STATE.PLAYING) {
            if (key == KeyEvent.VK_W) up = true;
            if (key == KeyEvent.VK_S) down = true;
            if (key == KeyEvent.VK_A) left = true;
            if (key == KeyEvent.VK_D) right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_D) right = false;
    }

    @Override public void keyTyped(KeyEvent e) {}

    // ==============================
    // RESET
    // ==============================

    private void resetGame() {
        bullets.clear();
        enemies.clear();
        tower = new Tower(370, 270);
        waveManager = new WaveManager(enemies);
        hud = new HUD();
    }

    public Tower getTower() { return tower; }

    // ==============================
    // MAIN
    // ==============================

    public static void main(String[] args) {
        Game game = new Game();
        new Window(800, 600, "🟣 Neon Defense", game);
    }
}
