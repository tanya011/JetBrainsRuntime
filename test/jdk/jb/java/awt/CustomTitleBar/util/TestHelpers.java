package util;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class TestHelpers {

    public static String takeScreenshot(Window window) throws AWTException, IOException {
        Robot robot = new Robot();

        final BufferedImage screenShot = robot.createScreenCapture(
                new Rectangle(
                        window.getLocation().x, window.getLocation().y, window.getWidth(), window.getHeight()));


        final String workingDir = System.getenv("PWD");
        final String fileName = String.format("AWT-Robot-screenshot-%s.bmp", UUID.randomUUID());

        ImageIO.write(screenShot, "BMP", Paths.get(workingDir, fileName).toFile());

        return fileName;
    }

}
