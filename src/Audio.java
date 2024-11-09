import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Audio {
    private final TargetDataLine targetLine;
    private final SourceDataLine sourceLine;
    private final FloatControl volumeControl;
    private final byte[] buffer;
    private boolean isRunning;
    private final Clip playbackClip;

    public Audio() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

        if (!AudioSystem.isLineSupported(targetInfo) || !AudioSystem.isLineSupported(sourceInfo)) {
            throw new LineUnavailableException("Линия с указанным форматом не поддерживается");
        }

        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);

        targetLine.open(format);
        sourceLine.open(format);

        volumeControl = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
        buffer = new byte[4096];

        playbackClip = AudioSystem.getClip();
    }

    public void start() {
        isRunning = true;
        targetLine.start();
        sourceLine.start();

        Thread captureThread = new Thread(() -> {
            ByteArrayOutputStream capturedAudio = new ByteArrayOutputStream();
            while (isRunning) {
                int bytesRead = targetLine.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    sourceLine.write(buffer, 0, bytesRead);
                    capturedAudio.write(buffer, 0, bytesRead);
                }
            }
            playbackCapturedAudio(capturedAudio.toByteArray());
        });
        captureThread.start();
    }

    public void stop() {
        isRunning = false;
        targetLine.stop();
        sourceLine.stop();
        targetLine.close();
        sourceLine.close();
        if (playbackClip.isOpen()) {
            playbackClip.close();
        }
    }

    public void setVolume(float volume) {
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        float value = min + (max - min) * volume;
        volumeControl.setValue(value);
    }

    private void playbackCapturedAudio(byte[] audioData) {
        try {
            AudioFormat format = targetLine.getFormat();
            AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(audioData),
                format,
                audioData.length / format.getFrameSize()
            );
            playbackClip.open(ais);
            playbackClip.start();
        } catch (LineUnavailableException | IOException e) {
            System.err.println("Ошибка при воспроизведении захваченного звука: " + e.getMessage());
        }
    }
}
