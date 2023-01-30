import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;
import util.TestUtils;

import javax.swing.SwingUtilities;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

/*
 * @test
 * @summary Regression test for JET-5124
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main/manual ExTest
 */
public class ExTest {

    public static void main(String... args) throws InterruptedException, InvocationTargetException {
        //SwingUtilities.invokeAndWait(() -> {
            Frame frame = new Frame() {
                @Override
                public void paint(Graphics g) {
                    Rectangle r = g.getClipBounds();
                    g.setColor(TestUtils.TITLE_BAR_COLOR);
                    g.fillRect(r.x, r.y, r.width, (int) TestUtils.TITLE_BAR_HEIGHT);
                    super.paint(g);
                }
            };
            //frame.setName("Frame");

            frame.setTitle("Frame");
            frame.setBounds(200, 400, 1000, 200);

            WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
            JBR.getWindowDecorations().setCustomTitleBar(frame, titleBar);


            Button button = new Button();
            button.setBackground(Color.RED);
            button.setSize(80, 40);
            //button.setLocation(300, 20);
            //button.setBounds(300, 20, 80, 40);
            MouseAdapter adapter = new MouseAdapter() {
                private void hit() {
                    //titleBar.forceHitTest(false);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("release " + e.getButton());
                    hit();
                    System.out.println("release " + e.getButton());
//                    if (e.getButton() == 1) {
//                        gotButton1Click = true;
//                    }
//                    if (e.getButton() == 2) {
//                        gotButton2Click = true;
//                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    hit();
                    System.out.println("pressed " + e.getButton());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    hit();
                    System.out.println("release " + e.getButton());
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hit();
                    System.out.println("entered");
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    hit();
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    hit();
                }
            };
            button.addMouseListener(adapter);
            button.addMouseMotionListener(adapter);

        Panel panel = new Panel(){

        };
        panel.setBounds(300, 10, 100, 50);
        panel.add(button);

            //frame.add(panel);
            //frame.setLayout(null);
            frame.setVisible(true);


            try {
                Robot robot = new Robot();
                robot.delay(30000);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        //});

    }

}
