package com.neondf.systems;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AudioPlayer {
    public enum TipoAudio{
        MUSICA,
        EFEITO,
        TESTE
    }
    private Clip clip;
    private FloatControl volumeControl;
    private static float volumeMusica, volumeEfeito;
    private final TipoAudio tipo;
    public AudioPlayer(String filePath,  TipoAudio tipo){
        this.tipo = tipo;
        volumeMusica = 0.5f;
        volumeEfeito = 0.5f;
        try {
            // 1. Abrir o fluxo de áudio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(getClass().getResource(filePath)));

            // 2. Obter o recurso de clip e abrir o fluxo
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // 3. Obter o controle de volume (MASTER_GAIN)
            // Isso nos permite alterar o volume a qualquer momento
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip == null) return;

        // Reinicia o áudio do começo e toca
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

//CONVERTE DA ESCALA DECIMAL, PARA DECIBEIS
    public static void incVolume(TipoAudio tipo){
        if(tipo == TipoAudio.MUSICA){
            if(volumeMusica <= 0.9f){
                volumeMusica += 0.1f;
            } else{
                volumeMusica = 1.0f;
            }
        } else{
            if(volumeEfeito <= 0.9f){
                volumeEfeito += 0.1f;
            } else{
                volumeEfeito = 1.0f;
            }
        }
    }

    public static void decVolume(TipoAudio tipo) {
        if (tipo == TipoAudio.MUSICA) {
            if(volumeMusica >= 0.1f){
                volumeMusica -= 0.1f;
            } else{
                volumeMusica = 0.0f;
            }
        } else {
            if(volumeEfeito >= 0.1f){
                volumeEfeito -= 0.1f;
            } else{
                volumeEfeito = 0.0f;
            }
        }
    }

    public void updateVolume(TipoAudio tipo){
        if(volumeControl == null) return;
        float volume;
        if(tipo == TipoAudio.MUSICA){
            volume = volumeMusica;
        } else{
            volume = volumeEfeito;
        }
        if(volume <= 0f){
            volumeControl.setValue(-80.0f);
        } else{
            volumeControl.setValue(20f * (float) Math.log10(volume));
        }
    }

    public static void updateAllVolumes(ArrayList<AudioPlayer> audios){
        for(AudioPlayer audio : audios){
            audio.updateVolume(audio.tipo);
        }
    }

    public static float getVolume(TipoAudio tipo){
        if(tipo == TipoAudio.MUSICA){
            return volumeMusica;
        } else{
            return volumeEfeito;
        }
    }

    public void testVolume(TipoAudio tipo){
        float volumeTeste = getVolume(tipo);
        if(volumeTeste <= 0f){
            volumeControl.setValue(-80.0f);
        } else{
            volumeControl.setValue(20f * (float) Math.log10(volumeTeste));
        }
        this.play();
    }
}