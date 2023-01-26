import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @test
 * @summary Regression test for JET-5194
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main/manual CustomAPITest
 */
public class CustomAPITest {

    private static final float TITLE_BAR_HEIGHT = 100;
    private static final Color TITLE_BAR_COLOR = Color.BLUE;

    private static Frame frame;

    public static void main(String... args) {
        Runner defaultTitleBar = new Runner("Create title bar with default settings") {
            @Override
            public void test() {
                WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitlebar();
                titleBar.setHeight(TITLE_BAR_HEIGHT);
                frame = createFrameWithCustomTitleBar(titleBar);

                passed = passed && checkTitleBarHeight(titleBar, TITLE_BAR_HEIGHT);
                passed = passed && checkFrameInsets(frame);

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
                titleBar.setHeight(TITLE_BAR_HEIGHT);
                titleBar.putProperty("controls.visible", "false");
                frame = createFrameWithCustomTitleBar(titleBar);

                passed = passed && checkTitleBarHeight(titleBar, TITLE_BAR_HEIGHT);
                passed = passed && checkFrameInsets(frame);

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
                frame = createFrameWithCustomTitleBar(titleBar);

                passed = passed && checkTitleBarHeight(titleBar, initialHeight);

                titleBar.setHeight(newHeight);

                passed = passed && checkTitleBarHeight(titleBar, newHeight);
            }
        };
        if (!changeTitleBarHeight.run()) {
            throw new RuntimeException("TEST FAILED");
        }
    }

    private static boolean checkTitleBarHeight(WindowDecorations.CustomTitlebar titleBar, float expected) {
        if (titleBar.getHeight() != expected) {
            System.out.printf(String.format("Wrong title bar height. Actual = %f, expected = %d\n", titleBar.getHeight(), expected));
            return false;
        }
        return true;
    }

    private static boolean checkFrameInsets(Frame frame) {
        Insets insets = frame.getInsets();
        if (!(insets.top == 0 && insets.right == 0 && insets.left == 0 && insets.bottom == 0)) {
            System.out.println("Frame insets must be zero, but got " + insets);
            return false;
        }
        return true;
    }

    private static Frame createFrameWithCustomTitleBar(WindowDecorations.CustomTitlebar titleBar) {
        Frame frame = new Frame(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(TITLE_BAR_COLOR);
                g.fillRect(r.x, r.y, r.width, (int) TITLE_BAR_HEIGHT);
            }
        };
        frame.setTitle("Frame");
        frame.setBounds(200, 400, 1000, 200);

        JBR.getWindowDecorations().setCustomTitlebar(frame, titleBar);

        frame.setLayout(null);
        frame.setVisible(true);

        return frame;
    }

}

abstract class Runner {

    protected Frame frame;
    private final String name;
    protected boolean passed = true;

    Runner(String name) {
        this.name = name;
    }

    final boolean run(){
        System.out.printf("RUN TEST CASE: %s\n%n", name);
        try {
            SwingUtilities.invokeAndWait(this::test);
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            SwingUtilities.invokeLater(() -> frame.dispose());
        }
        if (passed) {
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
        }
        return passed;
    }

    abstract void test();

}