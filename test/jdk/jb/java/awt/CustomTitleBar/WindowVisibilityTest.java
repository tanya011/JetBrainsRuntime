import com.jetbrains.JBR;
import util.CommonAPISuite;
import util.Task;
import util.TestUtils;

import java.awt.Robot;

/*
 * @test
 * @summary Verify custom title bar in case of changing visibility of a window
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main WindowVisibilityTest
 */
public class WindowVisibilityTest {

    public static void main(String... args) {
        boolean status = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), visibilityTest);

        if (!status) {
            throw new RuntimeException("WindowVisibilityTest FAILED");
        }
    }

    private static final Task visibilityTest = new Task("visibilityTest") {

        @Override
        public void prepareTitleBar() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        public void test() throws Exception {
            Robot robot = new Robot();

            final float titleBarHeight = titleBar.getHeight();

            window.setVisible(false);
            robot.delay(1000);

            window.setVisible(true);
            robot.delay(1000);

            if (titleBarHeight != titleBar.getHeight()) {
                passed = false;
                System.out.println("Error: title bar height has been changed");
            }
            if (!titleBar.getContainingWindow().equals(window)) {
                passed = false;
                System.out.println("Error: wrong containing window");
            }
        }

    };

}
