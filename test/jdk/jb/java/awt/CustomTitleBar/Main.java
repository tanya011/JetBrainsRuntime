import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {

    public static void main(String... args) {
        Frame frame = new Frame();
        frame.setBounds(100, 100, 1000, 600);

        Panel panel1 = new Panel(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(Color.BLUE);
                g.fillRect(r.x, r.y, 200, 100);
                super.paint(g);
            }
        };
        panel1.setBounds(0, 0, 200, 100);
        panel1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouse clicked to panel1");
            }
        });

        Panel panel2 = new Panel(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(Color.RED);
                g.fillRect(r.x, r.y, 200, 100);
                super.paint(g);
            }
        };
        panel2.setBounds(200, 0, 200, 100);
        panel2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouse clicked to panel2");
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
