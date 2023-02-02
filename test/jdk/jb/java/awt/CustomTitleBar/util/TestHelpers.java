package util;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
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
        final Path path = Paths.get(workingDir, fileName);

        System.out.println("path = " + path.toString());

        ImageIO.write(screenShot, "png", new File(fileName));

        return fileName;
    }

}
