import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @test
 * @run main ProgrammaticFocusTest
 */
public class ProgrammaticFocusTest {

    private static Frame sharedFrame;
    private static Robot robot;
    private static final int counterTimeout = 1000;
    private static final Map<Component, ChildComponentFocusListener> componentsToListeners = new HashMap<>();

    public static void main(String... args) throws AWTException, InterruptedException, InvocationTargetException {
        robot = new Robot();
        try {
            SwingUtilities.invokeAndWait(() -> {
                sharedFrame = new Frame();
                sharedFrame.setLayout(new FlowLayout());
                sharedFrame.setSize(600, 600);
                sharedFrame.setLocation(100, 100);
                sharedFrame.setVisible(true);
            });

            Component button1 = new Button();
            Component choice1 = new Choice();

            packComponents(button1, choice1);
            startFocusTracking(choice1);
            button1.setFocusable(false);
            System.out.println("The component button1 set to non-focusable");

            if (!verifyFocusGained(choice1)) {
                throw new RuntimeException("TEST FAILED: The component choice1 did not get the focus");
            }

            if (!choice1.isFocusOwner() || button1.isFocusOwner()) {
                throw new RuntimeException("TEST FAILED: The expected component choice1 gained the focus, but the previous component is still the focus owner");
            }

            Component button2 = new Button();
            Component choice2 = new Choice();

            packComponents(button2, choice2);
            startFocusTracking(choice2);
            button2.setVisible(false);
            System.out.println("The component button2 set to invisible");

            if (!verifyFocusGained(choice2)) {
                throw new RuntimeException("TEST FAILED: The component choice2 did not get the focus");
            }

            if (!choice2.isFocusOwner() || button2.isFocusOwner()) {
                throw new RuntimeException("TEST FAILED: The component choice2 gained the focus, but the previous component is still the focus owner");
            }
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                sharedFrame.dispose();
            });
        }
        System.out.println("TEST PASSED");
    }

    private static void packComponents(Component comp1, Component comp2) {
        componentsToListeners.clear();

        moveFocusTo(sharedFrame);
        robot.waitForIdle();

        comp1.setFocusable(true);
        comp2.setFocusable(true);
        robot.waitForIdle();
        sharedFrame.add(comp1);
        sharedFrame.add(comp2);
        sharedFrame.pack();
        robot.waitForIdle();

        startFocusTracking(comp1);
        comp1.requestFocusInWindow();
        if (!verifyFocusGained(comp1)) {
            throw new RuntimeException("TEST FAILED: The first component (comp1) did not get the focus");
        }
    }

    private static void moveFocusTo(Window window) {
        robot.waitForIdle();

        boolean focusedByToFront = false;
        try {
            if (window.isFocused()) {
                return;
            }
            window.toFront();

            robot.waitForIdle();

            boolean focused = requestFocusedWindow(window);
            if (!focused) {
                throw new RuntimeException("TEST ERROR: Failed to request a focused window");
            }
        } finally {
            if (window.isFocused() && !focusedByToFront) {
                window.toFront();
                robot.waitForIdle();
            }
        }
    }

    private static boolean verifyFocusGained(Component component) {
        robot.waitForIdle();

        ChildComponentFocusListener fl = componentsToListeners.get(component);
        long endTime = System.currentTimeMillis() + counterTimeout;

        synchronized (fl) {
            try {
                while (System.currentTimeMillis() < endTime && !fl.isGainCalled()) {
                    fl.wait(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("TEST ERROR: interrupted");
            }
        }
        return fl.isGainCalled();
    }

    private static void startFocusTracking(Component comp) {
        ChildComponentFocusListener fl = new ChildComponentFocusListener();
        comp.addFocusListener(fl);
        componentsToListeners.put(comp, fl);
    }

    private static boolean requestFocusedWindow(Window window) {
        robot.waitForIdle();
        if (!window.isFocused()) {
            window.requestFocus();

            robot.waitForIdle();
        }
        return window.isFocused();
    }

    private static class ChildComponentFocusListener implements FocusListener {

        private boolean gained = false;
        private boolean lost = false;
        private FocusEvent focusEvent;

        public synchronized void focusGained(FocusEvent e) {
            gained = true;
            focusEvent = e;
            notifyAll();
        }

        public synchronized void focusLost(FocusEvent e) {
            lost = true;
            focusEvent = e;
            notifyAll();
        }

        public synchronized boolean isGainCalled() {
            return gained;
        }

        public synchronized boolean isLostCalled() {
            return lost;
        }

        public synchronized FocusEvent getFocusEvent() {
            return focusEvent;
        }
    }

}
