/*
 * Copyright 2000-2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.jetbrains.JBR;
import util.CommonAPISuite;
import util.Runner;
import util.TestUtils;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Color;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * @test
 * @summary Regression test for JET-5124
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main HitTest
 */
public class HitTest {

    public static void main(String... args) {
        boolean status = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), hitTestNonClientArea);
        //status = status && CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), hitTestClientArea);


        if (!status) {
            throw new RuntimeException("HitTest FAILED");
        }
    }

    private static final Runner hitTestNonClientArea = new Runner("Hit test non-client area") {

        private boolean gotButton1Click = false;
        private boolean gotButton2Click = false;
        private boolean gotButton3Click = false;

        private Button button;

        @Override
        public void prepareTitleBar() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        public void customizeWindow() {
            System.out.println("custom");
            button = new Button();
            button.setBackground(Color.CYAN);
            button.setSize(80, 40);
            MouseAdapter adapter = new MouseAdapter() {
                private void hit() {
                    titleBar.forceHitTest(false);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("e = " + e.getButton());
                    hit();
                    System.out.println("ex = " + e.getButton());
                    if (e.getButton() == 1) {
                        gotButton1Click = true;
                    }
                    if (e.getButton() == 2) {
                        gotButton2Click = true;
                    }
                    if (e.getButton() == 3) {
                        gotButton3Click = true;
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    hit();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    hit();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hit();
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

            Panel panel = new Panel();
            panel.setBounds(300, 20, 100, 50);
            panel.add(button);
            window.add(panel);
        }

        @Override
        public void test() throws AWTException {
            System.out.println("created");

            Robot robot = new Robot();
            robot.delay(1000);

            robot.mouseMove(button.getLocationOnScreen().x + button.getWidth() / 2,
                    button.getLocationOnScreen().y + button.getHeight() / 2);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            robot.delay(1000);
            robot.mouseMove(button.getLocationOnScreen().x + button.getWidth() / 2,
                    button.getLocationOnScreen().y + button.getHeight() / 2);
            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);

            robot.delay(1000);
            robot.mouseMove(button.getLocationOnScreen().x + button.getWidth() / 2,
                    button.getLocationOnScreen().y + button.getHeight() / 2);
            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);

            robot.delay(1000);

            System.out.println("1 =" + gotButton1Click);
            System.out.println("2 =" + gotButton2Click);
            System.out.println("3 =" + gotButton3Click);

            Point initialLocation = window.getLocationOnScreen();

            robot.delay(500);
            int initialX = button.getLocationOnScreen().x + button.getWidth() / 2;
            int initialY = button.getLocationOnScreen().y + button.getHeight() / 2;
            robot.mouseMove(initialX, initialY);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            for (int i = 0; i < 10; i++) {
                initialX += 3;
                initialY += 3;
                robot.delay(500);
                robot.mouseMove(initialX, initialY);
            }
            robot.delay(500);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Point newLocation = window.getLocationOnScreen();
            boolean moved = initialLocation.x < newLocation.x && initialLocation.y < newLocation.y;

            passed = gotButton1Click && gotButton2Click && gotButton3Click && moved;
        }
    };

    private static final Runner hitTestClientArea = new Runner("Hit test client area") {

        private boolean gotMouseClick = false;
        @Override
        public void prepareTitleBar() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        public void test() throws AWTException {
            Button button = new Button();
            button.setBounds(200, 20, 80, 40);
            button.addMouseListener(new MouseAdapter() {

                private void hit() {
                    titleBar.forceHitTest(true);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    hit();
                    if (e.getButton() == 1) {
                        gotMouseClick = true;
                    }
                }
            });
            window.add(button);

            Robot robot = new Robot();
            robot.delay(1000);
            robot.mouseMove(button.getBounds().x + button.getBounds().width / 2, button.getBounds().y + button.getBounds().height / 2);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            passed = gotMouseClick;
            System.out.println("PASSED!");
        }

    };


}
