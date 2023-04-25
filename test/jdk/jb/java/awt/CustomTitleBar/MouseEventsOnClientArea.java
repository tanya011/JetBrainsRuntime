/*
 * Copyright 2000-2023 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
import com.jetbrains.JBR;
import util.CommonAPISuite;
import util.Task;
import util.TaskResult;
import util.TestUtils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

/*
 * @test
 * @summary Verify mouse events on custom title bar area
 * @requires (os.family == "windows" | os.family == "mac")
 * @run main/othervm MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.0 MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.25 MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.5 MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.0 MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.5 MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=3.0 MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=3.5 MouseEventsOnClientArea
 * @run main/othervm -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=4.0 MouseEventsOnClientArea
 */
public class MouseEventsOnClientArea {

    public static void main(String... args) {
        TaskResult result = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), mouseClicks);

        if (!result.isPassed()) {
            final String message = String.format("%s FAILED. %s", MethodHandles.lookup().lookupClass().getName(), result.getError());
            throw new RuntimeException(message);
        }
    }

    private static final Task mouseClicks = new Task("mouseClicks") {

        private final int[] BUTTON_MASKS = {
                InputEvent.BUTTON1_DOWN_MASK,
                //InputEvent.BUTTON2_DOWN_MASK,
                InputEvent.BUTTON3_DOWN_MASK
        };

        private final Object mouseLock = new Object();

        private static final int PANEL_WIDTH = 400;
        private static final int PANEL_HEIGHT = (int) TestUtils.TITLE_BAR_HEIGHT;

        private final boolean[] buttonsPressed = new boolean[BUTTON_MASKS.length];
        private final boolean[] buttonsReleased = new boolean[BUTTON_MASKS.length];
        private final boolean[] buttonsClicked = new boolean[BUTTON_MASKS.length];

        private Panel panel;

        @Override
        protected void init() {
            Arrays.fill(buttonsPressed, false);
            Arrays.fill(buttonsReleased, false);
            Arrays.fill(buttonsClicked, false);
        }

        @Override
        public void prepareTitleBar() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        protected void customizeWindow() {
            panel = new Panel() {
                @Override
                public void paint(Graphics g) {
                    Rectangle r = g.getClipBounds();
                    g.setColor(Color.CYAN);
                    g.fillRect(r.x, r.y, PANEL_WIDTH, PANEL_HEIGHT);
                    super.paint(g);
                }
            };
            panel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
            panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
            panel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        buttonsClicked[0] = true;
                    } else if (e.getButton() == 3) {
                        buttonsClicked[1] = true;
                    }
//                    if (e.getButton() >= 1 && e.getButton() <= 3) {
//                        buttonsClicked[e.getButton() - 1] = true;
//                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == 1) {
                        buttonsPressed[0] = true;
                    } else if (e.getButton() == 3) {
                        buttonsPressed[1] = true;
                    }
//                    if (e.getButton() >= 1 && e.getButton() <= 3) {
//                        buttonsPressed[e.getButton() - 1] = true;
//                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == 1) {
                        buttonsReleased[0] = true;
                    } else if (e.getButton() == 3) {
                        buttonsReleased[1] = true;
                    }
//                    if (e.getButton() >= 1 && e.getButton() <= 3) {
//                        buttonsReleased[e.getButton() - 1] = true;
//                    }
                }
            });
            window.add(panel);
        }

        @Override
        public void test() throws AWTException {
            Robot robot = new Robot();

//            int x = panel.getLocationOnScreen().x + panel.getWidth() / 2;
//            int y = panel.getLocationOnScreen().y + panel.getHeight() / 2;

            int x = panel.getLocationOnScreen().x + window.getWidth() / 2;
            int y = panel.getLocationOnScreen().y + window.getHeight() / 2;

            for (int i = 0; i < BUTTON_MASKS.length; i++) {
                robot.delay(500);

                robot.mouseMove(x, y);
                robot.mousePress(BUTTON_MASKS[i]);
                robot.mouseRelease(BUTTON_MASKS[i]);
                robot.delay(500);

                if (!buttonsReleased[i]) {
                    synchronized (mouseLock) {
                        try {
                            mouseLock.wait(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            err("Unable to release mouse lock");
                        }
                    }
                }
            }


            boolean status = true;
            for (int i = 0; i < BUTTON_MASKS.length; i++) {
                System.out.println("Button mask: " + BUTTON_MASKS[i]);
                System.out.println("pressed = " + buttonsPressed[i]);
                System.out.println("released = " + buttonsReleased[i]);
                System.out.println("clicked = " + buttonsClicked[i]);
                status = status && buttonsPressed[i] && buttonsReleased[i] && buttonsClicked[i];
            }
            if (!status) {
                err("some of mouse events weren't registered");
            }
        }
    };

}
