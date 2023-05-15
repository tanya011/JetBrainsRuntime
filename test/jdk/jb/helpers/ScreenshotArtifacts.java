import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotArtifacts {

    private ScreenshotArtifacts() {}

    public static void verify(String name) {
        takeScreenshot(name, 2);
        compareScreenshots(name);
    }

    public static void takeScreenshot(String name) {
        takeScreenshot(name, 1);
    }

    public static void takeScreenshot(String name, int count) {
        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            File file = getArtifactsPath().resolve(getFileName(name, count)).toFile();
            ImageIO.write(image, "png", file);
        } catch (IOException | AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void compareScreenshots(String name) {
        try {
            final Path s1 = getArtifactsPath().resolve(getFileName(name, 1));
            final Path s2 = getArtifactsPath().resolve(getFileName(name, 2));

            if (!Files.exists(s1)) {
                System.out.println("Screenshot 1 doesn't exists");
                throw new RuntimeException("Screenshot 1 doesn't exists");
            }

            if (!Files.exists(s2)) {
                System.out.println("Screenshot 2 doesn't exists");
                throw new RuntimeException("Screenshot 2 doesn't exists");
            }

            BufferedImage image1 = ImageIO.read(s1.toFile());
            BufferedImage image2 = ImageIO.read(s2.toFile());

            if (image1.getHeight() != image2.getHeight() || image1.getWidth() != image2.getWidth()) {
                System.out.println("Screen resolution was changed");
                throw new RuntimeException("Screen resolution was changed");
            }

            int count = 0;
            for (int x = 0; x < image1.getWidth(); x++) {
                for (int y = 0; y < image1.getHeight(); y++) {
                    int point1 = image1.getRGB(x, y);
                    int point2 = image2.getRGB(x, y);
                    if (point1 != point2) {
                        count++;
                    }
                }
            }

            int dots = image1.getWidth() * image1.getHeight();
            double diff = (double) count / (double) dots;
            System.out.println("[DIFF] value=" + diff);
            if (diff > 0.1) {
                System.out.println("DIFFERENCE MORE THAN 10%");
                throw new RuntimeException("DIFFERENCE MORE THAN 10%");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getArtifactsPath() throws IOException {
        final String userDir = System.getProperty("user.dir");
        final Path scratchPath = Path.of(userDir);
        final Path artifactsPath = scratchPath.getParent().resolve("artifacts");
        if (Files.notExists(artifactsPath)) {
            Files.createDirectory(artifactsPath);
        }
        return artifactsPath;
    }

    private static String getFileName(String name, int count) {
        return name.replaceAll("/", "-") + "-" + count + ".png";
    }

}
