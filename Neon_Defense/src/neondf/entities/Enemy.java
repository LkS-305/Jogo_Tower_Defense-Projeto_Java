package com.neondf.entities;

import com.neondf.systems.SpriteSheet;
import java.awt.*;
import java.awt.geom.AffineTransform; // Importante para girar
import java.awt.image.BufferedImage;

public class Enemy {

    protected double x, y, speed;
    protected int dmg, hp, score, shield;
    protected boolean alive = true;

    protected int scale = 2;

    // Animação de Sprite (Para os inimigos avançados que tiverem imagem)
    private BufferedImage[] frames;
    private int maxFrames;
    private int curFrame = 0;
    private int frameCount = 0;
    private int frameDelay = 10;

    // ...
    protected int width;   // <--- MUDOU PARA PROTECTED
    protected int height;  // <--- MUDOU PARA PROTECTED
    // ...
    // Animação Procedural (Para o inimigo básico sem imagem)
    private double rotationAngle = 0; // Para fazê-lo girar

    public enum dificuldade{
        FACIL,
        NORMAL,
        DIFICIL
    }
    private static dificuldade dificuldadeJogo = dificuldade.NORMAL;
    protected static int baseDmg = 10, baseHP = 10, baseScore = 100000;
    protected static double baseSpeed = 1.0;

    // --- CONSTRUTOR 1: DETALHADO (Para Inimigo1, Inimigo2...) ---
    public Enemy(double x, double y, String spritePath, int frameWidth, int frameHeight, int numFrames, int rowIndex) {
        this.x = x;
        this.y = y;
        this.width = frameWidth;
        this.height = frameHeight;
        this.maxFrames = numFrames;

        if(dificuldadeJogo == dificuldade.NORMAL){
            this.speed = baseSpeed;
            this.dmg = baseDmg;
            this.hp = baseHP;
            this.score = baseScore;
        } else if (dificuldadeJogo == dificuldade.FACIL) {
            this.speed = baseSpeed * 0.7;
            this.dmg = baseDmg / 2;
            this.hp = baseHP / 2;
            this.score = baseScore * 2;
        } else if (dificuldadeJogo == dificuldade.DIFICIL) {
            this.speed = baseSpeed * 1.5;
            this.dmg = (baseDmg * 3) / 2;
            this.hp = (baseHP * 3) / 2;
            this.score = (baseScore * 4) / 5;
        }
        this.shield = 0;

        try {
            SpriteSheet sheet = new SpriteSheet(spritePath);
            frames = new BufferedImage[maxFrames];
            int startY = rowIndex * height;
            for(int i = 0; i < maxFrames; i++) {
                frames[i] = sheet.getSprite(i * width, startY, width, height);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprite: " + spritePath);
        }
    }

    // --- CONSTRUTOR 2: BÁSICO (O INIMIGO "FRACO") ---
    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        if(dificuldadeJogo == dificuldade.NORMAL){
            this.speed = baseSpeed;
            this.dmg = baseDmg;
            this.hp = baseHP;
            this.score = baseScore;
        } else if (dificuldadeJogo == dificuldade.FACIL) {
            this.speed = baseSpeed * 0.7;
            this.dmg = baseDmg / 2;
            this.hp = baseHP / 2;
            this.score = baseScore * 2;
        } else if (dificuldadeJogo == dificuldade.DIFICIL) {
            this.speed = baseSpeed * 1.5;
            this.dmg = (baseDmg * 3) / 2;
            this.hp = (baseHP * 3) / 2;
            this.score = (baseScore * 4) / 5;
        }
        this.shield = 0;

        // Não carregamos imagem aqui.
        // O render vai detectar que 'frames' é nulo e desenhar o Losango Neon.
    }

    public void tick(double targetX, double targetY) {
        if (!alive) return;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 1) {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }

        // Lógica de Sprite
        if (frames != null) {
            frameCount++;
            if (frameCount >= frameDelay) {
                frameCount = 0;
                curFrame++;
                if (curFrame >= maxFrames) curFrame = 0;
            }
        } else {
            // Lógica do Inimigo Geométrico: GIRAR!
            rotationAngle += 0.1; // Velocidade do giro
        }
    }

    public void render(Graphics2D g) {
        // Se tiver sprite (Inimigo 1 e 2), desenha a imagem
        if (frames != null && frames[curFrame] != null) {
            int drawWidth = width * scale;
            int drawHeight = height * scale;
            g.drawImage(frames[curFrame], (int)x - drawWidth/2, (int)y - drawHeight/2, drawWidth, drawHeight, null);
        }
        // Se NÃO tiver sprite (Inimigo Básico), desenha o LOSANGO NEON
        else {
            AffineTransform old = g.getTransform();

            // Move para a posição do inimigo
            g.translate(x, y);
            // Gira
            g.rotate(rotationAngle);

            // Desenha um quadrado preenchido (Vermelho escuro)
            g.setColor(new Color(150, 0, 50));
            g.fillRect(-10, -10, 20, 20); // Centralizado

            // Desenha a borda brilhante (Vermelho Neon)
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2)); // Linha mais grossa
            g.drawRect(-10, -10, 20, 20);

            // Restaura a rotação para não bugar o resto do jogo
            g.setTransform(old);
        }
    }

    public Rectangle getBounds() {
        if (frames != null) {
            int drawWidth = width * scale;
            int drawHeight = height * scale;
            return new Rectangle((int) x - drawWidth/4, (int) y - drawHeight/4, drawWidth/2, drawHeight/2);
        } else {
            // Hitbox do Losango
            return new Rectangle((int) x - 10, (int) y - 10, 20, 20);
        }
    }

    public static dificuldade getDificuldade() {
        return dificuldadeJogo;
    }

    public static void changeDifficulty(dificuldade dif){
        if(dificuldadeJogo != dif){
            dificuldadeJogo = dif;
        }
    }

    // Getters e Setters
    public double getSpeed(){ return speed; }
    public void setSpeed(double speed){ this.speed = speed; }
    public int getDmg(){ return dmg; }
    public void setDmg(int damage){ this.dmg = damage; }
    public int getHp(){ return hp; }
    public void setHp(int hp){ this.hp = hp; }
    public int getScore(){ return score; }
    public void setScore(int score){ this.score = score; }
    public int getShield() { return shield; }
    public void setShield(int shield) { this.shield = shield; }
    public int getBaseDmg(){ return baseDmg; }
    public int getBaseHp(){ return baseHP; }
    public int getBaseScore(){ return baseScore; }
    public double getBaseSpeed(){ return baseSpeed; }
    public boolean isAlive() { return alive; }
    public double getX() { return x; }
    public double getY() { return y; }
    public void changeDmg(double multiplier){ this.dmg = (int) (multiplier * this.dmg); }
    public void changeHP(double multiplier){ this.hp = (int) (multiplier * this.hp); }
    public void changeScore(double multiplier){ this.score = (int) (multiplier * this.score); }
    public void changeSpeed(double multiplier){ this.speed = multiplier * this.speed; }
    public int calculateCoin(){ return this.score/10; }
    public void kill() { if (this.alive) { this.alive = false; } }
    public void takeDamage(int damage) {
        if(this.shield > 0){
            this.shield -= damage;
            if(this.shield <= 0){ this.shield = 0; }
        } else{
            this.setHp(this.getHp() - damage);
            if(this.getHp() <= 0){ this.kill(); }
        }
    }
    public static void upgradeEnemies(){
        baseSpeed = 1.2 * baseSpeed;
        baseHP = (int) (1.5 * baseHP);
        baseDmg = (int) (1.5 * baseDmg);
        baseScore *= 5;
    }
}