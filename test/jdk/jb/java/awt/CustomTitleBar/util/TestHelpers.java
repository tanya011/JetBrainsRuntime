package util;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
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

    public static BufferedImage takeScreenshot(Window window) throws AWTException {
        Robot robot = new Robot();

        final BufferedImage screenShot = robot.createScreenCapture(
                new Rectangle(window.getLocationOnScreen().x, window.getLocationOnScreen().y,
                        window.getWidth(), window.getHeight()));
        return screenShot;
    }

    public static void storeScreenshot(String namePrefix, BufferedImage image) throws IOException {
        final String fileName = String.format("%s-%s.png", namePrefix, UUID.randomUUID());
        ImageIO.write(image, "png", new File(fileName));
    }

    public static RectCoordinates findRectangleTitleBar(BufferedImage image, int titleBarHeight) {
        int centerX = image.getWidth() / 2;
        int centerY = titleBarHeight / 2;

        final int color = image.getRGB(centerX, centerY);

        int startY = centerY;
        for (int y = centerY; y >= 0; y--) {
            if (image.getRGB(centerX, y) != color) {
                startY = y + 1;
                break;
            }
        }

        int endY = centerY;
        for (int y = centerY; y <= (int) TestUtils.TITLE_BAR_HEIGHT; y++) {
            if (image.getRGB(centerX, y) != color) {
                endY = y - 1;
                break;
            }
        }

        int startX = centerX;
        for (int x = centerX; x >= 0; x--) {
            if (image.getRGB(x, startY) != color) {
                startX = x + 1;
                break;
            }
        }

        int endX = centerX;
        for (int x = centerX; x < image.getWidth(); x++) {
            if (image.getRGB(x, startY) != color) {
                endX = x - 1;
                break;
            }
        }

        return new RectCoordinates(startX, startY, endX, endY);
    }

}
