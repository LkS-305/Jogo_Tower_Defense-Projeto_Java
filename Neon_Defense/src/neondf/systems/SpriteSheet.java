package com.neondf.systems;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SpriteSheet {

    private BufferedImage sprite;

    public SpriteSheet(String path) {
        try {
            // Tenta localizar o arquivo
            URL url = getClass().getResource(path);

            if (url == null) {
                // Se não achar, avisa no console mas NÃO TRAVA o jogo
                System.err.println("AVISO: Imagem não encontrada (usando padrão): " + path);
                sprite = null;
            } else {
                // Se achar, carrega
                sprite = ImageIO.read(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sprite = null;
        }
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    // Método para recortar pedaços da imagem (para animações)
    public BufferedImage getSprite(int x, int y, int width, int height) {
        if (sprite == null) return null; // Proteção contra imagem não carregada
        return sprite.getSubimage(x, y, width, height);
    }
}