package com.neondf.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Escudeira extends Suporte {
    private int maxShield = 0;
    private long lastRegen = 0;

    public Escudeira() {
        super("Escudeira", new Color(0, 100, 255));
        this.baseColor = new Color(50, 150, 255); // Azul Neon Protetor
    }

    @Override
    public void onUpgrade() {
        maxShield += 20;
    }

    @Override
    public void tick(Tower tower, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        super.tick(tower, enemies, bullets);
        if (level == 0) return;

        // Posiciona em Cima da Torre Principal
        setPosition(tower.getCenterX(), tower.getCenterY() - 60);

        tower.setMaxShield(maxShield);

        long now = System.currentTimeMillis();
        if (now - lastRegen > 2000) {
            tower.addShield(level * 2);
            lastRegen = now;
        }
    }

    // --- O VISUAL "TANQUE" ---
    @Override
    public void render(Graphics2D g) {
        if (level == 0) return;

        AffineTransform old = g.getTransform();
        g.translate(x, y);

        // 1. O CAMPO DE FORÇA (Domo de Energia)
        // Usa o pulseTimer da classe mãe para animar o tamanho e a transparência
        float pulse = (float) (Math.sin(pulseTimer) + 1) / 2; // 0.0 a 1.0
        int shieldSize = 50 + (int)(pulse * 6); // O escudo cresce e diminui
        int alpha = 50 + (int)(pulse * 50); // Transparência varia

        g.setColor(new Color(0, 200, 255, alpha)); // Azul Claro Transparente
        g.fillOval(-shieldSize/2, -shieldSize/2, shieldSize, shieldSize);

        // Borda do escudo (Mais forte)
        g.setColor(new Color(0, 200, 255, 200));
        g.setStroke(new BasicStroke(2));
        g.drawOval(-shieldSize/2, -shieldSize/2, shieldSize, shieldSize);

        // 2. A BASE "TANQUE" (Industrial)
        // Um quadrado pesado girando lentamente ao contrário do pulso
        g.rotate(-pulseTimer * 0.5);

        int boxSize = 24;
        g.setColor(new Color(20, 20, 40)); // Metal Escuro
        g.fillRect(-boxSize/2, -boxSize/2, boxSize, boxSize);

        // Borda de Aço Neon
        g.setColor(baseColor);
        g.setStroke(new BasicStroke(3));
        g.drawRect(-boxSize/2, -boxSize/2, boxSize, boxSize);

        // 3. EMISSORES DE ESCUDO (Nos 4 cantos)
        g.setColor(Color.WHITE);
        int emitSize = 6;
        // Desenha 4 pontinhos brancos nos cantos da base
        g.fillRect(-boxSize/2 - 2, -boxSize/2 - 2, emitSize, emitSize); // Canto sup esq
        g.fillRect(boxSize/2 - 4, -boxSize/2 - 2, emitSize, emitSize);  // Canto sup dir
        g.fillRect(-boxSize/2 - 2, boxSize/2 - 4, emitSize, emitSize);  // Canto inf esq
        g.fillRect(boxSize/2 - 4, boxSize/2 - 4, emitSize, emitSize);   // Canto inf dir

        // 4. SÍMBOLO DE CRUZ (+) NO MEIO
        // Para parecer um "Buff" ou "Defesa"
        g.setColor(baseColor);
        g.fillRect(-3, -8, 6, 16); // Vertical
        g.fillRect(-8, -3, 16, 6); // Horizontal

        g.setTransform(old);

        // Indicadores de nível
        renderLevelDots(g);
    }

    private void renderLevelDots(Graphics2D g) {
        int dotSize = 4; int spacing = 6;
        int startX = (int)x -((level * (dotSize + spacing)) / 2) + spacing/2;
        for (int i = 0; i < level; i++) {
            g.setColor(baseColor); g.fillOval(startX + (i * (dotSize + spacing)), (int)y-35, dotSize, dotSize); // Desenha um pouco mais acima por causa do escudo grande
            g.setColor(Color.WHITE); g.fillOval(startX + (i * (dotSize + spacing)) + 1, (int)y-34, dotSize-2, dotSize-2);
        }
    }
}