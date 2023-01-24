import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @test
 * @summary Regression test for JET-5194
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 */
public class CreateFrameTest extends CreateWindowBase {

    private static Frame frame;

    private static WindowDecorations.CustomTitlebar titleBar;

    private static boolean leftMouseButtonClickedToTitleBar = false;
    private static boolean rightMouseButtonClickedToTitleBar = false;


    public static void main(String... args) throws AWTException, InterruptedException, InvocationTargetException {
        final Robot robot = createRobot();

        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = createFrame();
                WindowDecorations.CustomTitlebar titleBar = createTitleBar(TITLE_BAR_HEIGHT, true);
                JBR.getWindowDecorations().setCustomTitlebar(frame, titleBar);

                frame.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == 1) {
                            leftMouseButtonClickedToTitleBar = true;
                        }
                        if (e.getButton() == 2) {
                            rightMouseButtonClickedToTitleBar = true;
                        }
                    }
                });

                Panel panel1 = createFirstPanel();
                Panel panel2 = createSecondPanel();
                frame.add(createFirstPanel());
                frame.add(createSecondPanel());

                frame.setLayout(null);
                frame.setVisible(true);

                if (frame.getInsets().top != 0) {
                    throw new RuntimeException(String.format("Top inset of frame should be 0, but actual value is %d", frame.getInsets().top));
                }

                // click to title bar
                int x = (int) (frame.getLocation().x + titleBar.getLeftInset() + (frame.getWidth() - titleBar.getRightInset() - titleBar.getLeftInset()) / 2);
                int y = frame.getLocation().y + TITLE_BAR_HEIGHT / 2;
                robot.delay(500);
                robot.mouseMove(x, y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);


                robot.delay(500);
                robot.mouseMove(x, y);
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);

                int panel1X = frame.getLocation().x +  panel1.getLocation().x + PANEL_WIDTH / 2;
                int panel1Y = frame.getLocation().y + panel1.getLocation().y + PANEL_HEIGHT / 2;
                robot.delay(500);
                robot.mouseMove(panel1X, panel1Y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                int panel2X = frame.getLocation().x + panel2.getLocation().x + PANEL_WIDTH / 2;
                int panel2Y = frame.getLocation().y + panel2.getLocation().y + PANEL_HEIGHT / 2;
                robot.delay(500);
                robot.mouseMove(panel2X, panel2Y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            });
        } finally {
            SwingUtilities.invokeLater(() -> {
                if (!leftMouseButtonClickedToTitleBar) {
                    System.out.println("Title bar didn't receive BUTTON1 click");
                }
                if (!rightMouseButtonClickedToTitleBar) {
                    System.out.println("Title bar didn't receive BUTTON2 click");
                }

                if (!mouseClickedToPanel1) {
                    System.out.println("Panel1 didn't receive click");
                }

                if (!mouseClickedToPanel2) {
                    System.out.println("Panel2 didn't receive click");
                }
                frame.dispose();
            });
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
