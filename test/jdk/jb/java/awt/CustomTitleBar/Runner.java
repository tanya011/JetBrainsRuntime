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

import com.jetbrains.WindowDecorations;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

abstract class Runner {

    protected WindowDecorations.CustomTitleBar titleBar;
    protected Window window;
    private final String name;
    protected boolean passed = true;

    Runner(String name) {
        this.name = name;
    }

    final boolean run(Function<WindowDecorations.CustomTitleBar, Window> windowCreator) {
        passed = true;
        System.out.printf("RUN TEST CASE: %s\n%n", name);
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    prepare();
                    window = windowCreator.apply(titleBar);
                    test();
                } catch (AWTException e) {
                    passed = false;
                    throw new RuntimeException(e);
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            SwingUtilities.invokeLater(() -> window.dispose());
        }
        if (passed) {
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
        }
        return passed;
    }

    abstract void prepare();

    abstract void test() throws AWTException;

}
