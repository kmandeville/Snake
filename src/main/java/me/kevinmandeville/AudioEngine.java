package me.kevinmandeville;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A class responsible for managing and playing audio resources in an application. AudioEngine allows loading, playing,
 * and releasing sound clips while managing resources efficiently.
 */
public class AudioEngine {

    private final Map<String, Clip> soundClips;

    public enum Sounds {
        SNAKE_EAT(), GAME_OVER(), IM_A_SNAKE(), GAMESTART_IMASNAKE();

        private String fileName;

        Sounds() {
        }
    }

    public AudioEngine() {
        soundClips = new HashMap<>();
    }

    /**
     * Loads a sound clip from the specified resource path and associates it with the given sound enum. The loaded sound
     * clip is stored for later playback.
     *
     * @param sound        The enum value representing the sound to be loaded.
     * @param resourcePath The resource path to the audio file to be loaded.
     * @throws IOException                   If an I/O error occurs while accessing the audio file.
     * @throws UnsupportedAudioFileException If the audio file is in an unsupported format.
     * @throws LineUnavailableException      If a line for playing the audio clip cannot be opened.
     */
    public void loadSound(Sounds sound, String resourcePath)
        throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        File soundFile = new File(getClass().getClassLoader().getResource(resourcePath).getFile());
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundClips.put(sound.name(), clip);
        }
    }

    /**
     * Plays the specified sound clip associated with the given enum value. If the sound clip is already playing, it
     * stops the clip before starting it again from the beginning.
     *
     * @param sound The enum value representing the sound to be played.
     */
    public Clip playSound(Sounds sound) {
        Clip clip = soundClips.get(sound.name());
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop(); // Stop the clip if currently playing
            }
            clip.setFramePosition(0); // Rewind to the start
            clip.start();
            return clip;
        } else {
            System.err.println("AudioEngine: Sound clip '" + sound.name() + "' not found.");
            return null;
        }
    }

    /**
     * Releases resources held by all loaded audio clips.
     */
    public void releaseResources() {
        for (Clip clip : soundClips.values()) {
            clip.close();
        }
    }
}
