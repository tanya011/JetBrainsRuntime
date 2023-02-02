import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class CheckFullScreen {

    public static void main(String... args) {
        Frame f = new Frame(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(Color.BLUE);
                g.fillRect(r.x, r.y, r.width, 100);
                super.paint(g);
            }
        };

        WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
        titleBar.setHeight(100);
        JBR.getWindowDecorations().setCustomTitleBar(f, titleBar);

        f.setVisible(true);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(f);
    }

}
