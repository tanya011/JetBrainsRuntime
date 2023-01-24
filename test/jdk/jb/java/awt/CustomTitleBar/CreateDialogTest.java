import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * @test
 * @summary Regression test for JET-5194
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 */

public class CreateDialogTest extends CreateWindowBase {

    private static Dialog dialog;
    private static WindowDecorations.CustomTitlebar titleBar;

    private static boolean leftMouseButtonClickedToTitleBar = false;
    private static boolean rightMouseButtonClickedToTitleBar = false;

    public static void main(String... args) throws AWTException, InterruptedException, InvocationTargetException {
        final Robot robot = createRobot();

        try {
            SwingUtilities.invokeAndWait(() -> {
                dialog = createDialog();
                titleBar = createTitleBar(TITLE_BAR_HEIGHT, true);
                JBR.getWindowDecorations().setCustomTitlebar(dialog, titleBar);

                dialog.addMouseListener(new MouseAdapter() {
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
                dialog.add(createFirstPanel());
                dialog.add(createSecondPanel());

                dialog.setLayout(null);
                dialog.setVisible(true);

                if (dialog.getInsets().top != 0) {
                    throw new RuntimeException(String.format("Top inset of dialog should be 0, but actual value is %d", dialog.getInsets().top));
                }

                // click to title bar
                int x = (int) (dialog.getLocation().x + titleBar.getLeftInset() + (dialog.getWidth() - titleBar.getRightInset() - titleBar.getLeftInset()) / 2);
                int y = dialog.getLocation().y + TITLE_BAR_HEIGHT / 2;
                robot.delay(500);
                robot.mouseMove(x, y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);


                robot.delay(500);
                robot.mouseMove(x, y);
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);

                int panel1X = dialog.getLocation().x +  panel1.getLocation().x + PANEL_WIDTH / 2;
                int panel1Y = dialog.getLocation().y + panel1.getLocation().y + PANEL_HEIGHT / 2;
                robot.delay(500);
                robot.mouseMove(panel1X, panel1Y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                int panel2X = dialog.getLocation().x + panel2.getLocation().x + PANEL_WIDTH / 2;
                int panel2Y = dialog.getLocation().y + panel2.getLocation().y + PANEL_HEIGHT / 2;
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
                dialog.dispose();
            });
        }
    }


    private static Dialog createDialog() {
        Dialog d = new Dialog((Frame) null){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(TITLE_BAR_COLOR);
                g.fillRect(r.x, r.y, r.width, TITLE_BAR_HEIGHT);
            }
        };
        d.setBounds(200, 200, 800, 400);
        return d;
    }

}
