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

import java.util.ArrayList;
import java.util.List;

/*
 * @test
 * @summary Regression test for JET-5124
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=false
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.0
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.25
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=1.5
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.0
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.5
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=3.0
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=3.5
 * @run main CommonAPITest -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=4.0
 */
public class CommonAPITest {

    private static boolean testsSuitePassed = true;

    public static void main(String... args) {

        List<Runner> tests = new ArrayList<>();
        tests.add(defaultTitleBar);
        tests.add(hiddenSystemControls);
        tests.add(changeTitleBarHeight);

        tests.forEach(test -> testsSuitePassed = testsSuitePassed && test.run());

        if (testsSuitePassed) {
            System.out.println("CommonAPITest PASSED");
        } else {
            throw new RuntimeException("CommonAPITest FAILED");
        }
    }

    private static final Runner defaultTitleBar = new Runner("Create title bar with default settings") {
        @Override
        public void test() {
            WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
            window = TestUtils.createFrameWithCustomTitleBar(titleBar);

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
        void test() {
            WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
            titleBar.putProperty(PROPERTY_NAME, "false");
            window = TestUtils.createFrameWithCustomTitleBar(titleBar);

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

//            titleBar.putProperty(PROPERTY_NAME, "true");
//            if (!"true".equals(titleBar.getProperties().get(PROPERTY_NAME).toString())) {
//                passed = false;
//                System.out.println("controls.visible isn't true");
//            }
//
//            System.out.println("l = " + titleBar.getLeftInset() + " r = " + titleBar.getRightInset());
//            System.out.println("f top = " + window.getInsets().top);
//            if (titleBar.getLeftInset() == 0 && titleBar.getRightInset() == 0) {
//                passed = false;
//                System.out.println("System controls are visible so there are must be non-zero inset");
//            }
        }
    };

    private static final Runner changeTitleBarHeight = new Runner("Changing of title bar height") {
        @Override
        void test() {
            float initialHeight = 50;
            float newHeight = 100;

            WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(initialHeight);
            window = TestUtils.createFrameWithCustomTitleBar(titleBar);

            passed = passed && TestUtils.checkTitleBarHeight(titleBar, initialHeight);

            titleBar.setHeight(newHeight);

            passed = passed && TestUtils.checkTitleBarHeight(titleBar, newHeight);
        }
    };

}