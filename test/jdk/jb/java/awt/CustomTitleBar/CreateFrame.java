import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

public class CreateFrame extends CreateWindowBase {

    private static Frame frame;

    public CreateFrame() throws AWTException {
    }

    public static void main(String... args) throws AWTException, InterruptedException, InvocationTargetException {
        final Robot robot = createRobot();

        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = createFrame();
                if (JBR.isAvailable()) {
                    WindowDecorations.CustomTitlebar titleBar = createTitleBar(TITLE_BAR_HEIGHT, true);
                    JBR.getWindowDecorations().setCustomTitlebar(frame, titleBar);
                }

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                    }
                });

                frame.setLayout(null);
                frame.setVisible(true);
            });
        } finally {

        }
    }

    private static Frame createFrame() {
        Frame f = new Frame(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(TITLE_BAR_COLOR);
                g.fillRect(r.x, r.y, r.width, TITLE_BAR_HEIGHT);
            }
        };
        f.setBounds(200, 400, 1000, 200);
        f.setTitle("Frame");

        return f;
    }

}
