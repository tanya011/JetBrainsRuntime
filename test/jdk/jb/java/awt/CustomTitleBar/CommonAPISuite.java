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
import com.jetbrains.WindowDecorations;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class CommonAPISuite {

    static boolean runTestSuite(Function<WindowDecorations.CustomTitleBar, Window> windowCreator) {
        final List<Runner> tests = List.of(
                defaultTitleBar,
                hiddenSystemControls,
                changeTitleBarHeight,
                hitTest,
                nativeControlsVisibility,
                actionListener
        );

        AtomicBoolean testsSuitePassed = new AtomicBoolean(true);
        tests.forEach(test -> testsSuitePassed.set(testsSuitePassed.get() && test.run(TestUtils::createDialogWithCustomTitleBar)));

        return testsSuitePassed.get();
    }

    private static final Runner defaultTitleBar = new Runner("Create title bar with default settings") {

        @Override
        void prepare() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        public void test() {
            passed = passed && TestUtils.checkTitleBarHeight(titleBar, TestUtils.TITLE_BAR_HEIGHT);
            passed = passed && TestUtils.checkFrameInsets(window);

            if (titleBar.getLeftInset() == 0 && titleBar.getRightInset() == 0) {
                passed = false;
                System.out.println("Left or right space must be occupied by system controls");
            }
        }
    };

    private static final Runner hiddenSystemControls = new Runner("Hide system controls") {

        private static final String PROPERTY_NAME = "controls.visible";

        @Override
        void prepare() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
            titleBar.putProperty(PROPERTY_NAME, "false");
        }

        @Override
        void test()  {
            passed = passed && TestUtils.checkTitleBarHeight(titleBar, TestUtils.TITLE_BAR_HEIGHT);
            passed = passed && TestUtils.checkFrameInsets(window);

            if (!"false".equals(titleBar.getProperties().get(PROPERTY_NAME).toString())) {
                passed = false;
                System.out.println("controls.visible isn't false");
            }
            if (titleBar.getLeftInset() != 0 || titleBar.getRightInset() != 0) {
                passed = false;
                System.out.println("System controls are hidden so insets must be zero");
            }
        }
    };

    private static final Runner changeTitleBarHeight = new Runner("Changing of title bar height") {

        private final float initialHeight = 50;

        @Override
        void prepare() {
            WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(initialHeight);
        }

        @Override
        void test() {
            window = TestUtils.createDialogWithCustomTitleBar(titleBar);
            passed = passed && TestUtils.checkTitleBarHeight(titleBar, initialHeight);

            float newHeight = 100;
            titleBar.setHeight(newHeight);
            passed = passed && TestUtils.checkTitleBarHeight(titleBar, newHeight);
        }
    };

    private static final Runner hitTest = new Runner("Hit test") {

        private boolean gotMouseClick = false;

        @Override
        void prepare() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        void test() {
            Button button = new Button();
            button.setBounds(200, 20, 80, 40);
            button.addMouseListener(new MouseAdapter() {

                private void hit() {
                    titleBar.forceHitTest(false);
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

            try {
                Robot robot = new Robot();
                robot.delay(1000);
                robot.mouseMove(button.getBounds().x + button.getBounds().width / 2, button.getBounds().y + button.getBounds().height / 2);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                passed = !gotMouseClick;
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private static final Runner nativeControlsVisibility = new Runner("Native controls visibility") {

        @Override
        void prepare() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        void test() throws AWTException {
            Button button = new Button();
            button.setBounds(200, 20, 50, 50);
            button.addActionListener(e -> titleBar.putProperty("controls.visible", !(boolean)titleBar.getProperties().getOrDefault("controls.visible", true)));
            window.add(button);

            Robot robot = new Robot();
            robot.delay(1000);
            robot.mouseMove(button.getBounds().x + button.getBounds().width / 2, button.getBounds().y + button.getBounds().height / 2);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(1000);

            if (titleBar.getRightInset() != 0 && titleBar.getLeftInset() != 0) {
                passed = false;
                System.out.println("Native controls must be hidden after clicking the button");
            }
        }
    };

    private static final Runner actionListener = new Runner("Using of action listener") {

        @Override
        void prepare() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        void test() throws AWTException {
            Button button = new Button();
            button.setBounds(200, 20, 50, 50);
            button.addActionListener(a -> {
                System.out.println("Action listener got event");
            });
            window.add(button);

            final int initialHeight = window.getHeight();
            final int initialWidth = window.getWidth();

            Robot robot = new Robot();
            robot.delay(1000);
            robot.mouseMove(button.getBounds().x + button.getBounds().width / 2, button.getBounds().y + button.getBounds().height / 2);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(1000);

            if (window.getHeight() != initialHeight || window.getWidth() != initialWidth) {
                passed = false;
                System.out.println("Adding of action listener should block native title bar behavior");
            }
        }
    };

}
