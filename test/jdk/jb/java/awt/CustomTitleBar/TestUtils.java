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

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.List;
import java.util.function.Function;

public class TestUtils {

    static final float TITLE_BAR_HEIGHT = 100;
    static final Color TITLE_BAR_COLOR = Color.BLUE;

    private static List<Function<WindowDecorations.CustomTitleBar, Window>> windowCreationFunctions = List.of(
            TestUtils::createDialogWithCustomTitleBar,
            TestUtils::createFrameWithCustomTitleBar,
            TestUtils::createJDialogWithCustomTitleBar,
            TestUtils::createJDialogWithCustomTitleBar
    );

    static boolean checkTitleBarHeight(WindowDecorations.CustomTitleBar titleBar, float expected) {
        if (titleBar.getHeight() != expected) {
            System.out.printf(String.format("Wrong title bar height. Actual = %f, expected = %d\n", titleBar.getHeight(), expected));
            return false;
        }
        return true;
    }

    static boolean checkFrameInsets(Window window) {
        Insets insets = window.getInsets();
        if (!(insets.top == 0 && insets.right == 0 && insets.left == 0 && insets.bottom == 0)) {
            System.out.println("Frame insets must be zero, but got " + insets);
            return false;
        }
        return true;
    }

    static List<Function<WindowDecorations.CustomTitleBar, Window>> getWindowCreationFunctions() {
        return windowCreationFunctions;
    }

    static Frame createFrameWithCustomTitleBar(WindowDecorations.CustomTitleBar titleBar) {
        Frame frame = new Frame(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(TITLE_BAR_COLOR);
                g.fillRect(r.x, r.y, r.width, (int) TITLE_BAR_HEIGHT);
                super.paint(g);
            }
        };
        frame.setTitle("Frame");
        frame.setBounds(200, 400, 1000, 200);

        JBR.getWindowDecorations().setCustomTitleBar(frame, titleBar);

        frame.setLayout(null);
        frame.setVisible(true);

        return frame;
    }

    static Frame createJFrameWithCustomTitleBar(WindowDecorations.CustomTitleBar titleBar) {
        JFrame frame = new JFrame(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(TITLE_BAR_COLOR);
                g.fillRect(r.x, r.y, r.width, (int) TITLE_BAR_HEIGHT);
                super.paint(g);
            }
        };
        frame.setTitle("JFrame");
        frame.setBounds(200, 400, 1000, 200);

        JBR.getWindowDecorations().setCustomTitleBar(frame, titleBar);

        frame.setLayout(null);
        frame.setVisible(true);

        return frame;
    }

    static Dialog createDialogWithCustomTitleBar(WindowDecorations.CustomTitleBar titleBar) {
        Dialog dialog = new Dialog((Frame) null){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(TITLE_BAR_COLOR);
                g.fillRect(r.x, r.y, r.width, (int) TITLE_BAR_HEIGHT);
                super.paint(g);
            }
        };
        dialog.setTitle("Dialog");
        dialog.setBounds(200, 400, 1000, 200);

        JBR.getWindowDecorations().setCustomTitleBar(dialog, titleBar);

        dialog.setLayout(null);
        dialog.setVisible(true);

        return dialog;
    }

    static JDialog createJDialogWithCustomTitleBar(WindowDecorations.CustomTitleBar titleBar) {
        JDialog dialog = new JDialog((Frame) null){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(TITLE_BAR_COLOR);
                g.fillRect(r.x, r.y, r.width, (int) TITLE_BAR_HEIGHT);
                super.paint(g);
            }
        };
        dialog.setTitle("JDialog");
        dialog.setBounds(200, 400, 1000, 200);

        JBR.getWindowDecorations().setCustomTitleBar(dialog, titleBar);

        dialog.setLayout(null);
        dialog.setVisible(true);

        return dialog;
    }

}
