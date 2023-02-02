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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/*
 * @test
 * @summary Verify a property to change visibility of native controls
 * @requires (os.family == "windows" | os.family == "mac")
 * @run shell run.sh
 * @run main NativeControlsVisibilityTest
 */
public class NativeControlsVisibilityTest {

    public static void main(String... args) {
        boolean status = CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), hiddenControls);
        status = status && CommonAPISuite.runTestSuite(TestUtils.getWindowCreationFunctions(), visibleControls);

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

//            if (!"true".equals(titleBar.getProperties().get(PROPERTY_NAME).toString())) {
//                passed = false;
//                System.out.println("controls.visible isn't true");
//            }
            if (titleBar.getLeftInset() == 0 && titleBar.getRightInset() == 0) {
                passed = false;
                System.out.println("Left or right inset must be non-zero");
            }

            BufferedImage image = TestHelpers.takeScreenshot(window);

            List<Rect> foundControls = detectControls(image, (int) titleBar.getHeight(), (int) titleBar.getLeftInset(), (int) titleBar.getRightInset());
            System.out.println("Found controls at the title bar:");
            foundControls.forEach(System.out::println);

            if (foundControls.size() != 3) {
                passed = false;
                System.out.println("Error: there are must be 3 controls");
            }

            if (!passed) {
                TestHelpers.storeScreenshot("native-controls-visilibity-test-" + window.getName(), image);
            }
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

            BufferedImage image = TestHelpers.takeScreenshot(window);

            List<Rect> foundControls = detectControls(image, (int) titleBar.getHeight(), (int) titleBar.getLeftInset(), (int) titleBar.getRightInset());
            System.out.println("Found controls at the title bar:");
            foundControls.forEach(System.out::println);

            if (foundControls.size() != 0) {
                passed = false;
                System.out.println("Error: there are must be 0 controls");
            }

            if (!passed) {
                TestHelpers.storeScreenshot("native-controls-visilibity-test-" + window.getName(), image);
            }
        }

    };

    private static List<Rect> detectControls(BufferedImage image, int titleBarHeight, int leftInset, int rightInset) {
        RectCoordinates coords = TestHelpers.findRectangleTitleBar(image, titleBarHeight);

        Map<Color, Rect> map = new HashMap<>();

        System.out.println(coords);

        for (int x = coords.x1(); x <= coords.x2(); x++) {
            for (int y = coords.y1(); y <= coords.y2(); y++) {
                Color color = new Color(image.getRGB(x, y));
                Color adjustedColor = adjustColor(color);
                //int key = colorToInt(adjustedColor);
                Rect rect = map.getOrDefault(adjustedColor, new Rect(adjustedColor));
                rect.addPoint(x, y);
                map.put(adjustedColor, rect);
            }
        }

        int checkedHeight = coords.y2() - coords.y1() + 1;
        int checkedWidth = coords.x2() - coords.x1() + 1;
        int pixels = checkedWidth * checkedHeight;
        int nonCoveredAreaApprox = pixels - (leftInset * checkedHeight + rightInset * checkedHeight);

        List<Rect> rects = map.values().stream().filter(v -> v.getPixelCount() < nonCoveredAreaApprox).toList();
        List<Rect> foundControls = groupRects(rects);

        return foundControls;
    }

    private static List<Rect> groupRects(List<Rect> rects) {
        rects.forEach(System.out::println);
        List<Rect> found = new ArrayList<>();

        List<Rect> items = new ArrayList<>(rects);
        while (!items.isEmpty()) {
            AtomicReference<Rect> rect = new AtomicReference<>(items.remove(0));

            List<Rect> restItems = new ArrayList<>();
            items.forEach(item -> {
                Rect intersected = intersect(rect.get(), item);
                if (intersected != null) {
                    rect.set(intersected);
                } else {
                    restItems.add(item);
                }
            });
            found.add(rect.get());
            items = restItems;
        }

        return found;
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

        Color commonColor = r1.getPixelCount() > r2.getPixelCount() ? r1.getCommonColor() : r2.getCommonColor();

        return new Rect(x1, y1, x2, y2, r1.getPixelCount() + r2.getPixelCount(), commonColor);
    }

}
