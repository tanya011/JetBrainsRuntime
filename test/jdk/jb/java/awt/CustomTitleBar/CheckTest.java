import util.ScreenShotHelpers;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
 * @test
 * @summary Verify custom title bar in case of window resizing
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main CheckTest
 */
public class CheckTest {

    public static void main(String... args) throws AWTException, IOException {
        Frame f = new Frame(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(Color.BLUE);
                g.fillRect(r.x, r.y, r.width, 100);
                super.paint(g);
            }
        };
        f.setBounds(100, 100, 600, 400);
        f.setVisible(true);

        BufferedImage image = ScreenShotHelpers.takeScreenshot(f);
        ScreenShotHelpers.storeScreenshot("check", image);
    }

}
