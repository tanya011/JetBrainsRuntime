import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

/**
 * @test
 * @summary Regression test for JET-5124
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=false
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.0
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.25
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.5
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.0
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.5
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=3.0
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=3.5
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=4.0
 */
public class CommonAPITest {

    public static void main(String... args) {
        Runner defaultTitleBar = new Runner("Create title bar with default settings") {
            @Override
            public void test() {
                WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
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
                WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
                titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
                titleBar.putProperty("controls.visible", "false");
                frame = TestUtils.createFrameWithCustomTitleBar(titleBar);

                passed = passed && TestUtils.checkTitleBarHeight(titleBar, TestUtils.TITLE_BAR_HEIGHT);
                passed = passed && TestUtils.checkFrameInsets(frame);

                if (titleBar.getLeftInset() != 0 || titleBar.getRightInset() != 0) {
                    passed = false;
                    System.out.println("System controls are hidden so insets must be zero");
                }

                if (!"false".equals(titleBar.getProperties().get("controls.visible").toString())) {
                    passed = false;
                    System.out.println("controls.visible isn't false");
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

                WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
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