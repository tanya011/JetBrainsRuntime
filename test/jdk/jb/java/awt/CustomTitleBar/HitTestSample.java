import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HitTestSample {

    private static final int TITLE_BAR_HEIGHT = 80;

    private static WindowDecorations.CustomTitleBar titleBar;

    public static void main(String... args) {
        JFrame jFrame = new JFrame();
        JPanel jPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Rectangle r = g.getClipBounds();
                g.setColor(Color.CYAN);
                g.fillRect(r.x, r.y, r.x, TITLE_BAR_HEIGHT);
            }
        };
        jPanel.setBounds(0, 0, 1200, TITLE_BAR_HEIGHT);
        jFrame.setContentPane(jPanel);
        jFrame.setBounds(100, 100, 1200, 800);

        titleBar = JBR.getWindowDecorations().createCustomTitleBar();
        titleBar.setHeight(TITLE_BAR_HEIGHT);
        JBR.getWindowDecorations().setCustomTitleBar(jFrame, titleBar);

        jPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (titleBar == null) {
                    System.out.println("TITLE BAR IS NULL");
                }
                System.out.println("MOUSE PRESSED");
                titleBar.forceHitTest(true);
                System.out.println("hit test forced");
            }
        });

        jFrame.setVisible(true);
    }

}
