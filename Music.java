import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import java.net.URL;
import javax.sound.sampled.*;

import java.io.*;

public class Music {
    private static Clip clip;
    private static boolean playing;
   
    public static void playMusic(String filename){
        try {

            URL url = Music.class.getClassLoader().getResource(filename);

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

            clip = AudioSystem.getClip();

            clip.open(audioIn);
            clip.loop(clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        playing = true;
    }

    public static void stopMusic() {
        if (playing) {
            clip.stop();
        }
    }

    public static void playFX(String filename){
        try {

            URL url = Music.class.getClassLoader().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

            Clip c;
            c = AudioSystem.getClip();
            c.open(audioIn);
            c.loop(0);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    /*
    public static void playFX(String filename){
        URL urlClick = Music.class.getResource(filename);
        AudioClip click = Applet.newAudioClip(urlClick);
        click.play();

    }
    */
}