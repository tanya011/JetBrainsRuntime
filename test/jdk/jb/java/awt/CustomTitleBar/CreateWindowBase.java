import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreateWindowBase {

    protected static final int TITLE_BAR_HEIGHT = 40;
    protected static final Color TITLE_BAR_COLOR = Color.BLUE;


    protected static boolean mouseClickedToPanel1 = false;
    protected static boolean mouseClickedToPanel2 = false;

    protected static final int PANEL_WIDTH = 50;
    protected static final int PANEL_HEIGHT = 30;

    protected static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    protected static void scheduleTask(Runnable r, int delayInSeconds) {
        EXECUTOR.schedule(() -> SwingUtilities.invokeLater(r), delayInSeconds, TimeUnit.SECONDS);
    }

    protected static Panel createFirstPanel() {
        Panel panel = new Panel();
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.setBackground(Color.RED);
        panel.setLocation(0, TITLE_BAR_HEIGHT);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseClickedToPanel1 = true;
            }
        });
        return panel;
    }

    protected static Panel createSecondPanel() {
        Panel panel = new Panel();
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.setBackground(Color.YELLOW);
        panel.setLocation(0, PANEL_HEIGHT + TITLE_BAR_HEIGHT);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseClickedToPanel2 = true;
            }
        });
        return panel;
    }

    protected static Robot createRobot() throws AWTException {
        final Robot robot = new Robot();
        robot.setAutoWaitForIdle(false);
        robot.setAutoDelay(0);
        return robot;
    }

    protected static WindowDecorations.CustomTitlebar createTitleBar(int height, boolean showControls) {
        WindowDecorations.CustomTitlebar titleBar = JBR.getWindowDecorations().createCustomTitlebar();
        titleBar.setHeight(height);
        titleBar.putProperty("controls.visible", showControls);

        return titleBar;
    }

}
