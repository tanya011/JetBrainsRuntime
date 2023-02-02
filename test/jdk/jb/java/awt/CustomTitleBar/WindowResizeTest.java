import com.jetbrains.JBR;
import util.CommonAPISuite;
import util.Task;
import util.TestHelpers;
import util.TestUtils;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/*
 * @test
 * @summary Verify custom title bar in case of window resizing
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main WindowResizeTest
 */
public class WindowResizeTest {

    public static void main(String... args) {
        boolean status = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), windowResizeTest);

        if (!status) {
            throw new RuntimeException("WindowResizeTest FAILED");
        }
    }

    private static final Task windowResizeTest = new Task("Window resize test") {
        @Override
        public void prepareTitleBar() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        public void test() throws Exception {
            Robot robot = new Robot();
            robot.delay(1000);
            final float initialTitleBarHeight = titleBar.getHeight();

            BufferedImage image = TestHelpers.takeScreenshot(window);
            TestHelpers.storeScreenshot("window-resize-test-1-", image);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int newHeight = screenSize.height / 2;
            final int newWidth = screenSize.width / 2;

            window.setSize(newWidth, newHeight);
            robot.delay(1000);

            if (titleBar.getHeight() != initialTitleBarHeight) {
                passed = false;
                System.out.println("Error: title bar height has been changed");
            }

            image = TestHelpers.takeScreenshot(window);
            passed = verifyScreenshot(image);

            if (!passed) {
                TestHelpers.storeScreenshot("window-resize-test-" + window.getName(), image);
            }
        }
    };

    private static boolean verifyScreenshot(BufferedImage image) {
        int centerX = image.getWidth() / 2;
        int centerY = (int) (TestUtils.TITLE_BAR_HEIGHT / 2);

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

        System.out.println("Planned title bar rectangle coordinates: (" + startX + ", " + startY + "), (" + endX + ", " + endY + ")");
        System.out.println("w = " + image.getWidth() + " h = " + image.getHeight());
        if (startX != image.getWidth() - endX - 1) {
            System.out.println("Left and right non-rectangle areas widths are non equal");
            return false;
        }

        return true;
    }

}
