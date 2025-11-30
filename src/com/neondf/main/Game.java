package com.neondf.main;

import com.neondf.entities.*;
import com.neondf.systems.*;
import com.neondf.systems.Menu;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage; // Importante para as imagens
import java.util.ArrayList;
import java.util.Iterator;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

    private Thread thread;
    private boolean running = false;
    private enum STATE { MENU, PLAYING, GAME_OVER }
    private STATE gameState = STATE.MENU;

    private Tower tower;
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Shockwave> shockwaves = new ArrayList<>();

    private WaveManager waveManager;
    private HUD hud;
    private Menu menu;

    private Atiradora atiradora;
    private Medica medica;
    private Escudeira escudeira;

    // --- FUNDOS (BACKGROUNDS) ---
    private BufferedImage[] backgrounds; // Array para guardar as 3 imagens
    private int currentBgIndex = 0;      // Qual imagem estamos usando agora

    private boolean up, down, left, right;
    private boolean isShooting = false;
    private int mouseX = 0;
    private int mouseY = 0;

    public Game() {
        setPreferredSize(new Dimension(800, 600));
        addKeyListener(this);
        addMouseListener(this);

        // --- CARREGANDO OS FUNDOS ---
        backgrounds = new BufferedImage[3]; // Temos 3 imagens
        // Carrega cada uma na memória (lembre-se de corrigir o nome do arquivo 2!)
        backgrounds[0] = new SpriteSheet("/sprites/background1.png").getSprite();
        backgrounds[1] = new SpriteSheet("/sprites/background2.png").getSprite();
        backgrounds[2] = new SpriteSheet("/sprites/background3.png").getSprite();

        tower = new Tower(370, 270);
        waveManager = new WaveManager(enemies);
        hud = new HUD();
        menu = new Menu();

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
        try {
            Point p = this.getMousePosition();
            if (p != null) { mouseX = p.x; mouseY = p.y; }
        } catch (Exception e) { }

        if (gameState == STATE.MENU) {
            menu.tick();
            return;
        }

        if (gameState == STATE.GAME_OVER) return;

        tower.updateDirection(up, down, left, right);
        if (isShooting) tower.tryShoot(bullets);
        tower.tick();

        atiradora.tick(tower, enemies, bullets);
        medica.tick(tower, enemies, bullets);
        escudeira.tick(tower, enemies, bullets);

        hud.tick();
        hud.setWave(waveManager.getCurrentWave());
        waveManager.tick();

        shockwaves.removeIf(sw -> {
            sw.tick();
            return !sw.isActive();
        });

        bullets.removeIf(b -> {
            b.tick();
            return !b.statusBullet();
        });

        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            e.tick(tower.getCenterX(), tower.getCenterY());

            for (Shockwave sw : shockwaves) {
                if (sw.collidesWith(e)) {
                    e.takeDamage(500);
                    if (!e.isAlive()) {
                        hud.addScore(e.getScore());
                        hud.addCoin(e.calculateCoin());
                        waveManager.enemyDied();
                    }
                }
            }

            for (Bullet b : bullets) {
                Rectangle br = new Rectangle((int) b.getX(), (int) b.getY(), 6, 6);
                if (br.intersects(e.getBounds()) && b.isAlive()) {
                    e.takeDamage((b.getBaseDmg()));
                    b.hitEnemy();

                    if(!e.isAlive()){
                        hud.addScore(e.getScore());
                        hud.addCoin(e.calculateCoin());
                        waveManager.enemyDied();
                        tower.addEnergy(10);
                    }
                }
            }

            double dx = e.getX() - tower.getCenterX();
            double dy = e.getY() - tower.getCenterY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 40) {
                tower.takeDamage(e.getDmg());
                hud.damage(e.getDmg());
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

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- DESENHA O FUNDO ---
        // Se a imagem atual existe, desenha ela esticada na tela toda
        if (backgrounds[currentBgIndex] != null) {
            g.drawImage(backgrounds[currentBgIndex], 0, 0, getWidth(), getHeight(), null);
        } else {
            // Se der erro na imagem, usa preto como fallback
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (gameState == STATE.MENU) {
            menu.render(g, mouseX, mouseY);
        }
        else if (gameState == STATE.PLAYING) {
            tower.render(g);
            atiradora.render(g);
            medica.render(g);
            escudeira.render(g);

            for (Bullet b : bullets) b.render(g);
            for (Enemy e : enemies) e.render(g);

            for (Shockwave sw : shockwaves) sw.render(g);

            hud.render(g, tower, atiradora, medica, escudeira);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String name = menu.playerName;
            int nameW = g.getFontMetrics().stringWidth(name);
            g.drawString(name, (int)tower.getCenterX() - nameW/2, (int)tower.getCenterY() - 35);

            if (waveManager.isWaveStarting()) {
                String waveText = "WAVE " + waveManager.getCurrentWave();
                g.setFont(new Font("Consolas", Font.BOLD, 80));
                g.setColor(Color.MAGENTA);
                drawCenteredText(g, waveText, 305);
                g.setColor(Color.CYAN);
                drawCenteredText(g, waveText, 300);

                if (System.currentTimeMillis() % 500 > 250) {
                    g.setFont(new Font("Consolas", Font.BOLD, 30));
                    g.setColor(Color.YELLOW);
                    drawCenteredText(g, "PREPARE-SE!", 380);
                }
            }
        }
        else if (gameState == STATE.GAME_OVER) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(new Font("Consolas", Font.BOLD, 60));
            g.setColor(Color.RED);
            drawCenteredText(g, "GAME OVER", 180);

            g.setFont(new Font("Consolas", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            drawCenteredText(g, "Jogador: " + menu.playerName, 250);
            g.setColor(Color.YELLOW);
            drawCenteredText(g, "Pontuação Final: " + hud.getScore(), 300);
            drawCenteredText(g, "Ondas Sobrevividas: " + waveManager.getCurrentWave(), 340);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.BOLD, 20));
            drawCenteredText(g, "[R] Tentar Novamente", 450);
            drawCenteredText(g, "[ENTER] Voltar ao Menu", 490);
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

        if (gameState == STATE.MENU) {
            if (menu.inControls) { if (key == KeyEvent.VK_ESCAPE) menu.inControls = false; return; }
            if (menu.inSettings) { if (key == KeyEvent.VK_ESCAPE) menu.inSettings = false; return; }

            menu.handleTyping(key, e.getKeyChar());
            if (key == KeyEvent.VK_ENTER) {
                resetGame();
                gameState = STATE.PLAYING;
            }
            return;
        }

        if (gameState == STATE.GAME_OVER) {
            if (key == KeyEvent.VK_R) { resetGame(); gameState = STATE.PLAYING; }
            if (key == KeyEvent.VK_ENTER) { resetGame(); gameState = STATE.MENU; }
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
            if (key == KeyEvent.VK_4) atiradora.upgrade(hud);
            if (key == KeyEvent.VK_5) medica.upgrade(hud);
            if (key == KeyEvent.VK_6) escudeira.upgrade(hud);
            if (key == KeyEvent.VK_7) tower.buyUpgradeMultiShot(hud);

            if (key == KeyEvent.VK_E) {
                if (tower.isUltimateReady()) {
                    shockwaves.add(new Shockwave(tower.getCenterX(), tower.getCenterY()));
                    tower.resetEnergy();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameState == STATE.MENU) {
            int mx = e.getX();
            int my = e.getY();
            if (menu.inControls) {}
            else if (menu.inSettings) {
                if (menu.controlsBtn.contains(mx, my)) menu.inControls = true;
            }
            else {
                if (menu.playBtn.contains(mx, my)) { resetGame(); gameState = STATE.PLAYING; }
                else if (menu.settingsBtn.contains(mx, my)) menu.inSettings = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) up = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) down = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = false;
        if (key == KeyEvent.VK_SPACE) isShooting = false;
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private void resetGame() {
        bullets.clear();
        enemies.clear();
        shockwaves.clear();
        tower = new Tower(370, 270);
        waveManager = new WaveManager(enemies);
        hud = new HUD();
        atiradora = new Atiradora();
        medica = new Medica();
        escudeira = new Escudeira();

        // --- TROCA O FUNDO ---
        // Pega o próximo índice (0 -> 1 -> 2 -> 0...)
        currentBgIndex = (currentBgIndex + 1) % backgrounds.length;
    }

    public Tower getTower() { return tower; }

    public static void main(String[] args) {
        Game game = new Game();
        new Window(800, 600, "🟣 Neon Defense - Ultimate", game);
    }
}