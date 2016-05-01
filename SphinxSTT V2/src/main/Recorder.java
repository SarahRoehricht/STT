package main;
import javax.sound.sampled.*;
import java.io.*;
 

public class Recorder {
    
    // path of the wav file
    File wavFile = new File("RecordAudio.wav");
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
 
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
    	float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        int frameSize = 2;
        float frameRate = 16000;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,sampleRate, sampleSizeInBits,
                                             channels, frameSize, frameRate, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     * @throws InterruptedException 
     */
    void start() throws InterruptedException {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            //System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
 
    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
}