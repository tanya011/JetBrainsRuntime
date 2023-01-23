import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;

public class CreateDialog extends CreateWindowBase {

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
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

                robot.delay(500);
                robot.mouseMove(x, y);
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);


                //TODO what to do in case buttons switched vice versa
                if (!leftMouseButtonClickedToTitleBar) {
                    System.out.println("Title bar didn't receive BUTTON1 click");
                }
                if (!rightMouseButtonClickedToTitleBar) {
                    System.out.println("Title bar didn't receive BUTTON2 click");
                }
            });
        } finally {
            SwingUtilities.invokeLater(() -> {
                //dialog.dispose();
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
