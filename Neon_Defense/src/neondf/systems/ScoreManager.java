package com.neondf.systems;

import java.io.*;

public class ScoreManager {

    private static final String FILE_PATH = "highscore.txt";

    // Carrega o recorde do arquivo
    public static int loadHighScore() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return 0; // Se não tem arquivo, o recorde é 0
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();

            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar recorde.");
        }
        return 0;
    }

    // Salva o novo recorde
    public static void saveHighScore(int score) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            writer.write(String.valueOf(score));
            writer.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar recorde.");
        }
    }
}
