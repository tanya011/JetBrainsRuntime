import com.jetbrains.JBR;
import util.CommonAPISuite;
import util.Task;
import util.TestHelpers;
import util.TestUtils;

import java.awt.Dimension;
import java.awt.Toolkit;

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
            final float initialTitleBarHeight = titleBar.getHeight();

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int newHeight = screenSize.height / 2;
            final int newWidth = screenSize.width / 2;

            window.setSize(newWidth, newHeight);

            if (titleBar.getHeight() != initialTitleBarHeight) {
                passed = false;
                System.out.println("Error: title bar height has been changed");
            }
        }
    };

}
