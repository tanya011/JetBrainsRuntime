import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import java.awt.*;

/**
 * @test
 * @summary Regression test for JET-5194
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main/manual CommonAPITest
 */
public class CommonAPITest {



    private static Frame frame;

    public static void main(String... args) {
        Runner defaultTitleBar = new Runner("Create title bar with default settings") {
            @Override
            public void test() {
                WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitlebar();
                titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
                frame = TestUtils.createFrameWithCustomTitleBar(titleBar);

                passed = passed && TestUtils.checkTitleBarHeight(titleBar, TestUtils.TITLE_BAR_HEIGHT);
                passed = passed && TestUtils.checkFrameInsets(frame);

                if (titleBar.getLeftInset() == 0 && titleBar.getRightInset() == 0) {
                    passed = false;
                    System.out.println("Left or right space must be occupied by system controls");
                }
            }
        };

        if (!(defaultTitleBar.run())) {
            throw new RuntimeException("TEST FAILED");
        }

        Runner hiddenSystemControls = new Runner("Hide system controls") {
            @Override
            void test() {
                WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitlebar();
                titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
                titleBar.putProperty("controls.visible", "false");
                frame = TestUtils.createFrameWithCustomTitleBar(titleBar);

                passed = passed && TestUtils.checkTitleBarHeight(titleBar, TestUtils.TITLE_BAR_HEIGHT);
                passed = passed && TestUtils.checkFrameInsets(frame);

                if (titleBar.getLeftInset() != 0 || titleBar.getRightInset() != 0) {
                    passed = false;
                    System.out.println("System controls are hidden so insets must be zero");
                }
            }
        };

        if (!hiddenSystemControls.run()) {
            throw new RuntimeException("TEST FAILED");
        }

        Runner changeTitleBarHeight = new Runner("Changing of title bar height") {
            @Override
            void test() {
                float initialHeight = 50;
                float newHeight = 100;

                WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitlebar();
                titleBar.setHeight(initialHeight);
                frame = TestUtils.createFrameWithCustomTitleBar(titleBar);

                passed = passed && TestUtils.checkTitleBarHeight(titleBar, initialHeight);

                titleBar.setHeight(newHeight);

                passed = passed && TestUtils.checkTitleBarHeight(titleBar, newHeight);
            }
        };
        if (!changeTitleBarHeight.run()) {
            throw new RuntimeException("TEST FAILED");
        }
    }



}