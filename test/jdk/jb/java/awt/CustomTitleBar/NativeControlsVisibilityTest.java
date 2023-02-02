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
import util.RectCoordinates;
import util.Task;
import util.TestHelpers;
import util.TestUtils;

import java.awt.Color;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * @test
 * @summary Verify a property to change visibility of native controls
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main NativeControlsVisibilityTest
 */
public class NativeControlsVisibilityTest {

    public static void main(String... args) {
        //boolean status = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), hiddenControls);
        boolean status = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), visibleControls);

        if (!status) {
            throw new RuntimeException("NativeControlsVisibilityTest FAILED");
        }
    }

    private static final Task visibleControls = new Task("Visible native controls") {
        private static final String PROPERTY_NAME = "controls.visible";
        @Override
        public void prepareTitleBar() {
            titleBar = JBR.getWindowDecorations().createCustomTitleBar();
            titleBar.setHeight(TestUtils.TITLE_BAR_HEIGHT);
        }

        @Override
        public void test() throws Exception {
            Robot robot = new Robot();
            robot.delay(1000);

            passed = passed && TestUtils.checkTitleBarHeight(titleBar, TestUtils.TITLE_BAR_HEIGHT);
            passed = passed && TestUtils.checkFrameInsets(window);

            System.out.println(titleBar.getProperties().get(PROPERTY_NAME));

//            if (!"true".equals(titleBar.getProperties().get(PROPERTY_NAME).toString())) {
//                passed = false;
//                System.out.println("controls.visible isn't true");
//            }
//            if (titleBar.getLeftInset() == 0 && titleBar.getRightInset() == 0) {
//                passed = false;
//                System.out.println("Left or right inset must be non-zero");
//            }

            BufferedImage image = TestHelpers.takeScreenshot(window);

            //RectCoordinates coords = TestHelpers.findRectangleTitleBar(image, (int) titleBar.getHeight());
            detectControls(image, (int) titleBar.getHeight());
            TestHelpers.storeScreenshot("visible", image);
        }
    };

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

    private static void detectControls(BufferedImage image, int titleBarHeight) {
        RectCoordinates coords = TestHelpers.findRectangleTitleBar(image, titleBarHeight);

        Map<Integer, Rect> map = new HashMap<>();

        for (int x = coords.x1(); x <= coords.x2(); x++) {
            for (int y = coords.y1(); y <= coords.y2(); y++) {
                Color color = new Color(image.getRGB(x, y));
                Color adjustedColor = adjustColor(color);
                int key = colorToInt(adjustedColor);
                Rect rect = map.getOrDefault(key, new Rect());
                rect.addPoint(x, y);
                map.put(key, rect);
            }
        }


        map.forEach((k, v) -> {
            System.out.println("COLOR: " + k);
            System.out.println(v.toString());
        });

        Map<Color, Rect> areas = new HashMap<>();

        map.keySet().forEach(key1 -> {
            if (map.containsKey(key1)) {
                Rect value1 = map.get(key1);
                map.keySet().forEach(key2 -> {
                    Rect value2 = map.get(key2);
                });
            }
        });

    }

    private static int colorToInt(Color color) {
        return color.getRed() * 256 * 256 + color.getGreen() * 256 + color.getBlue();
    }

    private static Color adjustColor(Color color) {
        int r = adjustValue(color.getRed());
        int g = adjustValue(color.getGreen());
        int b = adjustValue(color.getBlue());
        return new Color(r, g, b);
    }

    private static int adjustValue(int value) {
        final int round = 64;
        int div = (value + 1) / round;
        int mod = (value + 1) % round;
        int result = div > 0 ? round * div - 1 : 0;
        if (mod > 32) result += round;
        return result;
    }

    private static Rect intersect(Rect r1, Rect r2) {
        int x1 = -1, x2 = -1, y1 = -1, y2 = -1;
        if (r1.getX1() <= r2.getX1() && r2.getX1() <= r1.getX2()) {
            x1 = r1.getX1();
            x2 = Math.max(r2.getX2(), r1.getX2());
        }
        if (r2.getX1() <= r1.getX1() && r1.getX1() <= r2.getX2()) {
            x1 = r2.getX1();
            x2 = Math.max(r2.getX2(), r1.getX2());
        }

        if (r1.getY1() < r2.getY1() && r2.getY1() <= r2.getY1()) {
            y1 = r1.getY1();
            y2 = Math.max(r1.getY2(), r2.getY2());
        }
        if (r2.getY1() <= r1.getY1() && r1.getY1() <= r2.getY2()) {
            y1 = r2.getY1();
            y2 = Math.max(r1.getY2(), r2.getY2());
        }
        if (x1 == -1 || x2 == -1 || y1 == -1 || y2 == -1) {
            return null;
        }
        return new Rect(x1, y1, x2, y2, r1.getPixelCount() + r2.getPixelCount());
    }

}
