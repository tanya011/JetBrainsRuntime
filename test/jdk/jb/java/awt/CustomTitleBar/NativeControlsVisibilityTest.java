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

/*
 * @test
 * @summary Regression test for JET-5124
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main NativeControlsVisibilityTest
 */
public class NativeControlsVisibilityTest {

    public static void main(String... args) {
        boolean status = CommonAPISuite.runTest(TestUtils.getWindowCreationFunctions(), test);

        if (!status) {
            throw new RuntimeException("NativeControlsVisibilityTest FAILED");
        }
    }

    private static final Runner test = new Runner("Hide and show system controls") {

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

}
