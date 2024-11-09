import javax.sound.sampled.LineUnavailableException;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            DesktopHock desktopHock = new DesktopHock();
            desktopHock.desktopHock(100, 100, true);
        } catch (AWTException | LineUnavailableException e) {
            System.err.println("Ошибка при создании DesktopHock: " + e.getMessage());
        }
    }
}