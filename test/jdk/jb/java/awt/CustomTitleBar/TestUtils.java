import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import java.awt.*;

public class TestUtils {

    static final float TITLE_BAR_HEIGHT = 100;
    static final Color TITLE_BAR_COLOR = Color.BLUE;

    static boolean checkTitleBarHeight(WindowDecorations.CustomTitlebar titleBar, float expected) {
        if (titleBar.getHeight() != expected) {
            System.out.printf(String.format("Wrong title bar height. Actual = %f, expected = %d\n", titleBar.getHeight(), expected));
            return false;
        }
        return true;
    }

    static boolean checkFrameInsets(Frame frame) {
        Insets insets = frame.getInsets();
        if (!(insets.top == 0 && insets.right == 0 && insets.left == 0 && insets.bottom == 0)) {
            System.out.println("Frame insets must be zero, but got " + insets);
            return false;
        }
        return true;
    }

    static Frame createFrameWithCustomTitleBar(WindowDecorations.CustomTitlebar titleBar) {
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
