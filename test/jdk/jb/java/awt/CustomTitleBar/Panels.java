import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Panels {

    public static void main(String... args) {
        WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
        titleBar.setHeight(100);

        Frame frame = new Frame();
        JBR.getWindowDecorations().setCustomTitleBar(frame, titleBar);
        frame.setBounds(0, 0, 1000, 600);

        Panel panel1 = new Panel() {
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(Color.BLUE);
                g.fillRect(r.x, r.y, 200, 100);
                super.paint(g);
            }
        };
        panel1.setBounds(0, 0, 200, 100);

        MouseAdapter adapter1 = new MouseAdapter() {

            private void hit() {
                titleBar.forceHitTest(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                hit();
                System.out.println("mouse clicked to panel1");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("mouse pressed");
                hit();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                hit();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("entered");
                hit();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("drag event");
                hit();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("moved");
                hit();
            }
        };
        panel1.addMouseListener(adapter1);

        Panel panel2 = new Panel() {
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(Color.RED);
                g.fillRect(r.x, r.y, 200, 100);
                super.paint(g);
            }
        };
        panel2.setBounds(200, 200, 200, 100);

        panel2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("moved2");
            }
        });

        frame.add(panel1);
        frame.add(panel2);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouse clicked to frame");
            }
        });

        frame.setLayout(null);
        frame.setVisible(true);
    }

}
