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
import util.Rect;
import util.ScreenShotHelpers;
import util.Task;
import util.TestUtils;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.List;

/*
 * @test
 * @summary Verify a property to change visibility of native controls
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main HiddenNativeControlsTest
 */
public class HiddenNativeControlsTest {

    public static void main(String... args) {
        boolean status = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), hiddenControls);

        if (!status) {
            throw new RuntimeException("HiddenNativeControlsTest FAILED");
        }
    }

    private static final Task hiddenControls = new Task("Hidden native controls") {

        private static final String PROPERTY_NAME = "controls.visible";

        @Override
        public void prepareTitleBar() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
            titleBar.putProperty(PROPERTY_NAME, "false");
        }

        @Override
        public void test() throws Exception {
            Robot robot = new Robot();
            robot.delay(1000);
            robot.mouseMove(0, 0);
            robot.delay(1000);

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

            BufferedImage image = ScreenShotHelpers.takeScreenshot(window);

            System.out.println("image w = " + image.getWidth() + " h = " + image.getHeight());

            List<Rect> foundControls = ScreenShotHelpers.detectControls(image, (int) titleBar.getHeight(),
                    (int) titleBar.getLeftInset(), (int) titleBar.getRightInset());
            System.out.println("Found controls at the title bar:");
            foundControls.forEach(System.out::println);

            if (foundControls.size() != 0) {
                passed = false;
                System.out.println("Error: there are must be 0 controls");
            }

            if (!passed) {
                String path = ScreenShotHelpers.storeScreenshot("hidden-controls-test-" + window.getName(), image);
                System.out.println("Screenshot stored in " + path);
            }
        }

    };

}
