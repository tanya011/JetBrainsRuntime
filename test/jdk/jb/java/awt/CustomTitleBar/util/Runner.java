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

package util;

import com.jetbrains.WindowDecorations;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

abstract public class Runner {

    protected WindowDecorations.CustomTitleBar titleBar;
    protected Window window;
    private final String name;
    protected boolean passed = true;

    public Runner(String name) {
        this.name = name;
    }

    public final boolean run(Function<WindowDecorations.CustomTitleBar, Window> windowCreator) {
        System.out.printf("RUN TEST CASE: %s%n", name);
        passed = true;
        prepareTitleBar();
        window = windowCreator.apply(titleBar);
        System.out.println("Created a window with the custom title bar. Window name: " + window.getName());
        customizeWindow();

        window.setVisible(true);
        try {
            test();
        } catch (AWTException e) {
            e.printStackTrace();
            passed = false;
        }

        window.dispose();

        if (passed) {
            System.out.println("TEST CASE PASSED");
        } else {
            System.out.println("TEST CASE FAILED");
        }
        return passed;
    }

    abstract public void prepareTitleBar();

    protected void customizeWindow() {
    }

    abstract public void test() throws AWTException;

}
