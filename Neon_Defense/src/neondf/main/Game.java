package com.neondf.main;

import com.neondf.entities.*;
import com.neondf.systems.*;
import com.neondf.systems.Menu;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

    private Thread thread;
    private boolean running = false;
    private enum STATE { MENU, PLAYING, PAUSED, GAME_OVER }
    private STATE gameState = STATE.MENU;

    private Tower tower;
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Shockwave> shockwaves = new ArrayList<>();
    private final ArrayList<DamageText> damageTexts = new ArrayList<>();
    private final ArrayList<AudioPlayer> audioPlayers = new ArrayList<>();

    private WaveManager waveManager;
    private HUD hud;
    private Menu menu;

    private Atiradora atiradora;
    private Medica medica;
    private Escudeira escudeira;

    private BufferedImage[] backgrounds;
    private int currentBgIndex = 0;

    private boolean up, down, left, right;
    private boolean isShooting = false;
    private int mouseX = 0;
    private int mouseY = 0;

    private int shakeTimer = 0;
    private boolean highScoreSaved = false;

    private final AudioPlayer musicaJogo, upgradeSound, ultimateSound, soundTest;

    public Game() {
        setPreferredSize(new Dimension(800, 600));
        addKeyListener(this);
        addMouseListener(this);
        musicaJogo = new AudioPlayer("/music.wav", AudioPlayer.TipoAudio.MUSICA);
        audioPlayers.add(musicaJogo);
        upgradeSound = new AudioPlayer("/cha_ching.wav",  AudioPlayer.TipoAudio.EFEITO);
        audioPlayers.add(upgradeSound);
        ultimateSound = new AudioPlayer("/electricity.wav", AudioPlayer.TipoAudio.EFEITO);
        audioPlayers.add(ultimateSound);
        soundTest = new AudioPlayer("/test.wav", AudioPlayer.TipoAudio.TESTE);
        try {
            backgrounds = new BufferedImage[3];
            backgrounds[0] = new SpriteSheet("/background1.png").getSprite();
            backgrounds[1] = new SpriteSheet("/background2.png").getSprite();
            backgrounds[2] = new SpriteSheet("/background3.png").getSprite();
        } catch (Exception e) {
            System.err.println("Erro ao carregar backgrounds.");
        }

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

        if (gameState == STATE.PAUSED) return;

        // Lógica de Salvar High Score
        if (gameState == STATE.GAME_OVER) {
            if (!highScoreSaved) {
                if (hud.getScore() > menu.highScore) {
                    ScoreManager.saveHighScore(hud.getScore());
                    menu.highScore = hud.getScore();
                }
                highScoreSaved = true;
            }
            return;
        }

        if (shakeTimer > 0) shakeTimer--;

        tower.updateDirection(up, down, left, right);
        if (isShooting) tower.tryShoot(bullets);
        tower.tick();

        atiradora.tick(tower, enemies, bullets);
        medica.tick(tower, enemies, bullets);
        escudeira.tick(tower, enemies, bullets);

        hud.tick();
        hud.setWave(waveManager.getCurrentWave());
        waveManager.tick();

        damageTexts.removeIf(t -> { t.tick(); return !t.isActive(); });
        shockwaves.removeIf(sw -> { sw.tick(); return !sw.isActive(); });
        bullets.removeIf(b -> { b.tick(); return !b.statusBullet(); });

        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            e.tick(tower.getCenterX(), tower.getCenterY());

            for (Shockwave sw : shockwaves) {
                if (sw.collidesWith(e)) {
                    int dmg = 500;
                    e.takeDamage(dmg);
                    damageTexts.add(new DamageText(e.getX(), e.getY()-20, dmg));
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
                    int dmg = b.getBaseDmg();
                    e.takeDamage(dmg);
                    b.hitEnemy();
                    damageTexts.add(new DamageText(e.getX(), e.getY()-20, dmg));

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

        AffineTransform originalTransform = g.getTransform();

        if (shakeTimer > 0 && gameState == STATE.PLAYING) {
            int shakeX = (int) (Math.random() * 10 - 5);
            int shakeY = (int) (Math.random() * 10 - 5);
            g.translate(shakeX, shakeY);
        }

        if (backgrounds[currentBgIndex] != null) {
            g.drawImage(backgrounds[currentBgIndex], 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (gameState == STATE.MENU) {
            menu.render(g, mouseX, mouseY);
        }
        else if (gameState == STATE.PLAYING || gameState == STATE.PAUSED) {
            if(!musicaJogo.isPlaying()){
                musicaJogo.loop();
            }

            tower.render(g);
            atiradora.render(g);
            medica.render(g);
            escudeira.render(g);

            for (Bullet b : bullets) b.render(g);
            for (Enemy e : enemies) e.render(g);
            for (Shockwave sw : shockwaves) sw.render(g);
            for (DamageText t : damageTexts) t.render(g);

            g.setTransform(originalTransform);

            hud.render(g, tower, atiradora, medica, escudeira);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String name = menu.playerName;
            int nameW = g.getFontMetrics().stringWidth(name);
            g.drawString(name, (int)tower.getCenterX() - nameW/2, (int)tower.getCenterY() - 35);

            if (gameState == STATE.PLAYING && waveManager.isWaveStarting()) {
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

            if (gameState == STATE.PAUSED) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setFont(new Font("Verdana", Font.BOLD, 60));
                g.setColor(Color.WHITE);
                drawCenteredText(g, "JOGO PAUSADO", 250);
                g.setFont(new Font("Verdana", Font.PLAIN, 20));
                g.setColor(Color.YELLOW);
                drawCenteredText(g, "Pressione [P] ou [ESC] para continuar", 320);
                g.setColor(Color.RED);
                drawCenteredText(g, "[M] Sair para o Menu", 450);
            }
        }

        // --- AQUI ESTÁ A TELA "SYSTEM FAILURE" (Visual Legal) DE VOLTA ---
        else if (gameState == STATE.GAME_OVER) {
            if(musicaJogo.isPlaying()){
                musicaJogo.stop();
            }

            g.setTransform(originalTransform);
            long now = System.currentTimeMillis();

            // 1. Fundo de Alerta (Vermelho Pulsante)
            float pulse = (float) (Math.sin(now * 0.005) + 1) / 2;
            int alphaRed = 50 + (int)(pulse * 100);

            g.setColor(new Color(10, 0, 0, 240));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(new Color(255, 0, 0, alphaRed));
            g.fillRect(0, 0, getWidth(), getHeight());

            // 2. Scanlines
            g.setColor(new Color(0, 0, 0, 100));
            for (int i = 0; i < getHeight(); i += 4) {
                g.fillRect(0, i, getWidth(), 2);
            }

            // 3. Título "SYSTEM FAILURE" com Glitch
            g.setFont(new Font("Consolas", Font.BOLD, 70));
            String title = "SYSTEM FAILURE";
            int tw = g.getFontMetrics().stringWidth(title);
            int tx = (getWidth() - tw) / 2;
            int ty = 150;

            if (now % 100 > 50) {
                g.setColor(Color.CYAN); g.drawString(title, tx - 5, ty);
                g.setColor(Color.RED);  g.drawString(title, tx + 5, ty);
            }
            g.setColor(Color.WHITE);
            int shakeY = (Math.random() > 0.9) ? (int)(Math.random()*10 - 5) : 0;
            g.drawString(title, tx, ty + shakeY);

            // 4. Relatório estilo Terminal
            int boxW = 400; int boxH = 200;
            int boxX = (getWidth() - boxW) / 2; int boxY = 220;

            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(boxX, boxY, boxW, boxH);
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2));
            g.drawRect(boxX, boxY, boxW, boxH);

            g.setFont(new Font("Consolas", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString("/// RELATÓRIO DE MISSÃO ///", boxX + 60, boxY + 30);

            g.setFont(new Font("Consolas", Font.PLAIN, 18));
            g.setColor(Color.GRAY);
            g.drawString("Operador:", boxX + 20, boxY + 70);
            g.setColor(Color.CYAN);
            g.drawString(menu.playerName, boxX + 250, boxY + 70);

            g.setColor(Color.GRAY);
            g.drawString("Ondas Sobrevividas:", boxX + 20, boxY + 100);
            g.setColor(Color.YELLOW);
            g.drawString("" + waveManager.getCurrentWave(), boxX + 250, boxY + 100);

            g.setColor(Color.GRAY);
            g.drawString("Pontuação Final:", boxX + 20, boxY + 130);
            g.setColor(Color.GREEN);
            g.drawString("" + hud.getScore(), boxX + 250, boxY + 130);

            // --- AVISO DE RECORDE ---
            if (hud.getScore() >= menu.highScore && hud.getScore() > 0) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Consolas", Font.BOLD, 16));
                if (now % 500 > 250) g.drawString("!!! NOVO RECORDE !!!", boxX + 110, boxY + 180);
            }

            // 5. Opções
            if (now % 1000 > 500) g.setColor(Color.WHITE); else g.setColor(Color.GRAY);
            g.setFont(new Font("Consolas", Font.BOLD, 20));
            drawCenteredText(g, "[R] REINICIAR PROTOCOLO", 500);
            drawCenteredText(g, "[ENTER] ABORTAR MISSÃO", 540);
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
            if (menu.inSettings) {
                if (key == KeyEvent.VK_ESCAPE) menu.inSettings = false;
                if (key == KeyEvent.VK_PLUS || key == KeyEvent.VK_EQUALS) {
                    AudioPlayer.incVolume(AudioPlayer.TipoAudio.MUSICA);
                    AudioPlayer.updateAllVolumes(audioPlayers);
                    soundTest.testVolume(AudioPlayer.TipoAudio.MUSICA);
                }
                if (key == KeyEvent.VK_MINUS){
                    AudioPlayer.decVolume(AudioPlayer.TipoAudio.MUSICA);
                    AudioPlayer.updateAllVolumes(audioPlayers);
                    soundTest.testVolume(AudioPlayer.TipoAudio.MUSICA);
                }
                if (key == KeyEvent.VK_P){
                    AudioPlayer.incVolume(AudioPlayer.TipoAudio.EFEITO);
                    AudioPlayer.updateAllVolumes(audioPlayers);
                    soundTest.testVolume(AudioPlayer.TipoAudio.EFEITO);
                }
                if (key == KeyEvent.VK_M) {
                    AudioPlayer.decVolume(AudioPlayer.TipoAudio.EFEITO);
                    AudioPlayer.updateAllVolumes(audioPlayers);
                    soundTest.testVolume(AudioPlayer.TipoAudio.EFEITO);
                }
                if (key == KeyEvent.VK_F) {
                    Enemy.changeDifficulty(Enemy.dificuldade.FACIL);
                    soundTest.testVolume(AudioPlayer.TipoAudio.EFEITO);
                }
                if (key == KeyEvent.VK_N) {
                    Enemy.changeDifficulty(Enemy.dificuldade.NORMAL);
                    soundTest.testVolume(AudioPlayer.TipoAudio.EFEITO);
                }
                if (key == KeyEvent.VK_D){
                    Enemy.changeDifficulty(Enemy.dificuldade.DIFICIL);
                    soundTest.testVolume(AudioPlayer.TipoAudio.EFEITO);
                }
                return;
            }
            menu.handleTyping(key, e.getKeyChar());
            if (key == KeyEvent.VK_ENTER) {
                resetGame();
                gameState = STATE.PLAYING;
            }
            return;
        }

        if (gameState == STATE.PLAYING) {
            if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
                gameState = STATE.PAUSED;
                return;
            }
        }
        else if (gameState == STATE.PAUSED) {
            if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) gameState = STATE.PLAYING;
            if (key == KeyEvent.VK_M) {
                gameState = STATE.MENU;
                menu.updateHighScore();
            }
            return;
        }

        if (gameState == STATE.GAME_OVER) {
            if (key == KeyEvent.VK_R) { resetGame(); gameState = STATE.PLAYING; }
            if (key == KeyEvent.VK_ENTER) {
                resetGame();
                gameState = STATE.MENU;
                menu.updateHighScore();
            }
        }

        if (gameState == STATE.PLAYING) {
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) up = true;
            if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) down = true;
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = true;
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = true;
            if (key == KeyEvent.VK_SPACE) isShooting = true;

            if (key == KeyEvent.VK_1) tower.buyUpgradeDamage(hud, upgradeSound);
            if (key == KeyEvent.VK_2) tower.buyUpgradeSpeed(hud, upgradeSound);
            if (key == KeyEvent.VK_3) tower.buyUpgradePierce(hud, upgradeSound);
            if (key == KeyEvent.VK_4) tower.buyUpgradeMultiShot(hud, upgradeSound);
            if (key == KeyEvent.VK_5) atiradora.upgrade(hud, upgradeSound);
            if (key == KeyEvent.VK_6) medica.upgrade(hud, upgradeSound);
            if (key == KeyEvent.VK_7) escudeira.upgrade(hud, upgradeSound);

            if (key == KeyEvent.VK_E) {
                if (tower.isUltimateReady()) {
                    shockwaves.add(new Shockwave(tower.getCenterX(), tower.getCenterY()));
                    tower.resetEnergy();
                    shakeTimer = 20;
                    ultimateSound.play();
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
    @Override public void keyReleased(KeyEvent e) {
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
        damageTexts.clear();
        shockwaves.clear();
        tower = new Tower(370, 270);
        waveManager = new WaveManager(enemies);
        hud = new HUD();
        atiradora = new Atiradora();
        medica = new Medica();
        escudeira = new Escudeira();
        currentBgIndex = (currentBgIndex + 1) % backgrounds.length;
        highScoreSaved = false;
    }
    public Tower getTower() { return tower; }
    public static void main(String[] args) {
        Game game = new Game();
        new Window(800, 600, "🟣 Neon Defense - Ultimate", game);
    }
}