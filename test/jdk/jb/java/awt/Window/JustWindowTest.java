import java.awt.*;

/*
 * @test
 * @run main/othervm JustWindowTest
 */
public class JustWindowTest {

    public static void main(String... args) throws AWTException, InterruptedException {
        Frame frame = new Frame();
        frame.setBounds(100, 100, 800, 600);
        frame.setVisible(true);

        Robot robot = new Robot();
        robot.wait(1000);

        frame.dispose();

        System.out.println("TEST PASSED");
    }

}
