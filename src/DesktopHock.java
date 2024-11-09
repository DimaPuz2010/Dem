import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DesktopHock {
    private final JPanel panel;
    private final JFrame frame;
    private final JLabel label;
    private final Dimension screenSize;
    private final Robot robot;
    private BufferedImage screenshot;
    private final Audio audio;

    public DesktopHock() throws AWTException, LineUnavailableException {
        panel = new JPanel();
        frame = new JFrame();
        label = new JLabel();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        robot = new Robot();
        audio = new Audio();

        initializeFrame();
        initializeLabel();
    }

    public void desktopHock(int locateX, int locateY, boolean isStartAudio) {
        audio.setVolume(0.5f);
        if(isStartAudio) audio.start();
        while (true) {
            captureScreen();
            updateDisplay(locateX, locateY);
        }
    }

    private void initializeFrame() {
        panel.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(screenSize);
        frame.setVisible(true);
    }

    private void captureScreen() {
        screenshot = robot.createScreenCapture(new Rectangle(screenSize));
    }

    private void initializeLabel() {
        label.setBounds(0, 0, frame.getWidth() / 2, frame.getHeight() / 2);
        panel.add(label);
    }

    private void updateDisplay(int x, int y) {
        label.setLocation(x, y);
        ImageIcon icon = new ImageIcon(screenshot.getScaledInstance(frame.getWidth() / 2, frame.getHeight() / 2, Image.SCALE_SMOOTH));
        label.setIcon(icon);
        panel.repaint();
    }
}
